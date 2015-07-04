package com.rabby250.googlefoobar;

import java.util.HashSet;
import java.util.LinkedList;

public class DontMindTheMap {

    private static final int RESULT_MET = -1;
    private static final int RESULT_FAIL = -2;
    private static final int RESULT_OVERFLOW = -3;

    private static final int UPPER_BOUND = 1000000;

    public static int answer(final int[][] subwayMap) {
        /*
         * The real challenge was test case 5, which had:
         * - subwayMap[48][3]
         * - subwayMap[0] = { x, y1, 0 } and
         *   subwayMap[x] = { 0, y2, x } (note the relationship)
         * - Will result in TLE when fw-searching subwayMap,
         *   and result in TLE when back-searching alt(close 0)
         *
         * The best solution I could think of within time
         * limit was a bidirectional search.
         */
        if (fakeBiDiSearch(subwayMap) == RESULT_MET) {
            return RESULT_MET;
        }
        for (int station = 0; station < subwayMap.length; station++) {
            if (fakeBiDiSearch(buildAlternateMap(subwayMap, station))
                    == RESULT_MET) {
                return station;
            }
        }
        return RESULT_FAIL;
    }

    // A "fake" bidirectional search: if a forward search takes up
    // time/space, search from the other end instead
    // TODO: try an actual (simultaneous) bidirectional search
    private static int fakeBiDiSearch(final int[][] map) {
        final int fwResult = searchForward(map);
        switch (fwResult) {
            case RESULT_MET:
            case RESULT_FAIL:
                return fwResult;
            case RESULT_OVERFLOW:
            default:
                return searchBackwards(map);
        }
    }

    // Map builders

    // reverseMap[s][l] = stations (in bit representation)
    // which could reach station s via line l
    private static long[][] buildReverseMap(final int[][] map) {
        final int numStations = map.length;
        final int numLines = map[0].length;
        final long[][] reverseMap = new long[numStations][numLines];
        for (int station = 0; station < numStations; station++) {
            for (int line = 0; line < numLines; line++) {
                reverseMap[map[station][line]][line] |= 1L << station;
            }
        }
        return reverseMap;
    }

    // Alternate map with closedStation removed
    // (and all indices behind shifted for array compactness)
    private static int[][] buildAlternateMap(
            final int[][] map, final int closedStation) {
        final int numStations = map.length;
        final int numLines = map[0].length;
        final int[][] alternateMap
                = new int[numStations - 1][numLines];
        for (int line = 0; line < numLines; line++) {
            // The new destination for those who would've arrived
            // at the closed station (-1 means u-turn)
            int newDest = map[closedStation][line];
            if (newDest == closedStation) {
                newDest = -1;
            } else if (newDest > closedStation) {
                newDest--;
            }

            int newStation = 0;
            for (int station = 0; station < numStations; station++) {
                if (station == closedStation) {
                    continue;
                }
                final int dest = map[station][line];
                if (dest == closedStation) {
                    // Rerouted
                    if (newDest == -1) {
                        alternateMap[newStation][line] = newStation;
                    } else {
                        alternateMap[newStation][line] = newDest;
                    }
                } else if (dest > closedStation) {
                    // Shifted
                    alternateMap[newStation][line] = dest - 1;
                } else {
                    // Unchanged
                    alternateMap[newStation][line] = dest;
                }
                newStation++;
            }
        }
        return alternateMap;
    }

    // Forward tracers
    // { 0 .. s-1 } -> line l1 -> ... -> line ln -> { meet }

    private static long forwardTrace(
            final int[][] map, final long from, final int line) {
        final int numStations = map.length;
        long to = 0L;
        for (int station = 0; station < numStations; station++) {
            if ((from & (1L << station)) != 0) {
                to |= 1L << map[station][line];
            }
        }
        return to;
    }

    private static boolean hasOneStationLeft(
            final long state, final int numStations) {
        /*
         * TODO: optimize w/ parallel Hamming weight calculation:
         * result = popCount(state << (64 - numStations)) == 1;
         *
         * private static int popCount(long x) {
         *     x -= (x >>> 1) & 0x5555555555555555;
         *     x = (x & 0x3333333333333333)
         *             + ((x >>> 2) & 0x3333333333333333);
         *     x = (x + (x >>> 4)) & 0x0f0f0f0f0f0f0f0f;
         *     return (x * 0x0101010101010101) >>> 56;
         * }
         */
        boolean result = false;
        for (int station = 0; station < numStations; station++) {
            if ((state & (1L << station)) != 0) {
                if (result) {
                    // Multiple stations left
                    return false;
                }
                result = true;
            }
        }
        return result;
    }

    private static int searchForward(final int[][] map) {
        final int numStations = map.length;
        final int numLines = map[0].length;

        final LinkedList<Long> stateStack = new LinkedList<>();
        final HashSet<Long> stateCache = new HashSet<>();

        // Init state: all stations
        final long initState = (1L << numStations) - 1L;
        stateCache.add(initState);
        stateStack.push(initState);
        Long from = initState;

        while (from != null) {
            boolean done = true;

            // The following loop will cause the parent state
            // to be processed twice
            // TODO: skip redundant processing

            for (int line = 0; line < numLines; line++) {
                final long to = forwardTrace(map, from, line);
                if (hasOneStationLeft(to, numStations)) {
                    return RESULT_MET;
                }
                if (stateCache.contains(to)) {
                    continue;
                }
                if (stateCache.size() >= UPPER_BOUND) {
                    return RESULT_OVERFLOW;
                }
                stateCache.add(to);
                stateStack.push(to);
                done = false;
            }
            if (done) {
                stateStack.pop();
            }
            from = stateStack.peek();
        }
        return RESULT_FAIL;
    }

    // Back tracers
    // { meet } -> line ln -> ... -> line l1 -> { 0 .. s-1 }

    private static long backTrace(
            final long[][] reverseMap, final long to, final int line) {
        final int numStations = reverseMap.length;
        long from = 0L;
        for (int station = 0; station < numStations; station++) {
            if ((to & (1L << station)) != 0) {
                from |= reverseMap[station][line];
            }
        }
        return from;
    }

    private static int searchBackwards(final int[][] map) {
        final long[][] reverseMap = buildReverseMap(map);
        final int numStations = reverseMap.length;
        final int numLines = reverseMap[0].length;
        final long endState = (1L << numStations) - 1L;

        final LinkedList<Long> stateStack = new LinkedList<>();
        final HashSet<Long> stateCache = new HashSet<>();

        for (int station = numStations - 1; station >= 0; station--) {
            final Long stationFlag = 1L << station;
            stateCache.add(stationFlag);
            stateStack.push(stationFlag);
        }
        Long to = 1L;

        while (to != null) {
            boolean done = true;

            // The following loop will cause the parent state
            // to be processed twice
            // TODO: skip redundant processing

            for (int line = 0; line < numLines; line++) {
                final long from = backTrace(reverseMap, to, line);
                if (from == endState) {
                    return RESULT_MET;
                }
                if (stateCache.contains(from)) {
                    continue;
                }
                if (stateCache.size() >= UPPER_BOUND) {
                    return RESULT_OVERFLOW;
                }
                stateCache.add(from);
                stateStack.push(from);
                done = false;
            }
            if (done) {
                stateStack.pop();
            }
            to = stateStack.peek();
        }
        return RESULT_FAIL;
    }
}
