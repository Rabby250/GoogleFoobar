package com.rabby250.googlefoobar;

import java.math.BigInteger;
import java.util.HashMap;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

public class UndercoverUnderground {

    // Cache for nChooseR()
    private static final HashMap<Integer, BigInteger>
            NCR_CACHE = new HashMap<>();
    // Cache for allGraphs()
    private static final HashMap<Integer, BigInteger>
            ALL_GRAPHS_CACHE = new HashMap<>();
    // Cache for connectedGraphs()
    private static final HashMap<Integer, BigInteger>
            CONN_GRAPHS_CACHE = new HashMap<>();

    public static String answer(final int w, final int t) {
        return connectedGraphs(w, t).toString();
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

    // Calculate the number of possible undirected graphs
    // given v vertices and e edges
    private static BigInteger allGraphs(final int v, final int e) {
        if (v == 0) {
            return ZERO;
        }
        final int key = v << 16 | e;
        if (ALL_GRAPHS_CACHE.containsKey(key)) {
            return ALL_GRAPHS_CACHE.get(key);
        }
        // possible graphs = choose e edges from all possible edges
        // (edges that form a v-vertex complete graph)
        final int allE = v * (v - 1) >> 1;
        final BigInteger result = nChooseR(allE, e);
        ALL_GRAPHS_CACHE.put(key, result);
        return result;
    }

    // Calculate the number of possible connected graphs
    // given v vertices and e edges
    private static BigInteger connectedGraphs(
            final int v, final int e) {
        if (v == 0) {
            return ZERO;
        }
        if (v == 1) {
            // Fast exit #1: single-vertex
            return e == 0 ? ONE : ZERO;
        }
        final int allE = v * (v - 1) >> 1;
        if (e == allE) {
            // Fast exit #2: complete graph (the only result)
            return ONE;
        }
        final int key = v << 16 | e;
        if (CONN_GRAPHS_CACHE.containsKey(key)) {
            return CONN_GRAPHS_CACHE.get(key);
        }
        BigInteger result;
        if (e == v - 1) {
            // Fast exit #3: trees -> apply Cayley's formula
            result = BigInteger.valueOf(v).pow(v - 2);
        } else if (e > allE - (v - 1)) {
            // Fast exit #4: e > edges required to build a (v-1)-vertex
            // complete graph -> will definitely form connected graphs
            // in all cases, so just calculate allGraphs()
            result = allGraphs(v, e);
        } else {
            /*
             * In all other cases, we'll have to remove
             * partially-connected graphs from all graphs.
             *
             * We can define a partially-connected graph as a graph
             * with 2 sub-graphs A and B with no edges between them.
             * In addition, we require one of the sub-graphs (say, A)
             * to hold the following properties:
             * 1. A must be connected: this implies that the number
             *    of possible patterns for A could be counted
             *    by calling connectedGraphs().
             * 2. A must contain at least one fixed vertex:
             *    this property - combined with 1. -
             *    implies that A would not be over-counted.
             *
             * Under the definitions above, the number of
             * partially-connected graphs to be removed will be:
             *
             * (Combinations of vertices to be added from B to A) *
             * (number of possible A's, by connectedGraphs()) *
             * (number of possible B's, by allGraphs())
             * added up iteratively through v - 1 vertices
             * (one must be fixed in A) and all possible distributions
             * of the e edges through A and B
             */
            result = allGraphs(v, e);
            for (int vA = 1; vA < v; vA++) {
                // Combinations of vertices to be added from B to A
                final BigInteger aComb = nChooseR(v - 1, vA - 1);
                final int allEA = vA * (vA - 1) >> 1;
                // Maximum number of edges which could be added to A
                final int maxEA = allEA < e ? allEA : e;
                for (int eA = vA - 1; eA < maxEA + 1; eA++) {
                    result = result.subtract(aComb
                            // Number of possible A's
                            .multiply(connectedGraphs(vA, eA))
                            // Number of possible B's
                            .multiply(allGraphs(v - vA, e - eA)));
                }
            }
        }
        CONN_GRAPHS_CACHE.put(key, result);
        return result;
    }
}
