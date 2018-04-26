import midiExtract.MidiLoader;
import midiExtract.Note;
import midiExtract.TrackWrapper;

import javax.sound.midi.InvalidMidiDataException;
import java.io.IOException;
import java.util.HashMap;

public class NGramGenerator {
    private HashMap<NGram, Integer> frequency;
    private int gramType;
    private int totalNGrams;

    public NGramGenerator(int n){
        frequency = new HashMap<>();
        gramType = n;
        totalNGrams = 0;
    }

    public boolean addToModel(MidiLoader loader){
        for (TrackWrapper track : loader.getPiece().getTracks()){
            if (track.getNotes().size() == 0) continue;

            NgramIterator iterator = new NgramIterator(gramType, track.getNotes());

            while (iterator.hasNext()){
                Note[] returned = iterator.next();
                totalNGrams++;
                NGram nGram = new NGram(returned, gramType);
                frequency.merge(nGram, 1, (a,b) -> a + b);
            }
        }
        return true;
    }

    public boolean addToModel(String fileName){
        try{
            MidiLoader loader = new MidiLoader(fileName);
            addToModel(loader);
        }

        catch (InvalidMidiDataException | IOException e){
            return false;
        }
        return true;
    }

    public HashMap<NGram, Integer> getFrequencyMap() {
        return frequency;
    }

    public int getFrequency(NGram nGram){
        return frequency.getOrDefault(nGram, 0);
    }

    public double getUnsmoothedProbability(NGram nGram){
        int freq = getFrequency(nGram);
        if (freq > 0) return ((double)freq)/totalNGrams;

        return 0.0;
    }
}
