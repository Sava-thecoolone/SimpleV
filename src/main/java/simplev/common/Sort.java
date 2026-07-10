package simplev.common;

public class Sort {
    public String name;
    public volatile Highlight highlight;

    public Sort() {
        this.highlight = new Highlight(1);
        this.name = "<unnamed sort>";
    }

    public Sort(Highlight highlight, String name) {
        this.highlight = highlight;
        this.name = name;
    }

    public void runSort(int[] array, int len) {}
}