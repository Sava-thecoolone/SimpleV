package simplev.common;

import java.util.logging.Level;

public class Run {
    private static final SimpleLogger LOGGER = new SimpleLogger(Run.class.getName());
    public int[] array = {};
    public int len = 0;
    Sort sort;
    Shuffle shuf;

    public Run(int[] array, int len, Sort sort, Shuffle shuf) {
        this.array = array;
        this.len = len;
        this.sort = sort;
        this.shuf = shuf;
    }

    public Thread makeThread(boolean benchmark) {
        return new Thread(() -> {
            LOGGER.log(Level.FINER, "Shuffle "+shuf.name+" started");
            shuf.highlight.title = "Shuffle: "+shuf.name;
            try {
                shuf.runShuffle(array, len);
            } catch (Throwable e) {
                LOGGER.log(Level.SEVERE, "Runtime shuffle exception", e);
            }
            shuf.highlight.highlight.set(-1);
            LOGGER.log(Level.FINER, "Shuffle "+shuf.name+" finished with "+shuf.highlight.reads+" reads and "+shuf.highlight.writes+" writes");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.log(Level.INFO, "Interrupted shuffle", e);
                return;
            }
            shuf.highlight.reset();
            LOGGER.log(Level.FINER, "Sort "+sort.name+" started");
            sort.highlight.title = "Sort: "+sort.name;
            long start = System.nanoTime();
            try {
                sort.runSort(array, len);
            } catch (Throwable e) {
                LOGGER.log(Level.SEVERE, "Runtime sort exception", e);
            }
            long end = System.nanoTime();
            boolean sorted = true;
            for (int i = 1; i < len; i++) {
                if (array[i-1] > array[i]) {
                    sorted = false;
                    break;
                }
            }
            sort.highlight.highlight.set(-1);
            LOGGER.log(Level.FINER, "Sort "+sort.name+" "+(sorted ? "finished" : "FAILED")+" with "+sort.highlight.reads+" reads and "+sort.highlight.writes+" writes");
            if (benchmark) {
                LOGGER.log(Level.INFO, "Sort "+sort.getClass().getCanonicalName()+" with "+len+" inputs shuffled by "+shuf.getClass().getCanonicalName()+" "+(sorted ? "finished" : "FAILED")+" in "+(end-start)+" nanoseconds");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.log(Level.INFO, "Interrupted sort", e);
                return;
            }
            sort.highlight.reset();
        }, "RunThread");
    }
}