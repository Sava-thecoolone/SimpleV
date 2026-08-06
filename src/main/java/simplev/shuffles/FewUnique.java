package simplev.shuffles;

import simplev.common.Highlight;
import simplev.common.Shuffle;

public class FewUnique extends Shuffle {
    public FewUnique(Highlight highlight) {
        super(highlight, "Few unique");
    }

    @Override
    public void runShuffle(int[] array, int len) {
        for (int i = 0; i < len; i++) {
            highlight.write(array, i, highlight.read(array, i)/(len/8+1)*(len/8+1));
        }
    }
}