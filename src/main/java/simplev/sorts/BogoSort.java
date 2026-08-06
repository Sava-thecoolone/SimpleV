package simplev.sorts;

import simplev.common.Highlight;
import simplev.common.Sort;

public class BogoSort extends Sort {
    public BogoSort(Highlight highlight) {
        super(highlight, "Bogo sort");
        isBogo = true;
    }

    @Override
    public void runSort(int[] array, int len) {
        while (!highlight.isSorted(array)) {
            for (int i = 0; i < len; i++) {
                highlight.swap(array, i, (int)Math.floor(Math.random()*len));
            }
        }
    }
}