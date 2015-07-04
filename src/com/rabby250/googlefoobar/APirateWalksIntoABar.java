package com.rabby250.googlefoobar;

import java.util.HashMap;

public class APirateWalksIntoABar {

    /*
     * For some reason (luck?) I ran into this on Level 5,
     * while my colleague solved it on Level 1. Hmm.
     */

    public static int answer(int[] numbers) {
        final HashMap<Integer, Integer> map = new HashMap<>();
        int i = 0, j = 0;
        map.put(j, i);
        while(map.size() <= numbers.length) {
            i++;
            j = numbers[j];
            if (map.containsKey(j)) {
                return i - map.get(j);
            }
            map.put(j, i);
        }
        return 0;
    }

    /*
     * The following code solves the problem
     * without any extra heap allocations.
     *
     * TODO: verify it on Foobar
     */

    // bit 14 ~ 26: first visited
    private static final int BITS = 14;
    // bit 13: flag (visited or not)
    private static final int FLAG = 1 << (BITS - 1);
    // bit 0 ~ 12: next (max 8191 > 5000)
    private static final int MASK = FLAG - 1;

    public static int answer2(int[] numbers) {
        int next = 0;
        for (int pos = 0; pos < numbers.length; pos++) {
            final int current = next;
            next = numbers[current];
            if (next > MASK) {
                return pos - (next >>> BITS);
            }
            next &= MASK;
            numbers[current] = (pos << BITS) | FLAG | next;
        }
        return 0;
    }
}
