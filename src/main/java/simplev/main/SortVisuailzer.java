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
import simplev.common.Highlight;
import simplev.common.Run;
import simplev.common.Shuffle;
import simplev.common.SimpleLogger;
import simplev.common.Sort;

public class SortVisuailzer {
    private static final SimpleLogger LOGGER = new SimpleLogger(SortVisuailzer.class.getName());
    ArrayList<Run> cases = new ArrayList<>();
    volatile Highlight linkedHigh = new Highlight(1);
    Renderer rend = new Renderer(new int[1024], 1024, linkedHigh);
    Sounds sound = new Sounds(rend.array, rend.len, linkedHigh);
    boolean benchmark = false;
  
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
    
    public final void loadCases(String path) {
        try {
            LOGGER.log(Level.FINE, "Loading cases...");
            String[] strs = Files.readString(Path.of(path)).split("\n");
            try {
                for (String str : strs) {
                    String[] p = str.split("-");
                    if (p.length <= 1) continue;
                    switch (p[0].trim()) {
                        case "delay":
                            linkedHigh.delayMult = Double.parseDouble(p[1].trim());
                            break;
                        case "benchmark":
                            benchmark = true;
                            linkedHigh.delayMult = 0;
                            break;
                        default:
                            if (p[0].trim().length() == 0) break;
                            Sort sort = loadSort(p[0].trim());
                            String[] s = p[1].split(",");
                            Shuffle shuffle = loadShuffle(s[1].trim());
                            cases.add(new Run(rend.array, Integer.parseInt(s[0].trim()), sort, shuffle));
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

    public SortVisuailzer(String suitePath) {
        this.loadCases(suitePath);
    }

    private Thread makeMultiSortThread() {
        return new Thread(() -> {
            LOGGER.log(Level.FINE, "Suite started");
            for (Run c : cases) {
                rend.array = new int[c.len];
                for (int i = 0; i < c.len; i++) {
                    rend.array[i] = i;
                }
                c.array = rend.array;
                sound.array = rend.array;
                rend.len = c.len;
                sound.len = c.len;
                Thread sortT = c.makeThread(benchmark);
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
        rend.array = new int[cases.get(0).len];
        for (int i = 0; i < cases.get(0).len; i++) {
            rend.array[i] = i;
        }
        cases.get(0).array = rend.array;
        sound.array = rend.array;
        rend.len = cases.get(0).len;
        sound.len = cases.get(0).len;
        JFrame frame = new JFrame("SimpleV");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(rend);
        frame.setSize(1280, 720);
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