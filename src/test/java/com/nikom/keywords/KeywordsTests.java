package com.nikom.keywords;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * @author nikom
 */
@RunWith(MockitoJUnitRunner.class)
public class KeywordsTests {


    @Test
    public void testingMethod() {


        try {
            List<Keyword> result = KeywordsGenerator.guessFromUrl("http://uk.ign.com/articles/2013/09/30/100-little-things-in-gta-5-that-will-blow-your-mind");
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }




    }


}