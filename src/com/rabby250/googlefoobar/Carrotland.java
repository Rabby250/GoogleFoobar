package com.rabby250.googlefoobar;

public class Carrotland {

    /*
     * Pick's theorem ftw!
     *
     * The only question was that the coordinates may be too large
     * to be handled as an integer. Let's do the math in long.
     */

    public static int answer(int[][] vertices) {

        final long
                x0 = vertices[0][0],
                x1 = vertices[1][0],
                x2 = vertices[2][0],
                y0 = vertices[0][1],
                y1 = vertices[1][1],
                y2 = vertices[2][1];

        long area
                = x0 * y1 - x0 * y2
                + x1 * y2 - x1 * y0
                + x2 * y0 - x2 * y1;
        if (area < 0) {
            area = -area;
        }

        final long border
                = gcd(x0 - x1, y0 - y1)
                + gcd(x1 - x2, y1 - y2)
                + gcd(x2 - x0, y2 - y0);

        return (int) ((area + 2 - border) / 2);
    }

    private static long gcd(long a, long b) {
        if (a < 0) {
            a = -a;
        } else if (a == 0) {
            return b;
        }
        if (b < 0) {
            b = -b;
        } else if (b == 0) {
            return a;
        }
        while (true) {
            if (a < b) {
                long temp = a;
                a = b;
                b = temp;
            }
            a = a % b;
            if (a == 0) {
                return b;
            }
        }
    }
}
