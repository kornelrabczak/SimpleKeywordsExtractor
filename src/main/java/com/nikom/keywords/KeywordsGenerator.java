package com.nikom.keywords;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Guesses keywords from an input string, based on the frequency of the words.
 *
 * @see <a href="http://lucene.apache.org/">http://lucene.apache.org/</a>
 */
public class KeywordsGenerator {

    /** Lucene version. */
    private static Version LUCENE_VERSION = Version.LUCENE_36;

    /**
     * Stemmize the given term.
     *
     * @param term The term to stem.
     * @return The stem of the given term.
     * @throws IOException If an I/O error occured.
     */
    private static String stemmize(String term) throws IOException {

        // tokenize term
        TokenStream tokenStream = new ClassicTokenizer(LUCENE_VERSION, new StringReader(term));
        // stemmize
        tokenStream = new PorterStemFilter(tokenStream);

        Set<String> stems = new HashSet<String>();
        CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);
        // for each token
        while (tokenStream.incrementToken()) {
            // add it in the dedicated set (to keep unicity)
            stems.add(token.toString());
        }

        // if no stem or 2+ stems have been found, return null
        if (stems.size() != 1) {
            return null;
        }

        String stem = stems.iterator().next();

        // if the stem has non-alphanumerical chars, return null
        if (!stem.matches("[\\w-]+")) {
            return null;
        }

        return stem;
    }

    /**
     * Tries to find the given example within the given collection. If it hasn't been found, the
     * example is automatically added in the collection and is then returned.
     *
     * @param collection The collection to search into.
     * @param example The example to search.
     * @return The existing element if it has been found, the given example otherwise.
     */
    private static <T> T find(Collection<T> collection, T example) {
        for (T element : collection) {
            if (element.equals(example)) {
                return element;
            }
        }
        collection.add(example);
        return example;
    }

    /**
     * Extracts text content from the given URL and guesses keywords within it (needs jsoup parser).
     *
     * @param The URL to read.
     * @return A set of potential keywords. The first keyword is the most frequent one, the last the
     *         least frequent.
     * @throws IOException If an I/O error occured.
     * @see <a href="http://jsoup.org/">http://jsoup.org/</a>
     */
    public static List<Keyword> guessFromUrl(String url) throws IOException {
        // get textual content from url
        Document doc = Jsoup.connect(url).get();
        String content = doc.body().text();
        // guess keywords from this content
        return guessFromString(content);
    }

    /**
     * Guesses keywords from given input string.
     *
     * @param input The input string.
     * @return A set of potential keywords. The first keyword is the most frequent one, the last the
     *         least frequent.
     * @throws IOException If an I/O error occured.
     */
    public static List<Keyword> guessFromString(String input) throws IOException {

        // hack to keep dashed words (e.g. "non-specific" rather than "non" and "specific")
        input = input.replaceAll("-+", "-0");
        // replace any punctuation char but dashes and apostrophes and by a space
        input = input.replaceAll("[\\p{Punct}&&[^'-]]+", " ");
        // replace most common english contractions
        input = input.replaceAll("(?:'(?:[tdsm]|[vr]e|ll))+\\b", "");

        // tokenize input
        TokenStream tokenStream = new ClassicTokenizer(LUCENE_VERSION, new StringReader(input));
        // to lower case
        tokenStream = new LowerCaseFilter(LUCENE_VERSION, tokenStream);
        // remove dots from acronyms (and "'s" but already done manually above)
        tokenStream = new ClassicFilter(tokenStream);
        // convert any char to ASCII
        tokenStream = new ASCIIFoldingFilter(tokenStream);
        // remove english stop words
        tokenStream = new StopFilter(LUCENE_VERSION, tokenStream, EnglishAnalyzer.getDefaultStopSet());

        List<Keyword> keywords = new LinkedList<Keyword>();
        CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);

        // for each token
        while (tokenStream.incrementToken()) {
            String term = token.toString();
            // stemmize
            String stem = stemmize(term);
            if (stem != null) {
                // create the keyword or get the existing one if any
                Keyword keyword = find(keywords, new Keyword(stem.replaceAll("-0", "-")));
                // add its corresponding initial token
                keyword.add(term.replaceAll("-0", "-"));
            }
        }

        // reverse sort by frequency
        Collections.sort(keywords);

        return keywords;
    }

}