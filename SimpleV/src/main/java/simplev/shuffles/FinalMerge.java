package simplev.shuffles;

import simplev.common.Highlight;
import simplev.common.Shuffle;

public class FinalMerge extends Shuffle {
    public FinalMerge(Highlight highlight) {
        super(highlight, "Final merge");
    }

    @Override
    public void runShuffle(int[] array, int len) {
        int[] temp = new int[len];
        for (int i = 0; i < len; i++) {
            temp[i] = highlight.read(array, i);
        }
        for (int i = 0; i < len/2; i++) {
            highlight.write(array, i, temp[i*2]);
            highlight.write(array, i+len/2, temp[i*2+1]);
        }
    }
}