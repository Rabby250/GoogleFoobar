package com.rabby250.googlefoobar;

import java.math.BigInteger;
import java.util.HashMap;

public class BreedingLikeRabbits {
    private static final BigInteger NEG_ONE = BigInteger.valueOf(-1);
    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private static final BigInteger THREE = BigInteger.valueOf(3);

    // 10^25 requires less than 400 entries, so no size concerns
    private static final HashMap<BigInteger, BigInteger> R_CACHE
            = new HashMap<BigInteger, BigInteger>();

    public static String answer(final String str_S) {
        final BigInteger s = new BigInteger(str_S);
        // Fast exit for 1 to 3
        if (s.compareTo(THREE) != 1) {
            return str_S;
        }
        R_CACHE.put(ZERO, ONE);
        R_CACHE.put(ONE, ONE);
        R_CACHE.put(TWO, TWO);
        // Even Ns will always outgrow odd Ns after 3,
        // so we'll look for odd solutions first
        BigInteger r = searchN(s, true);
        if (!r.equals(NEG_ONE)) {
            return r.toString();
        }
        r = searchN(s, false);
        if (!r.equals(NEG_ONE)) {
            return r.toString();
        }
        return "None";
    }

    private static BigInteger r(final BigInteger n) {
        if (R_CACHE.containsKey(n)) {
            return R_CACHE.get(n);
        }
        final BigInteger i = n.divide(TWO);
        final BigInteger r;
        if (n.testBit(0)) {
            r = r(i.subtract(ONE)).add(r(i)).add(ONE);
        } else {
            r = r(i.add(ONE)).add(r(i)).add(i);
        }
        R_CACHE.put(n, r);
        return r;
    }

    private static BigInteger searchN(
            final BigInteger target, final boolean searchOdd) {
        BigInteger uBound = target,
                lBound = searchOdd ? ONE : TWO;
        boolean isOdd = uBound.testBit(0);
        if ((searchOdd && !isOdd) || (!searchOdd && isOdd)) {
            uBound = uBound.subtract(ONE);
        }
        while (uBound.compareTo(lBound) != -1) {
            // Calculate median and fix parity
            BigInteger median = uBound.add(lBound).divide(TWO);
            isOdd = median.testBit(0);
            if ((searchOdd && !isOdd) || (!searchOdd && isOdd)) {
                median = median.add(ONE);
            }
            // Compare and adjust boundaries (excluding median)
            switch (r(median).compareTo(target)) {
                case -1:
                    lBound = median.add(TWO);
                    break;
                case 0:
                    return median;
                case 1:
                    uBound = median.subtract(TWO);
            }
        }
        return NEG_ONE;
    }
}
