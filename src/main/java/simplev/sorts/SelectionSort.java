package simplev.sorts;

import simplev.common.Highlight;
import simplev.common.Sort;

public class SelectionSort extends Sort {
    public SelectionSort(Highlight highlight) {
        super(highlight, "Seleciton sort");
    }

    @Override
    public void runSort(int[] array, int len) {
        for (int i = 0; i < len; i++) {
            int pos = i;
            for (int j = i+1; j < len; j++) {
                if (highlight.read(array, j) < highlight.read(array, pos)) {
                    pos = j;
                }
            }
            highlight.swap(array, i, pos);
        }
    }
}