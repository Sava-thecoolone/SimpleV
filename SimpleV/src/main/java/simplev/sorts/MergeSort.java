package simplev.sorts;

import simplev.common.Highlight;
import simplev.common.Sort;

public class MergeSort extends Sort {
    public MergeSort(Highlight highlight) {
        super(highlight, "Merge sort");
    }

    int[] merge(int[] array, int l, int mid, int r) {
        int p1 = l;
        int p2 = mid;
        int[] temp = new int[r-l+1];
        int i = 0;
        while (p1 < mid && p2 < r) {
            if (highlight.read(array, p2) < highlight.read(array, p1)) {
                temp[i++] = highlight.read(array, p2);
                p2++;
            } else {
                temp[i++] = highlight.read(array, p1);
                p1++;
            }
        }
        while (p1 < mid) {
            temp[i++] = highlight.read(array, p1);
            p1++;
        }
        while (p2 < r) {
            temp[i++] = highlight.read(array, p2);
            p2++;
        }
        return temp;
    }

    void mergeSort(int[] array, int l, int r) {
        if (r-1 <= l) return;
        int mid = l+(r-l+1)/2;
        mergeSort(array, l, mid);
        mergeSort(array, mid, r);
        int[] temp = merge(array, l, mid, r);
        for (int i = l; i < r; i++) {
            highlight.write(array, i, temp[i-l]);
        }
    }

    @Override
    public void runSort(int[] array, int len) {
        mergeSort(array, 0, len);
    }
}