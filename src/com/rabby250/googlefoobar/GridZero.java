package com.rabby250.googlefoobar;

public class GridZero {

    /*
     * Discussion and strategy proposal can be found
     * in the following Stack Exchange links:
     *
     * http://math.stackexchange.com/questions/441571/
     * http://stackoverflow.com/questions/27436275/
     */

    public static int answer(final int[][] matrix) {
        final int n = matrix.length;

        /*
         * If n was even, there'll be exactly one solution:
         * Each (i, j) will be toggled by the number of lit lights
         * on row i and column j, modulo 2 (since every 2nd toggle
         * reverts it to its initial state). Sum through all (i, j)
         * to get the total count.
         */
        if ((n & 1) == 0) {
            int count = 0;
            for (int[] row : matrix) {
                for (int j = 0; j < n; j++) {
                    int toggle = 0;
                    for (int k = 0; k < n; k++) {
                        toggle ^= row[k];
                        toggle ^= matrix[k][j];
                    }
                    // (i, j) itself was counted twice; fix it
                    toggle ^= row[j];
                    count += toggle;
                }
            }
            return count;
        }

        /*
         * For odd-sized grids, all rows and columns should share
         * the same parity for corresponding solutions to exist.
         */
        final int rowParity[] = new int[n];
        final int columnParity[] = new int[n];
        int row[];
        for (int i = 0; i < n; i++) {
            row = matrix[i];
            for (int j = 0; j < n; j++) {
                final int grid = row[j];
                rowParity[i] ^= grid;
                columnParity[j] ^= grid;
            }
        }
        final int correctParity = rowParity[0];
        for (int i = 0; i < n; i++) {
            if (rowParity[i] != correctParity
                    || columnParity[i] != correctParity) {
                return -1;
            }
        }

        /*
         * All we've got left were solvable odd cases, whose solution
         * space must be searched to obtain the minimum result.
         *
         * One proposal of doing this was to build a n^2 linear system
         * and find the solution to the system with minimum Hamming
         * distance, however the search space will be 2^(2n-2), which
         * will be rejected by Foobar's judging system.
         *
         * Therefore, we adopt the proposal by SO user darwinsenior
         * (original proposal could be found in the links above), which
         * expands the original matrix by adding an extra row and
         * column, and searches for solutions in the expanded matrix.
         * With proper optimization, we can fix the extra column
         * and focus on searching possibilities of extra rows only,
         * reducing the search space to 2^(n+1).
         */
        int result = n * n;
        for (int extraRow = 0; extraRow < (1 << (n + 1)); extraRow++) {

            // Fast parity calculation (from
            // http://graphics.stanford.edu/~seander/bithacks.html )
            int exRowParity = extraRow ^ (extraRow >>> 1);
            exRowParity ^= exRowParity >>> 2;
            exRowParity = (exRowParity & 0x11111111) * 0x11111111;
            exRowParity = (exRowParity >>> 28) & 1;

            // The solution should not contain any toggles on the
            // (non-existent) extra row/column, so skip those cases
            boolean skip = false;
            for (int j = 0; j < n; j++) {
                if ((columnParity[j] ^ exRowParity) != 0) {
                    skip = true;
                    break;
                }
            }
            if (skip) {
                continue;
            }

            /*
             * Calculate the minimum number of toggles for extraRow.
             * This could be done by manually adjusting each grid
             * in the extra column to minimize the toggle counts
             * of every row.
             */
            int minCase = 0, exColumnParity = 0, minPenalty = n;
            for (int i = 0; i < n; i++) {
                row = matrix[i];
                int rowCount = 0;
                for (int j = 0; j < n; j++) {
                    final int exRowJ = (extraRow >>> j) & 1;
                    // Toggle = rowParity[i] xor columnParity[j]
                    //          xor row[j] (remove duplicate)
                    //          xor exRowJ (not yet counted)
                    if ((rowParity[i] ^ columnParity[j]
                            ^ row[j] ^ exRowJ) != 0) {
                        rowCount++;
                    }
                }
                /*
                 * We have two possible numbers of toggles for each row:
                 * rowCount and n-rowCount . Compare them and choose the
                 * smaller one by adjusting extraColumn[i] .
                 */
                int rowPenalty = (rowCount << 1) - n;
                if (rowPenalty > 0) {
                    // Choose n-rowCount - set extraColumn[i] to 1
                    // to cancel out rowCount and switch to n-rowCount
                    minCase += n - rowCount;
                    exColumnParity ^= 1;
                } else {
                    // Keep rowCount - extraColumn[i] is 0
                    minCase += rowCount;
                    rowPenalty = -rowPenalty;
                }
                if (rowPenalty < minPenalty) {
                    minPenalty = rowPenalty;
                }
            }

            /*
             * Parity of the extra row and column should be the same
             * to prevent toggling the lower-right corner.
             * In case of a mismatch, we will need to fix the parity
             * by reversing one of the grids in the extra column.
             *
             * However, reversing a grid would also reverse the toggle
             * count of the row it belongs to (rowCount to n-rowCount,
             * or vice versa), which means that a penalty of
             * abs(rowCount - (n-rowCount)) will be added to the number
             * of toggles. minPenalty holds the minimum penalty to pay,
             * so just add it to our count.
             */
            if (exColumnParity != exRowParity) {
                minCase += minPenalty;
            }

            if (minCase < result) {
                result = minCase;
            }
        }
        return result;
    }
}
