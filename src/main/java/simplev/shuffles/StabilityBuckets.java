package simplev.shuffles;

import simplev.common.Highlight;
import simplev.common.Shuffle;

public class StabilityBuckets extends Shuffle {
    public StabilityBuckets(Highlight highlight) {
        super(highlight, "Stability buckets");
    }

    @Override
    public void runShuffle(int[] array, int len) {
        for (int i = 0; i < len; i++) {
            highlight.write(array, i, highlight.read(array, i)/8*8);
        }
    }
}