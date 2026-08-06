package simplev.sorts;

import simplev.common.Highlight;
import simplev.common.Sort;

public class OptimizedBozoSort extends Sort {
    public OptimizedBozoSort(Highlight highlight) {
        super(highlight, "Optimized bozo sort");
    }

    @Override
    public void runSort(int[] array, int len) {
        while (!highlight.isSorted(array)) {
            int pos = (int)Math.floor(Math.random()*len);
            int i = (int)Math.floor(Math.random()*pos);
            if (highlight.read(array, i) > highlight.read(array, pos)) {
                highlight.swap(array, i, pos);
            }
        }
    }
}