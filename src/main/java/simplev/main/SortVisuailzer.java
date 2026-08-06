package simplev.main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.JFrame;

import net.openhft.compiler.CompilerUtils;
import simplev.common.Case;
import simplev.common.Highlight;
import simplev.common.Run;
import simplev.common.RunPrint;
import simplev.common.Shuffle;
import simplev.common.SimpleLogger;
import simplev.common.Sort;

public class SortVisuailzer {
    private static final SimpleLogger LOGGER = new SimpleLogger(SortVisuailzer.class.getName());
    ArrayList<Case> cases = new ArrayList<>();
    volatile Highlight linkedHigh = new Highlight(1);
    public int[] stabilityTable = new int[1024];
    Renderer rend = new Renderer(new int[1024], 1024, linkedHigh, stabilityTable);
    Sounds sound = new Sounds(rend.array, rend.len, linkedHigh);
    boolean benchmark = false;
    boolean zen = false;
    int len = 0;
  
    private Sort loadSort(String name) {
        try {
            LOGGER.log(Level.FINE, "Compiling sorts/"+name+".java");
            return (Sort)(CompilerUtils.CACHED_COMPILER.loadFromJava("simplev.sorts."+name, Files.readString(Path.of("src/main/java/simplev/sorts/"+name+".java"))).getDeclaredConstructor(Highlight.class).newInstance(linkedHigh));
        } catch (IOException | ClassNotFoundException | InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            LOGGER.log(Level.SEVERE, "Unable to load sort", e);
            return new Sort();
        }
    }
  
    private Shuffle loadShuffle(String name) {
        try {
            LOGGER.log(Level.FINE, "Compiling shuffles/"+name+".java");
            return (Shuffle)(CompilerUtils.CACHED_COMPILER.loadFromJava("simplev.shuffles."+name, Files.readString(Path.of("src/main/java/simplev/shuffles/"+name+".java"))).getDeclaredConstructor(Highlight.class).newInstance(linkedHigh));
        } catch (IOException | ClassNotFoundException | InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            LOGGER.log(Level.SEVERE, "Unable to load shuffle", e);
            return new Shuffle();
        }
    }
    
    public final void loadCases(String path, String[] replace) {
        try {
            LOGGER.log(Level.FINE, "Loading cases...");
            String suite = Files.readString(Path.of(path));
            for (int i = 0; i < replace.length; i++) {
                suite = suite.replace("$"+i, replace[i]);
            }
            String[] strs = suite.split("\n");
            try {
                for (String str : strs) {
                    String[] p = str.split("(?<!\\\\)-");
                    if (p.length == 0) continue;
                    switch (p[0].trim()) {
                        case "delay":
                            linkedHigh.delayMult = Double.parseDouble(p[1].trim());
                            break;
                        case "benchmark":
                            benchmark = true;
                            linkedHigh.delayMult = 0;
                            break;
                        case "zen":
                            zen = true;
                            linkedHigh.delayMult = 0;
                            break;
                        case "print":
                            cases.add(new RunPrint(p[1].trim().replace("\\", "")));
                            break;
                        default:
                            if (p[0].trim().length() == 0) break;
                            Sort sort = loadSort(p[0].trim());
                            String[] s = p[1].split("(?<!\\\\),");
                            Shuffle[] shuffles = new Shuffle[s.length-1];
                            for (int i = 0; i < s.length-1; i++) {
                                shuffles[i] = loadShuffle(s[i+1].trim());
                            }
                            int curLen = Integer.parseInt(s[0].trim());
                            if (len == 0) len = curLen;
                            Run added = new Run(rend.array, curLen, sort, shuffles, p.length > 2 && p[2].trim().equals("Stability Check"), stabilityTable);
                            cases.add(added);
                            break;
                    }
                }
                LOGGER.log(Level.FINE, "All cases loaded successfully");
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                LOGGER.log(Level.SEVERE, "Malformed suite", e);
                System.exit(1);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Can't read suite file", e);
            System.exit(1);
        }
    }

    public SortVisuailzer(String suitePath, String[] replace) {
        this.loadCases(suitePath, replace);
    }

    private Thread makeMultiSortThread() {
        return new Thread(() -> {
            LOGGER.log(Level.FINE, "Suite started");
            for (Case c : cases) {
                if (c == null) continue;
                if (c instanceof Run r) {
                    rend.array = new int[r.len];
                    stabilityTable = new int[r.len];
                    for (int i = 0; i < r.len; i++) {
                        rend.array[i] = i;
                    }
                    r.array = rend.array;
                    sound.array = rend.array;
                    rend.len = r.len;
                    sound.len = r.len;
                    r.stabilityTable = stabilityTable;
                    rend.stabilityTable = stabilityTable;
                }
                Thread sortT = c.makeThread(benchmark, zen);
                sortT.start();
                try {
                    sortT.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.log(Level.WARNING, "Interrupted main suite thread", e);
                    linkedHigh.title = "Interrupted";
                    return;
                }
            }
            linkedHigh.title = "Done!";
            LOGGER.log(Level.FINE, "Suite ended");
        }, "RunAllThread");
    }

    public void setupAudio() {
        sound.makeThread().start();
        LOGGER.log(Level.FINER, "Audio setup finished");
    }

    public void setupRender() {
        rend.array = new int[len];
        stabilityTable = new int[len];
        for (int i = 0; i < len; i++) {
            rend.array[i] = i;
        }
        sound.array = rend.array;
        rend.len = len;
        sound.len = len;
        rend.stabilityTable = stabilityTable;
        JFrame frame = new JFrame("SimpleV");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(rend);
        frame.setSize(1024, 1024*3/4);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                frame.removeMouseListener(this);
                makeMultiSortThread().start();
            }
        });
        linkedHigh.title = "Click to start suite";
        LOGGER.log(Level.FINER, "Render setup finished");
    }
}