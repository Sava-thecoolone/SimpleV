package simplev.sorts;

import simplev.common.Highlight;
import simplev.common.Sort;

public class QuickSort extends Sort {
    public QuickSort(Highlight highlight) {
        super(highlight, "Quick sort");
    }

    private void quickSort(int[] array, int p, int r) {
        int pivot = p + (r - p + 1) / 2;
        int x = highlight.read(array, pivot);

        int i = p;
        int j = r;

        while (i <= j) {
            while (highlight.read(array, i) < x) {
                i++;
            }
            while (highlight.read(array, j) > x) {
                j--;
            }

            if (i <= j) {
                highlight.swap(array, i, j);
                i++;
                j--;
            }
        }

        if(p < j) {
            this.quickSort(array, p, j);
        }
        if(i < r) {
            this.quickSort(array, i, r);
        }
    }

    @Override
    public void runSort(int[] array, int len) {
        quickSort(array, 0, len-1);
    }
}