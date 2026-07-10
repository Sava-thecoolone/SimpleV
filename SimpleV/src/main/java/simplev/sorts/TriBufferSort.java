package simplev.sorts;

import simplev.common.Highlight;
import simplev.common.Sort;

public class TriBufferSort extends Sort {
    public TriBufferSort(Highlight highlight) {
        super(highlight, "Tri-Buffer sort");
    }

    private int binlog(int bits) {
        int log = 0;
        if ((bits & 0xffff0000) != 0) {bits >>>= 16; log = 16;}
        if (bits >= 256) {bits >>>= 8; log += 8;}
        if (bits >= 16) {bits >>>= 4; log += 4;}
        if (bits >= 4) {bits >>>= 2; log += 2;}
        return log + (bits >>> 1);
    }

    private int medianidx(int[] array, int a, int b, int c) {
		if (highlight.read(array, a) > highlight.read(array, b)) {
            return highlight.read(array, a) < highlight.read(array, c) ? a : (highlight.read(array, b) < highlight.read(array, c) ? c : b);
		} else {
            return highlight.read(array, a) > highlight.read(array, c) ? a : (highlight.read(array, b) > highlight.read(array, c) ? c : b);
        }
	}

	private int getPivot(int[] array, int l, int r) {
        if (r-l <= 16) return l+(r-l+1)/2;
        int nineth = (r-l+1)/9;
        int m1 = medianidx(array, l+nineth/2, l+nineth/2+nineth, l+nineth/2+nineth*2);
        int m2 = medianidx(array, l+nineth/2+nineth*3, l+(r-l+1)/2, l+nineth/2+nineth*5);
        int m3 = medianidx(array, l+nineth/2+nineth*6, l+nineth/2+nineth*7, l+nineth/2+nineth*8);
		return medianidx(array, m1, m2, m3);
	}

    public void insertionSort(int[] array, int l, int r) {
        for (int i = l; i < r; i++) {
            int pos = l-1;
            for (int j = i-1; j >= l; j--) {
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



    private int partition(int[] array, int l, int r) {
		int pivotIdx = getPivot(array, l, r);
        highlight.swap(array, pivotIdx, r-1);
        int pivot = highlight.read(array, r-1);
		int p1 = l;
		int p2 = r-2;

		while (p1 <= p2) {
            while (highlight.read(array, p1) < pivot) p1++;
            while (highlight.read(array, p2) > pivot) p2--;
            if (p1 <= p2) highlight.swap(array, p1++, p2--);
		}
        highlight.swap(array, r-1, p1);

		return p1;
	}

    private void bufferMerge(int[] array, int l, int mid, int r, int bufferl) {
        if (highlight.read(array, mid-1) < highlight.read(array, mid)) return;
		int p1 = 0, p2 = 0, cur = 0;
		for (int i = l; i < mid; i++) {
			highlight.swap(array, bufferl+i-l, i);
		}
		while (p1+l < mid && mid+p2 < r) {
			if (highlight.read(array, bufferl+p1) < highlight.read(array, mid+p2)) {
				highlight.swap(array, bufferl+p1, l+cur);
				p1++;
			} else {
				highlight.swap(array, mid+p2, l+cur);
				p2++;
			}
			cur++;
		}
		while (p1+l < mid) {
			highlight.swap(array, bufferl+p1, l+cur);
			p1++;
			cur++;
		}
	}

    private void quickSelect(int[] array, int l, int r, int k) {
		int pivot = partition(array, l, r);
        if (k == pivot) {}
        else if (pivot > k) quickSelect(array, l, pivot, k);
        else quickSelect(array, pivot+1, r, k);
	}

    private void iterMerge(int[] array, int l, int r, int bufferl) {
        int length = r-l;
        for (int i = (int)Math.ceil(binlog(length))-2; i >= 0; i--) {
            double scale = length/Math.pow(2, i);
            
            for (int j = 0; j < Math.pow(2, i); j++) {
                int start = ((int)(j*scale))+l;
                int end = ((int)((j+1)*scale))+l;
                if (scale <= 8) {
                    insertionSort(array, start, end);
                } else {
                    int mid = ((int)((j+0.5f)*scale))+l;
                    bufferMerge(array, start, mid, end, bufferl);
                }
            }
        }
    }

    private void buffMergeSort(int[] array, int l, int r) {
        if (r <= l) return;
        if (r-l <= 32) {
            insertionSort(array, l, r);
            return;
        }
        int third = (int)Math.ceil((r-l+1)/3);
        quickSelect(array, l, r, l+third);
        iterMerge(array, l+third, r, l);
        buffMergeSort(array, l, l+third);
    }

    @Override
    public void runSort(int[] array, int len) {
        buffMergeSort(array, 0, len);
    }
}