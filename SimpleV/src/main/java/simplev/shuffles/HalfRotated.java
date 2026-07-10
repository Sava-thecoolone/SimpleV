package simplev.shuffles;

import simplev.common.Highlight;
import simplev.common.Shuffle;

public class HalfRotated extends Shuffle {
    public HalfRotated(Highlight highlight) {
        super(highlight, "Half rotated");
    }

    @Override
    public void runShuffle(int[] array, int len) {
        highlight.rotate(array, 0, len/2, len);
    }
}