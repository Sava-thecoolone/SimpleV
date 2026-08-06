package simplev.common;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

public class Highlight {
    private static final SimpleLogger LOGGER = new SimpleLogger(Highlight.class.getName());
    public AtomicInteger highlight;
    public double delayMult;
    public String title = "<no title>";
    public AtomicLong reads;
    public AtomicLong writes;
    public boolean stabilityCheck = false;
    public int[] stabilityTable = {};
    int unslept = 0;

    public int read(int[] array, int a) {
        doHigh(a, array.length);
        reads.incrementAndGet();
        return array[a];
    }

    public void write(int[] array, int a, int b) {
        array[a] = b;
        doHigh(a, array.length);
        writes.incrementAndGet();
    }

    public void swap(int[] array, int a, int b) {
        int temp = read(array, a);
        write(array, a, read(array, b));
        write(array, b, temp);
        if (stabilityCheck) {
            temp = read(stabilityTable, a);
            write(stabilityTable, a, read(stabilityTable, b));
            write(stabilityTable, b, temp);
        }
    }

    public void reverse(int[] array, int l, int r) {
        for (int i = l; i < (l+r)/2; i++) {
            swap(array, i, r-i-1+l);
        }
    }

    public int rotate(int[] array, int l, int mid, int r) {
        int a = l;
        int b = mid;
        int c = mid;
        int d = r;
        if (mid-l > r-mid) {
            int loop = (r-mid)/2;
            while (loop-- > 0) {
                b--; d--;
                int temp = read(array, b);
                write(array, b, read(array, a));
                write(array, a, read(array, c));
                write(array, c, read(array, d));
                write(array, d, temp);
                if (stabilityCheck) {
                    temp = read(stabilityTable, b);
                    write(stabilityTable, b, read(stabilityTable, a));
                    write(stabilityTable, a, read(stabilityTable, c));
                    write(stabilityTable, c, read(stabilityTable, d));
                    write(stabilityTable, d, temp);
                }
                a++; c++;
            }
            loop = (b-a)/2;
            while (loop-- > 0) {
                b--; d--;
                int temp = read(array, b);
                write(array, b, read(array, a));
                write(array, a, read(array, d));
                write(array, d, temp);
                if (stabilityCheck) {
                    temp = read(stabilityTable, b);
                    write(stabilityTable, b, read(stabilityTable, a));
                    write(stabilityTable, a, read(stabilityTable, d));
                    write(stabilityTable, d, temp);
                }
                a++;
            }
            loop = (d-a)/2;
            while (loop-- > 0) {
                d--;
                int temp = read(array, a);
                write(array, a, read(array, d));
                write(array, d, temp);
                if (stabilityCheck) {
                    temp = read(stabilityTable, a);
                    write(stabilityTable, a, read(stabilityTable, d));
                    write(stabilityTable, d, temp);
                }
                a++;
            }
        } else if (mid-l < r-mid) {
            int loop = (mid-l)/2;
            while (loop-- > 0) {
                b--; d--;
                int temp = read(array, b);
                write(array, b, read(array, a));
                write(array, a, read(array, c));
                write(array, c, read(array, d));
                write(array, d, temp);
                if (stabilityCheck) {
                    temp = read(stabilityTable, b);
                    write(stabilityTable, b, read(stabilityTable, a));
                    write(stabilityTable, a, read(stabilityTable, c));
                    write(stabilityTable, c, read(stabilityTable, d));
                    write(stabilityTable, d, temp);
                }
                a++; c++;
            }
            loop = (d-c)/2;
            while (loop-- > 0) {
                d--;
                int temp = read(array, c);
                write(array, c, read(array, d));
                write(array, d, read(array, a));
                write(array, a, temp);
                if (stabilityCheck) {
                    temp = read(stabilityTable, c);
                    write(stabilityTable, c, read(stabilityTable, d));
                    write(stabilityTable, d, read(stabilityTable, a));
                    write(stabilityTable, a, temp);
                }
                a++; c++;
            }
            loop = (d-a)/2;
            while (loop-- > 0) {
                d--;
                int temp = read(array, a);
                write(array, a, read(array, d));
                write(array, d, temp);
                if (stabilityCheck) {
                    temp = read(stabilityTable, a);
                    write(stabilityTable, a, read(stabilityTable, d));
                    write(stabilityTable, d, temp);
                }
                a++;
            }
        } else {
            int loop = mid-l;
            while (loop-- > 0) {
                int temp = read(array, a);
                write(array, a, read(array, b));
                write(array, b, temp);
                if (stabilityCheck) {
                    temp = read(stabilityTable, a);
                    write(stabilityTable, a, read(stabilityTable, b));
                    write(stabilityTable, b, temp);
                }
                a++; b++;
            }
        }
        return l+r-mid;
    }

    public boolean isSorted(int[] array) {
        for (int i = 1; i < array.length; i++) {
            if (read(array, i-1) > read(array, i)) return false;
        }
        return true;
    }

    public void doHigh(int idx, int len) {
        highlight.set(idx);
        try {
            int delay = (int)((10000000d*delayMult)/Math.log(len));
            if (delay+unslept > 1000000) {
                Thread.sleep((delay+unslept)/1000000);
                unslept -= (delay+unslept)/1000000*1000000;
            } else {
                unslept += delay;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.log(Level.INFO, "Interrupted highlight");
        }
    }

    public void reset() {
        highlight.set(-1);
        reads.set(0);
        writes.set(0);
    }

    public Highlight(int delayMult) {
        this.delayMult = delayMult;
        this.reads = new AtomicLong();
        this.writes = new AtomicLong();
        this.highlight = new AtomicInteger();
        this.highlight.set(-1);
    }

    public Highlight(int delayMult, String title) {
        this.delayMult = delayMult;
        this.reads = new AtomicLong();
        this.writes = new AtomicLong();
        this.highlight = new AtomicInteger();
        this.highlight.set(-1);
        this.title = title;
    }
}