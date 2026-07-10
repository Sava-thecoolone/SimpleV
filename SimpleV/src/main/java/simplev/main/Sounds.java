package simplev.main;

import java.io.IOException;
import java.util.logging.Level;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

import simplev.common.Highlight;
import simplev.common.SimpleLogger;

public class Sounds {
    private static final SimpleLogger LOGGER = new SimpleLogger(Sounds.class.getName());
    public int[] array;
    public int len;
    public volatile Highlight highlight;
    Synthesizer synth;
    MidiChannel channel;

    public Thread makeThread() {
        try {
            this.synth = MidiSystem.getSynthesizer();
            this.synth.open();
            this.synth.loadAllInstruments(MidiSystem.getSoundbank(Highlight.class.getResourceAsStream("/sfx.sf2")));
            this.channel = this.synth.getChannels()[0];
            this.channel.programChange(0, 16);
        } catch (MidiUnavailableException | InvalidMidiDataException | IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to create synth", e);
            return new Thread();
        }
        Thread audio = new Thread(() -> {
            try {
                while (true) {
                    if (highlight.highlight.get() == -1) {
                        channel.allNotesOff();
                        continue;
                    }
                    double pitch = ((double)array[(int)highlight.highlight.get()]/len)*80+20;
                    channel.noteOn((int)pitch, 80);
                    channel.setPitchBend((int)((pitch-((int)pitch))*8192d)+8192); // from ArrayV
                    channel.controlChange(91, 10);
                }
            } catch (RuntimeException e) {
                LOGGER.log(Level.SEVERE, "Audio thread exception", e);
            }
        }, "AudioThread");
        return audio;
    }

    public Sounds(int[] array, int len, Highlight highlight) {
        this.array = array;
        this.len = len;
        this.highlight = highlight;
    }
}