import midiExtract.MidiLoader;
import midiExtract.Note;
import midiExtract.TrackWrapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.*;

public class Test {
    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    public static void printMidi() throws Exception{
        Sequence sequence = MidiSystem.getSequence(new File("data/cornered.mid"));

        int trackNumber = 0;
        for (Track track :  sequence.getTracks()) {
            trackNumber++;
            System.out.println("Track " + trackNumber + ": size = " + track.size());
            System.out.println();
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                System.out.print("@" + event.getTick() + " ");
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    System.out.print("Channel: " + sm.getChannel() + " ");
                    if (sm.getCommand() == NOTE_ON) {
                        int key = sm.getData1();
                        int octave = (key / 12)-1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                    } else if (sm.getCommand() == NOTE_OFF) {
                        int key = sm.getData1();
                        int octave = (key / 12)-1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                    } else {
                        System.out.println("Command:" + sm.getCommand());
                    }
                } else {
                    System.out.println("Other message: " + message.getClass());
                }
            }

            System.out.println();
        }
    }

    public static void testNGramIterator() throws InvalidMidiDataException, IOException{
        MidiLoader loader = new MidiLoader("data/cornered.mid");

        double count = 0;
        HashMap<NGram, Integer> map = new HashMap<>();
        for (TrackWrapper track : loader.getPiece().getTracks()){

            if (track.getNotes().size() == 0) continue;
            NgramIterator iterator = new NgramIterator(2, track.getNotes());
            while (iterator.hasNext()){
                Note[] returned = iterator.next();
                count++;
                NGram nGram = new NGram(returned, 2);
                map.merge(nGram, 1, (a,b) -> a + b);

            }

        }

        double num = 0;
        for (Map.Entry<NGram, Integer> entry : map.entrySet()){
            num += ((double)entry.getValue())/count;
            if (entry.getValue() > 25){
                System.out.println(entry.getKey() + " ====== " + entry.getValue());
                System.out.println(" ");
            }

        }
        System.out.println(num);
    }

    public static void main(String[] args) throws Exception {
        try{
            testNGramIterator();
        }
        catch (InvalidMidiDataException e){
            e.printStackTrace();
        }
    }
}
