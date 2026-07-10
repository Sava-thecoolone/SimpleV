package simplev;

import simplev.main.SortVisuailzer;

public class Main {
    public static void main(String[] args) {
        SortVisuailzer vis = new SortVisuailzer(args[0]);
        vis.setupAudio();
        vis.setupRender();
    }
}