package com.nikom.keywords;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Keyword implements Comparable<Keyword> {

        /**
         * The unique stem.
         */
        private String stem;

        /**
         * The frequency of the stem.
         */
        private Integer frequency;

        /**
         * The found corresponding terms for this stem.
         */
        private Set<String> terms;

        /**
         * Unique constructor.
         *
         * @param stem The unique stem this instance must hold.
         */
        public Keyword(String stem) {
            this.stem = stem;
            terms = new HashSet<String>();
            frequency = 0;
        }

        /**
         * Add a found corresponding term for this stem. If this term has been already found, it
         * won't be duplicated but the stem frequency will still be incremented.
         *
         * @param term The term to add.
         */
        public void add(String term) {
            terms.add(term);
            frequency++;
        }

        /**
         * Gets the unique stem of this instance.
         *
         * @return The unique stem.
         */
        public String getStem() {
            return stem;
        }

        /**
         * Gets the frequency of this stem.
         *
         * @return The frequency.
         */
        public Integer getFrequency() {
            return frequency;
        }

        /**
         * Gets the list of found corresponding terms for this stem.
         *
         * @return The list of found corresponding terms.
         */
        public Set<String> getTerms() {
            return terms;
        }

        /**
         * Used to reverse sort a list of keywords based on their frequency (from the most frequent
         * keyword to the least frequent one).
         */
        @Override
        public int compareTo(Keyword o) {
            return o.frequency.compareTo(frequency);
        }

        /**
         * Used to keep unicity between two keywords: only their respective stems are taken into
         * account.
         */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof Keyword && obj.hashCode() == hashCode();
        }

        /**
         * Used to keep unicity between two keywords: only their respective stems are taken into
         * account.
         */
        @Override
        public int hashCode() {
            return Arrays.hashCode(new Object[]{stem});
        }

        /**
         * User-readable representation of a keyword: "[stem] x[frequency]".
         */
        @Override
        public String toString() {
            return stem + " x" + frequency;
        }

}