package com.rabby250.googlefoobar;

public class GuardGame {
    /*
     * The answer to this challenge was called "Digital Root":
     * https://en.wikipedia.org/wiki/Digital_root
     *
     * It could be solved easily by input % 9
     * (and handle the 9s correctly).
     */
    public static int answer(final int number) {
        final int result = number % 9;
        if (result != 0) {
            return result;
        }
        return number != 0 ? 9 : 0;
    }
}
