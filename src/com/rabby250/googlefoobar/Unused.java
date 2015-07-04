package com.rabby250.googlefoobar;

import java.math.BigInteger;
import java.util.HashMap;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

public class Unused {
    private static class GridZero {
        private static int[][] createLinearSystem(final int[][] input) {
            final int n = input.length;
            final int nSquare = n * n;
            final int[][] system = new int[nSquare][nSquare + 1];

            for (int index = 0; index < nSquare; index++) {
                int[] equation = system[index];
                // Original (i, j) in input
                final int i = index / n, j = index % n;
                // Set s(i, ..)
                for (int row = i * n; row < (i + 1) * n; row++) {
                    equation[row] = 1;
                }
                // Set s(.., j)
                for (int column = j; column < nSquare; column += n) {
                    equation[column] = 1;
                }
                // Set initial state
                equation[nSquare] = input[i][j];
            }

            return system;
        }

        /*
         * Fast algorithm to count the number of set bits in i
         * (see http://stackoverflow.com/questions/109023/ )
         */
        private static int popCount(int i) {
            i -= ((i >>> 1) & 0x55555555);
            i = (i & 0x33333333) + ((i >>> 2) & 0x33333333);
            return (((i + (i >>> 4)) & 0x0F0F0F0F) * 0x01010101) >>> 24;
        }
    }

    private static class ZombitPandemic {
        private static final BigInteger FOUR = BigInteger.valueOf(4);
        private static final BigInteger
                SEVENTY_TWO = BigInteger.valueOf(72);

        // Cache for overallSum()
        private static final HashMap<Integer, BigInteger>
                OVERALL_SUM_CACHE = new HashMap<>();
        /*
        public static String answer(final int n) {
            final BigInteger dividend = overallSum(n);
            final BigInteger divisor = allGraphs(n).multiply(
                    BigInteger.valueOf(n));
            final BigInteger gcd = dividend.gcd(divisor);
            return dividend.divide(gcd).toString() + "/"
                    + divisor.divide(gcd).toString();
        }
        */

        /*
        private static BigInteger overallSum(final int v) {
            switch (v) {
                case 0:
                    return ZERO;
                case 1:
                    return ONE;
                case 2:
                    return FOUR;
                case 3:
                    return SEVENTY_TWO;
                default:
            }
            if (OVERALL_SUM_CACHE.containsKey(v)) {
                return OVERALL_SUM_CACHE.get(v);
            }
            BigInteger result = connectedGraphs(v)
                    .multiply(BigInteger.valueOf(v * v));
            for (int i = 2; i <= v - 2; i++) {
                result = result.add(nChooseR(v - 1, i - 1)
                        .multiply(connectedGraphs(i))
                        .multiply(allGraphs(v - i)
                                .multiply(BigInteger.valueOf(i * i))
                                .add(overallSum(v - i))));
            }
            OVERALL_SUM_CACHE.put(v, result);
            return result;
        }
        */
    }
}
