package simplev.shuffles;

import simplev.common.Highlight;
import simplev.common.Shuffle;

public class DeckCut extends Shuffle {
    public DeckCut(Highlight highlight) {
        super(highlight, "Deck cut");
    }

    @Override
    public void runShuffle(int[] array, int len) {
        for (int t = 0; t < 15; t++) {
            int[] temp = new int[len];
            for (int i = 0; i < len; i++) {
                temp[i] = highlight.read(array, i);
            }
            int cur = len-1;
            int tempC = 0;
            while (cur > 0) {
                int next = Math.max(cur-(int)(Math.random()*len/6), 0);
                for (int j = next; j < cur; j++) {
                    highlight.write(array, j, temp[tempC]);
                    tempC++;
                }
                cur = next;
            }
        }
    }
}