package simplev.shuffles;

import simplev.common.Highlight;
import simplev.common.Shuffle;

public class Reversed extends Shuffle {
    public Reversed(Highlight highlight) {
        super(highlight, "Reversed");
    }

    @Override
    public void runShuffle(int[] array, int len) {
        for (int i = 0; i < len/2; i++) {
            highlight.swap(array, i, len-i-1);
        }
    }
}