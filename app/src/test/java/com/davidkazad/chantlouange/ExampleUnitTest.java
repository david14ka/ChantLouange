package com.davidkazad.chantlouange;

import com.davidkazad.chantlouange.songs.NW;
import com.davidkazad.chantlouange.songs.SongsBook;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void man(){

        NW nw = new NW();
        //List<String> strings = Arrays.asList(Arrays.sort(nw.getTitle());

        String array[] = nw.getTitle();
        //Arrays.sort(array, CC.reverseOrder());
        Arrays.sort(array);

        System.out.println("Tableau trié\n");
        int i = 1;
        for (String entier : array) {
            System.out.println(i+". "+entier+"");
            i++;
        }
    }
    @Test
    public void man2(){

        NW nw = new NW();
        //List<String> strings = Arrays.asList(Arrays.sort(nw.getTitle());

        String array[] = nw.getTitle();
        int search = Arrays.binarySearch(array,"yesu");

        System.out.println("Tableau trié "+search);
        int i=1;
        for (String entier : array) {
            System.out.println(i+". "+entier);
            i++;
        }
    }
}