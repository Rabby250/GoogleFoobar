package com.rabby250.googlefoobar;

public class WhenItRainsItPours {

    public static int answer(int[] heights) {

        final int length = heights.length;
        // Left boundary for hutch i
        final int[] left = new int[length];
        // Right boundary for hutch i
        final int[] right = new int[length];

        // Scan left[]
        int max = -1;
        for (int i = 0; i < length; i++) {
            if (heights[i] > max) {
                left[i] = -1;
                max = heights[i];
            } else {
                left[i] = max;
            }
        }

        // Scan right[]
        max = -1;
        for (int i = length - 1; i >= 0; i--) {
            if (heights[i] > max) {
                right[i] = -1;
                max = heights[i];
            } else {
                right[i] = max;
            }
        }

        // Calculate the water accumulated on each hutch
        // TODO: combine this with left[] scan to remove left[]
        int result = 0;
        for (int i = 0; i < length; i++) {
            if (left[i] > 0 && right[i] > 0) {
                result += (left[i] < right[i] ?
                        left[i] : right[i]) - heights[i];
            }
        }
        return result;
    }
}
