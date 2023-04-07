package IntList;

import static org.junit.Assert.*;
import org.junit.Test;

public class SquarePrimesTest {

    /**
     * Here is a test for isPrime method. Try running it.
     * It passes, but the starter code implementation of isPrime
     * is broken. Write your own JUnit Test to try to uncover the bug!
     */
    @Test
    public void testSquarePrimesSimple() {
        IntList lst0 = IntList.of(14, 15, 16, 17, 18);
        boolean changed0 = IntListExercises.squarePrimes(lst0);
        assertEquals("14 -> 15 -> 16 -> 289 -> 18", lst0.toString());
        assertTrue(changed0);

        IntList lst1 = IntList.of(2, 3, 16, 17, 18);
        boolean changed1 = IntListExercises.squarePrimes(lst1);
        assertEquals("4 -> 9 -> 16 -> 289 -> 18", lst1.toString());
        assertTrue(changed1);

        IntList lst2 = IntList.of(2, 2, 2, 2, 2);
        boolean changed2 = IntListExercises.squarePrimes(lst2);
        assertEquals("4 -> 4 -> 4 -> 4 -> 4", lst2.toString());
        assertTrue(changed2);

    }
}
