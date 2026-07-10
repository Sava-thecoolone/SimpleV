package simplev.sorts;

import simplev.common.Highlight;
import simplev.common.Sort;

public class InsertionSort extends Sort {
    public InsertionSort(Highlight highlight) {
        super(highlight, "Insertion sort");
    }

    @Override
    public void runSort(int[] array, int len) {
        for (int i = 0; i < len; i++) {
            int pos = -1;
            for (int j = i-1; j >= 0; j--) {
                if (highlight.read(array, j) <= highlight.read(array, i)) {
                    pos = j;
                    break;
                }
            }
            for (int j = i-1; j > pos; j--) {
                highlight.swap(array, j+1, j);
            }
        }
    }
}