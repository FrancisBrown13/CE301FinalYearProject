import midiExtract.*;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Track;
import java.io.IOException;
import java.util.*;

public class MyNGramExample {
    ArrayList<TrackWrapper> samples;
    ArrayList<TrackWrapper> samplesCopy;
    int n;
    //MyNGramCounter ngc;
    HashMap<TrackWrapper, MyNGramCounter> nGramCounters;

    private boolean goodTuringAvailable;
    public double numTrainingNgrams;
    TreeMap<Integer, Double> numberOfNgramsWithCount;

    public MyNGramExample(ArrayList<TrackWrapper> samples, int n){
        this.samples = samples;
        this.n = n;
        this.numberOfNgramsWithCount = new TreeMap<>();
        samplesCopy = new ArrayList<>(samples.size());

        //this.ngc = new MyNGramCounter(n, numberOfNgramsWithCount);
        nGramCounters = new HashMap<>();
    }

    public MyNGramExample(int n){
        this.n = n;
        this.numberOfNgramsWithCount = new TreeMap<>();
        //this.ngc = new MyNGramCounter(n, numberOfNgramsWithCount);

        samples = new ArrayList<>();
        samplesCopy = new ArrayList<>();
        nGramCounters = new HashMap<>();
    }

    public static TrackWrapper[] trackRipper(MidiLoader loader){
        return loader.getPiece().getTracks();
    }

    public static void main(String[] args) {
        String fileName = RingTones.NOKIA_RING.getFilename();
        MidiLoader loader;
        try{
            loader = new MidiLoader(fileName);
            int nGram = 3;
            MyNGramExample example = new MyNGramExample(nGram);
            example.train2(loader);

            Piece piece = new Piece(loader.getPiece(), example.writeNew(example.samples));

            MidiWriter writer = new MidiWriter(fileName, piece);

            System.out.println("Success");
        }
        catch (InvalidMidiDataException | IOException e){
            e.printStackTrace();
            System.exit(1);
        }

    }

    public void train2(MidiLoader loader){
        for (TrackWrapper track : loader.getPiece().getTracks()){
            addSample(track);

        }
        // ngc.GoodTuring();
    }

    public void addSample(TrackWrapper track){
        if (track.getNotes().size() == 0) return;
        nGramCounters.put(track, new MyNGramCounter(n, new TreeMap<>()));
        MyNGramCounter ngc = nGramCounters.get(track);

        NgramIterator iterator = new NgramIterator(n, track.getNotes());

        while (iterator.hasNext()){
            Note[] returned = iterator.next();
            ngc.insert(returned);
        }

        samples.add(track);
        ngc.GoodTuring();
    }

    public ArrayList<TrackWrapper> writeNew(ArrayList<TrackWrapper> original){
        ArrayList<TrackWrapper> newPiece = new ArrayList<>(original.size());
        for (TrackWrapper track : original){
            MyNGramCounter ngc = nGramCounters.get(track);
            ArrayList<Note> notes = new ArrayList<>(track.getNotes());
            Note[] nGram = new Note[n];
            for (int i = 0; i < n; i++){
                nGram[i] = new Note(true);
            }
            nGram[n-1] = notes.get(0);
            for (int j = 1; j < notes.size(); j++){
                Note currentNote = notes.get(j);
                for (int i = 0; i < n - 1; i++){
                    nGram[i] = nGram[i+1];
                }

                Note newNote = ngc.generateNextNote(nGram);

                notes.set(j, new Note(currentNote.getStart(),
                        currentNote.getDuration(),
                        currentNote.getChannel(),
                        currentNote.getVelocity(),
                        newNote.getPitch()));
                nGram[n-1] = notes.get(j);
            }
            TrackWrapper newTrack = new TrackWrapper(notes, track.getTrackNumber());
            newTrack.setInstrument(track.getInstrument());
            newTrack.setName(track.getName());

            newPiece.add(newTrack);
        }
        return newPiece;
    }


//    public double goodTuringSmoothedProbability(Note[] notes){
//        if (!goodTuringAvailable){
//            ngc.makeGoodTuringCounts();
//            goodTuringAvailable = true;
//        }
//
//        double gtcount = ngc.gtcount(notes);
//        if (gtcount > 0) return gtcount / ngc.level1GTCount(notes);
//
//        return numberOfNgramsWithCount.get(1.0)/numTrainingNgrams;
//    }
}
