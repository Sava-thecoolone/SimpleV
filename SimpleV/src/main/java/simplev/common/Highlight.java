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
    }

    public void reverse(int[] array, int l, int r) {
        for (int i = l; i < (l+r)/2; i++) {
            swap(array, i, r-i-1+l);
        }
    }

    public void rotate(int[] array, int l, int mid, int r) {
        reverse(array, l, mid);
        reverse(array, mid, r);
        reverse(array, l, r);
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