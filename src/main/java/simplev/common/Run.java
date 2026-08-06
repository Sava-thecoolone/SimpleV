package simplev.common;

import java.util.logging.Level;

public class Run implements Case {
    private static final SimpleLogger LOGGER = new SimpleLogger(Run.class.getName());
    public int[] array = {};
    public int[] stabilityTable = {};
    public int len = 0;
    public boolean stabilityCheck;
    Sort sort;
    Shuffle[] shufs;

    public Run(int[] array, int len, Sort sort, Shuffle[] shufs, boolean stabilityCheck, int[] stabilityTable) {
        this.array = array;
        this.len = len;
        this.sort = sort;
        this.shufs = shufs;
        this.stabilityCheck = stabilityCheck;
        this.stabilityTable = stabilityTable;
    }

    @Override
    public Thread makeThread(boolean benchmark, boolean zen) {
        return new Thread(() -> {
            String[] names = new String[shufs.length];
            for (int i = 0; i < shufs.length; i++) {
                names[i] = shufs[i].name;
            }
            String shufName = String.join(" -> ", names);
            shufs[0].highlight.title = "Shuffle: "+shufName;
            System.arraycopy(array, 0, stabilityTable, 0, len);
            shufs[0].highlight.stabilityCheck = false;
            shufs[0].highlight.stabilityTable = new int[0];
            for (Shuffle shuf : shufs) {
                LOGGER.log(Level.FINER, "Shuffle "+shufName+" started");
                try {
                    shuf.runShuffle(array, len);
                } catch (Throwable e) {
                    LOGGER.log(Level.SEVERE, "Runtime shuffle exception", e);
                }
                shuf.highlight.highlight.set(-1);
                LOGGER.log(Level.FINER, "Shuffle "+shuf.name+" finished");
            }
            sort.highlight.stabilityCheck = stabilityCheck;
            sort.highlight.stabilityTable = stabilityTable;
            if (!benchmark) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.log(Level.INFO, "Interrupted shuffle", e);
                    return;
                }
            }
            shufs[0].highlight.reset();
            LOGGER.log(Level.FINER, "Sort "+sort.name+" started");
            sort.highlight.title = "Sort: "+sort.name+(stabilityCheck ? " - STABILITY CHECK..." : "");
            long start = System.nanoTime();
            if (!sort.isBogo) {
                try {
                    sort.runSort(array, len);
                } catch (Throwable e) {
                    LOGGER.log(Level.SEVERE, "Runtime sort exception", e);
                }
            }
            long end = System.nanoTime();
            String time = Float.toString((end-start)/1000000f);
            boolean sorted = true;
            for (int i = 1; i < len; i++) {
                if (array[i-1] > array[i]) {
                    sorted = false;
                    break;
                }
            }
            if (stabilityCheck) {
                boolean stable = true;
                int stableCount = 0;
                for (int i = 1; i < len; i++) {
                    if (array[i-1] == array[i] && stabilityTable[i-1] > stabilityTable[i]) {
                        array[i] = len;
                        stable = false;
                        stableCount++;
                    }
                }
                sort.highlight.title = "Sort: "+sort.name+" - STABILITY CHECK - "+(stable ? "STABLE - " : "UNSTABLE - ")+stableCount+" out of order";
                if (benchmark) {
                    if (zen) System.out.println(sort.getClass().getSimpleName()+" - stability check - "+(stable ? "stable - " : "unstable - ")+stableCount+" out of order");
                    else LOGGER.log(Level.INFO, sort.name+" - stability check - "+stable);
                }
            }
            if (sort.isBogo && !sorted) {
                time = "inf";
            }
            sort.highlight.highlight.set(-1);
            LOGGER.log(Level.FINER, "Sort "+sort.name+" "+(sorted ? "finished" : "FAILED")+" with "+sort.highlight.reads+" reads and "+sort.highlight.writes+" writes");
            if (benchmark) {
                if (zen) {
                    System.out.println(sort.getClass().getSimpleName()+", "+len+" inputs, "+shufName+" - "+(sorted ? "finished" : "FAILED")+" : "+time+" milliseconds");
                } else {
                    LOGGER.log(Level.INFO, "Sort "+sort.getClass().getName()+" with "+len+" inputs shuffled by "+shufName+" "+(sorted ? "finished" : "FAILED")+" in "+time+" milliseconds");
                }
            }
            if (!benchmark) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.log(Level.INFO, "Interrupted sort", e);
                    return;
                }
            }
            sort.highlight.stabilityCheck = false;
            sort.highlight.reset();
        }, "RunThread");
    }
}