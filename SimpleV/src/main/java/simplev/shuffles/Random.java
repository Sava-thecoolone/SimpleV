package simplev.shuffles;

import simplev.common.Highlight;
import simplev.common.Shuffle;

public class Random extends Shuffle {
    public Random(Highlight highlight) {
        super(highlight, "Random");
    }

    @Override
    public void runShuffle(int[] array, int len) {
        for (int idx = 0; idx < len; idx++) {
            highlight.swap(array, idx, (int)Math.floor(Math.random()*len));
        }
    }
}