package simplev.sorts;

import simplev.common.Highlight;
import simplev.common.Sort;

public class BogoBubbleSort extends Sort {
    public BogoBubbleSort(Highlight highlight) {
        super(highlight, "Bogo bubble sort");
    }

    @Override
    public void runSort(int[] array, int len) {
        while (!highlight.isSorted(array)) {
            int pos = (int)Math.floor(Math.random()*(len-1));
            int i = pos+1;
            if (highlight.read(array, i) < highlight.read(array, pos)) {
                highlight.swap(array, i, pos);
            }
        }
    }
}