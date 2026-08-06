package simplev.sorts;

import simplev.common.Highlight;
import simplev.common.Sort;

public class WikiSort extends Sort {
    public WikiSort(Highlight highlight) {
        super(highlight, "Wiki sort");
    }

    private int binlog(int bits) {
        int log = 0;
        if ((bits & 0xffff0000) != 0) {bits >>>= 16; log = 16;}
        if (bits >= 256) {bits >>>= 8; log += 8;}
        if (bits >= 16) {bits >>>= 4; log += 4;}
        if (bits >= 4) {bits >>>= 2; log += 2;}
        return log + (bits >>> 1);
    }

    public int binSearch(int[] array, int l, int r, int val) {
        while (l < r) {
            int pos = l+(r-l)/2;
            if (highlight.read(array, pos) < val) l = pos+1;
            else r = pos;
        }
        return l;
    }

    public int binSearchLast(int[] array, int l, int r, int val) {
        while (l < r) {
            int pos = l+(r-l)/2;
            if (highlight.read(array, pos) <= val) l = pos+1;
            else r = pos;
        }
        return l;
    }

    public void insertionSort(int[] array, int l, int r) {
        for (int i = l; i < r; i++) {
            int pos = binSearchLast(array, l, i, highlight.read(array, i));
            for (int j = i-1; j >= pos; j--) {
                highlight.swap(array, j+1, j);
            }
        }
    }

    void rotateMerge(int[] array, int l, int mid, int r) {
        while (true) {
            int pos = binSearch(array, mid, r, highlight.read(array, l));
            l = highlight.rotate(array, l, mid, pos);
            if (mid >= r) break;
            l = binSearchLast(array, l, mid, highlight.read(array, l));
            mid = pos;
            if (l >= mid) break;
        }
    }

    private void bufferMerge(int[] array, int l, int mid, int r, int bufferl) {
		int p1 = 0, p2 = 0, cur = 0;
		for (int i = l; i < mid; i++) {
			highlight.swap(array, bufferl+i-l, i);
		}
		while (p1+l < mid && mid+p2 < r) {
			if (highlight.read(array, bufferl+p1) <= highlight.read(array, mid+p2)) {
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

    void blockSwap(int[] array, int b1, int b2, int blockSize) {
        for (int i = 0; i < blockSize; i++) {
            highlight.swap(array, i+b1, i+b2);
        }
    }

    void wikiMerge(int[] array, int l, int mid, int r, int blockSize, int tagBufferL, int tagBufferR, int mergeBufferL, int mergeBufferR) throws RuntimeException {
        if (highlight.read(array, mid-1) <= highlight.read(array, mid)) return;
        if (highlight.read(array, l) >= highlight.read(array, r-1)) {
            highlight.rotate(array, l, mid, r);
            return;
        }
        int numA = (mid-l)/blockSize;
        int unmergedL = l;
        l += (mid-l)%blockSize;
        for (int i = 0; i < numA; i++) {
            highlight.swap(array, l+i*blockSize+1, tagBufferL+i);
        }
        int unmergedR = l;
        int lastB = -1;
        int curTag = tagBufferL;
        for (int i = l; i <= r-blockSize; i += blockSize) {
            int smallestA = l;
            if (i+numA*blockSize <= r) {
                smallestA = i;
                for (int j = i+blockSize; j < i+numA*blockSize; j += blockSize) {
                    if (highlight.read(array, j) < highlight.read(array, smallestA) ||
                        (highlight.read(array, j) == highlight.read(array, smallestA) && highlight.read(array, j+1) < highlight.read(array, smallestA+1))) smallestA = j;
                }
            }
            if (i+numA*blockSize >= r || (lastB != -1 && highlight.read(array, smallestA) <= highlight.read(array, lastB))) {
                if (i+numA*blockSize <= r) {blockSwap(array, i, smallestA, blockSize);}
                numA--;
                if (unmergedR-unmergedL == blockSize) {
                    highlight.swap(array, unmergedL+1, curTag++);
                }
                int pos = binSearch(array, unmergedR, i, highlight.read(array, i));
                if (mergeBufferR-mergeBufferL == 0) rotateMerge(array, unmergedL, unmergedR, pos);
                else bufferMerge(array, unmergedL, unmergedR, pos, mergeBufferL);
                highlight.rotate(array, pos, i, i+blockSize);
                unmergedL = pos;
                unmergedR = unmergedL+blockSize;
                lastB = -1;
            } else {
                int bSize = Math.min(i+numA*blockSize+blockSize, r)-(i+numA*blockSize);
                if (bSize == blockSize) blockSwap(array, i, i+numA*blockSize, blockSize);
                else highlight.rotate(array, i, i+numA*blockSize, Math.min(i+numA*blockSize+blockSize, r));
                lastB = i+bSize-1;
                i += bSize-blockSize;
            }
        }
        if (unmergedR-unmergedL == blockSize) {
            highlight.swap(array, unmergedL+1, curTag++);
        }
        bufferMerge(array, unmergedL, unmergedR, r, mergeBufferL);
    }

    @Override
    public void runSort(int[] array, int len) throws RuntimeException {
        int minrun = (int)Math.ceil(binlog(len))-4;
        for (int j = 0; j < Math.pow(2, minrun); j++) {
            int start = ((int)(j*(len/Math.pow(2, minrun))));
            int end = ((int)((j+1)*(len/Math.pow(2, minrun))));
            insertionSort(array, start, end);
        }
        for (int i = minrun-1; i >= 0; i--) {
            double scale = len/Math.pow(2, i);
            int lenA = ((int)(scale/2));
            int blockSize = (int)Math.sqrt((int)(lenA));
            int bufferSize = (int)(lenA)/blockSize+1;
            int found = 1;
            int last = 0;
            while (found < bufferSize*2) {
                int pos = binSearchLast(array, last, lenA, highlight.read(array, last));
                if (pos >= lenA) break;
                last = pos;
                found++;
            }
            int mergeBuff = bufferSize;
            if (found < bufferSize*2) {
                mergeBuff = 0;
                found = 1;
                last = 0;
                while (found < bufferSize) {
                    int pos = binSearchLast(array, last, lenA, highlight.read(array, last));
                    if (pos >= lenA) break;
                    last = pos;
                    found++;
                }
            }
            found = 1;
            while (last > 0) {
                int pos = binSearch(array, 0, last, highlight.read(array, last-1));
                highlight.rotate(array, pos, last, last+found);
                last = pos;
                found++;
            }
            int tagBuff = Math.min(found, bufferSize);
            if (highlight.read(array, 0) > highlight.read(array, found)) highlight.reverse(array, 0, found);
            blockSize = (int)(lenA)/(tagBuff-1)+1;
            
            for (int j = 0; j < Math.pow(2, i); j++) {
                int start = ((int)(j*scale));
                int end = ((int)((j+1)*scale));
                int mid = ((int)(j*scale+scale/2));
                if (j == 0) start += tagBuff+mergeBuff;
                wikiMerge(array, start, mid, end, blockSize, 0, tagBuff, tagBuff, tagBuff+mergeBuff);
            }
            insertionSort(array, 0, tagBuff+mergeBuff);
            rotateMerge(array, 0, tagBuff+mergeBuff, (int)scale);
        }
    }
}