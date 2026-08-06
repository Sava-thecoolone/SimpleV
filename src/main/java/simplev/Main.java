package simplev;

import simplev.main.SortVisuailzer;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        SortVisuailzer vis = new SortVisuailzer(args[0], Arrays.copyOfRange(args, 1, args.length));
        vis.setupAudio();
        vis.setupRender();
    }
}