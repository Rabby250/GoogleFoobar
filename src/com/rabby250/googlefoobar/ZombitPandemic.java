package com.rabby250.googlefoobar;

import java.math.BigInteger;
import java.util.HashMap;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

public class ZombitPandemic {

    // Basically the strategy for solving this was very similar
    // to Undercover Underground - you solve one of them,
    // and you will solve both

    private static final BigInteger EIGHT = BigInteger.valueOf(8);

    // Cache for nChooseR()
    private static final HashMap<Integer, BigInteger>
            NCR_CACHE = new HashMap<>();
    // Cache for allGraphs()
    private static final HashMap<Integer, BigInteger>
            ALL_GRAPHS_CACHE = new HashMap<>();
    // Cache for connectedGraphs()
    private static final HashMap<Integer, BigInteger>
            CONN_GRAPHS_CACHE = new HashMap<>();
    // Cache for restrictedGraphs()
    private static final HashMap<Integer, BigInteger>
            REST_GRAPHS_CACHE = new HashMap<>();

    public static String answer(final int n) {
        BigInteger dividend = connectedGraphs(n)
                .multiply(BigInteger.valueOf(n));
        for (int i = 2; i <= n - 2; i ++) {
            dividend = dividend.add(restrictedGraphs(n, i)
                    .subtract(restrictedGraphs(n, i - 1))
                    .multiply(BigInteger.valueOf(i)));
        }
        final BigInteger divisor = allGraphs(n);
        final BigInteger gcd = dividend.gcd(divisor);
        return dividend.divide(gcd).toString() + "/"
                + divisor.divide(gcd).toString();
    }

    private static BigInteger nChooseR(final int n, final int r) {
        if (n < 0 | r < 0 | r > n) {
            return ZERO;
        } else if (r == 0 || r == n) {
            return ONE;
        }
        final int key = n << 16 | r;
        if (NCR_CACHE.containsKey(key)) {
            return NCR_CACHE.get(key);
        }
        BigInteger result;
        if (r == 1 || r == n - 1) {
            result =  BigInteger.valueOf(n);
        } else {
            result = ONE;
            for (int i = 0; i < r; i++) {
                result = result
                        .multiply(BigInteger.valueOf(n - i))
                        .divide(BigInteger.valueOf(i + 1));
            }
        }
        NCR_CACHE.put(key, result);
        return result;
    }

    // Calculates the number of possible graphs given v vertices
    // (which is actually (v - 1) ^ v)
    private static BigInteger allGraphs(final int v) {
        switch (v) {
            case 0:
            case 1:
                return ZERO;
            case 2:
                return ONE;
            case 3:
                return EIGHT;
            default:
        }
        if (ALL_GRAPHS_CACHE.containsKey(v)) {
            return ALL_GRAPHS_CACHE.get(v);
        }
        final BigInteger result = BigInteger.valueOf(v - 1).pow(v);
        ALL_GRAPHS_CACHE.put(v, result);
        return result;
    }

    // Calculates the number of connected graphs with v vertices
    // (also known as the OEIS A000435 sequence)
    private static BigInteger connectedGraphs(final int v) {
        switch (v) {
            case 0:
            case 1:
                return ZERO;
            case 2:
                return ONE;
            case 3:
                return EIGHT;
            default:
        }
        if (CONN_GRAPHS_CACHE.containsKey(v)) {
            return CONN_GRAPHS_CACHE.get(v);
        }
        BigInteger result = allGraphs(v);
        for (int i = 2; i <= v - 2; i++) {
            result = result.subtract(nChooseR(v - 1, i - 1)
                    .multiply(connectedGraphs(i))
                    .multiply(allGraphs(v - i)));
        }
        CONN_GRAPHS_CACHE.put(v, result);
        return result;
    }

    // Given v vertices, calculates the number of graphs whose largest
    // connected component(s) contain no more than maxSize vertices.
    private static BigInteger restrictedGraphs(
            final int v, int maxSize) {
        if (maxSize >= v) {
            return allGraphs(v);
        } else if (maxSize == v - 1) {
            maxSize--;
        }
        final int key = v << 16 | maxSize;
        if (REST_GRAPHS_CACHE.containsKey(key)) {
            return REST_GRAPHS_CACHE.get(key);
        }
        BigInteger result = ZERO;
        for (int i = 2; i <= maxSize; i++) {
            result = result.add(nChooseR(v - 1, i - 1)
                    .multiply(connectedGraphs(i))
                    .multiply(restrictedGraphs(v - i, maxSize)));
        }
        REST_GRAPHS_CACHE.put(key, result);
        return result;
    }
}
