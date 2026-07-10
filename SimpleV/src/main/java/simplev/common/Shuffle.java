package simplev.common;

public class Shuffle {
    public String name;
    public volatile Highlight highlight;

    public Shuffle() {
        this.highlight = new Highlight(1);
        this.name = "<unnamed shuffle>";
    }

    public Shuffle(Highlight highlight, String name) {
        this.highlight = highlight;
        this.name = name;
    }

    public void runShuffle(int[] array, int len) {}
}