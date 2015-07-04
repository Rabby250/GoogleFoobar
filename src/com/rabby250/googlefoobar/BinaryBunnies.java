package com.rabby250.googlefoobar;

import java.math.BigInteger;
import java.util.ArrayList;

public class BinaryBunnies {

    private static final BigInteger ONE = BigInteger.ONE;

    public static String answer(final int[] sequence) {
        final Integer[] clsSeq = new Integer[sequence.length];
        for (int i = 0; i < sequence.length; i++) {
            clsSeq[i] = sequence[i];
        }
        return combinations(clsSeq).toString();
    }

    private static BigInteger combinations(final Integer[] sequence) {
        final int leafCount = sequence.length - 1;
        if (leafCount <= 0) {
            return ONE;
        }
        final ArrayList<Integer> left = new ArrayList<Integer>();
        final ArrayList<Integer> right = new ArrayList<Integer>();
        final int root = sequence[0];
        for (int num : sequence) {
            if (num < root) {
                left.add(num);
            } else if (num > root) {
                right.add(num);
            }
        }
        final Integer[] leftSeq = new Integer[left.size()];
        final Integer[] rightSeq = new Integer[right.size()];
        return nChooseR(leafCount, left.size())
                .multiply(combinations(left.toArray(leftSeq)))
                .multiply(combinations(right.toArray(rightSeq)));
    }

    private static BigInteger nChooseR(int n, int r) {
        BigInteger result = ONE;
        for (int i = 0; i < r; i++) {
            result = result
                    .multiply(BigInteger.valueOf(n - i))
                    .divide(BigInteger.valueOf(i + 1));
        }
        return result;
    }
}
