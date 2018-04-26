import midiExtract.Note;

import java.util.*;

public class MyNGramCounter {
    private static final double CONFID_FACTOR = 1.96;

    private static TreeMap<Integer, Double> p = new TreeMap<>();
    private static double pZero;

    public int level;
    public HashMap<Note, MyNGramCounter> map;
    public double count;

    public double gtcount;
    public TreeMap<Integer, Double> numberOfNgramsWithCount;
    public static int totalNGrams;

    public MyNGramCounter(int level, TreeMap<Integer, Double> numberOfNgramsWithCount){
        this.level = level;
        this.numberOfNgramsWithCount = numberOfNgramsWithCount;

        if (level == 0) {
            // There are no links to child nodes, we are a leaf node
            this.map = null;
            // Set the initial count to 0.
            this.count = 0.0;
        } else {
            // We are not a leaf node, set up child node link hash.
            this.map = new HashMap<>();
            map.put(new Note(false), new MyNGramCounter(level - 1, numberOfNgramsWithCount));
            map.put(new Note(true), new MyNGramCounter(level - 1, numberOfNgramsWithCount));
            for (int i = 21; i < 109; i++){
                Note note = new Note(i);
                map.put(note, new MyNGramCounter(level - 1, numberOfNgramsWithCount));
            }
        }
    }

    public double insert(Note[] ngram){
        if (level != 0) count++;
        else {
            count++;
            totalNGrams++;
            return count;
        }

        MyNGramCounter next;
        Note note = ngram[ngram.length - level];
        if (map.containsKey(note)){
            next = map.get(note);
        }
        else{
            next = new MyNGramCounter(level - 1, numberOfNgramsWithCount);
            map.put(note, next);
        }

        return next.insert(ngram);
    }

    public double count(Note[] ngram){
        if (level == 0) return count;

        Note note = ngram[ngram.length - level];
        if (!map.containsKey(note)) return 0.0;

        return map.get(note).count(ngram);
    }

    public double level1Count(Note[] ngram){
        if (level == 1) return count;

        Note note = ngram[ngram.length - level];
        if (!map.containsKey(note)) return 0.0;

        return map.get(note).level1Count(ngram);
    }

    public Note generateNextNote(Note[] ngram){
        if (ngram.length == level)
            return generateNextNoteHelper(ngram);
        else if (ngram.length == level - 1){
            Note[] newNgram = new Note[level];
            System.arraycopy(ngram, 0, newNgram, 0, ngram.length);
            return generateNextNoteHelper(newNgram);
        }
        else
            throw new IllegalArgumentException();
    }

    private Note generateNextNoteHelper(Note[] ngram){
        if (level == 1){
            double totalCountForLevel = level1Count(ngram);

            // Generate a random distance into the counts to take a word from
            double prob = Math.random() * (totalCountForLevel/totalNGrams);
            double cumulativeProbability = 0.0;

            // Go through the possible words and see how far our random number gets us
            NgramIteratorTree i = new NgramIteratorTree(ngram.length, map.keySet());
            Iterator<Note> it = i.iterator();

            Note nextNote;
            int lowestPitch = 108;
            int highestPitch = 21;
            while (it.hasNext()) {
                nextNote = it.next();

                int count = (int)map.get(nextNote).count(ngram);

                if (count != 0) {
                    if (nextNote.getPitch() < lowestPitch) lowestPitch = nextNote.getPitch();
                    if (nextNote.getPitch() > highestPitch) highestPitch = nextNote.getPitch();
                }

                if (nextNote.getPitch() == -1) continue;

                if (p.get(count) == null) continue;//cumulativeProbability += pZero;
                else cumulativeProbability += p.get(count);

                if (prob <= cumulativeProbability) return nextNote;

            }

            return Note.generateRandomNote(lowestPitch, highestPitch);
        }

        return map.get(ngram[ngram.length-level]).generateNextNoteHelper(ngram);
    }

    public void populateGoodTuringFrequencies(){
        if (level == 0){
            if (count != 0) numberOfNgramsWithCount.merge((int)count, 1.0, Double::sum);
            return;
        }

        for (MyNGramCounter ngc : map.values())
            ngc.populateGoodTuringFrequencies();
    }

    public void GoodTuring(){
        populateGoodTuringFrequencies();
        compute();
    }

    // Generate Good Turing Counts based on original counts and the numberOfNgramsWithCount map
    public void makeGoodTuringCounts()
    {
//        System.out.println(numberOfNgramsWithCount);
//        for (Map.Entry entry : numberOfNgramsWithCount.entrySet()){
//            System.out.println(entry.getKey());
//            System.out.println(entry.getValue());
//        }
        // One level above leaf nodes, do the same as for any other non-leaf, but set the level 1 gtcount
        if (level == 1) {
            gtcount = 0;
            for (MyNGramCounter ngc : map.values()) {
                ngc.makeGoodTuringCounts();
                gtcount += ngc.gtcount;
            }
            return;
        }

        // On leaf level, set the gtcount
        if (level == 0) {
//            if (!numberOfNgramsWithCount.containsKey(count+1)) {
//                numberOfNgramsWithCount.put(count+1, 1.0);
//            }
            //numberOfNgramsWithCount.merge(count + 1, 1.0, Double::sum);
           // System.out.println(numberOfNgramsWithCount);
            // c* = (c+1) * N(c+1) / N(c)
            if (count != 0){
                if (count == 4)
                    System.out.println("Trigger");
                gtcount = (count+1)*(numberOfNgramsWithCount.get(count+1))/(numberOfNgramsWithCount.get((int)count));
                System.out.println(count);
                System.out.println(gtcount);
                System.out.println("");
            }
            return;
        }

        // Recursive step - Recurse to each child
        for (MyNGramCounter ngc : map.values()) {
            ngc.makeGoodTuringCounts();
        }
    }

    public double gtcount(Note[] notes){
        if (level == 0) return gtcount;

        if (!map.containsKey(notes[notes.length - level])) return 0.0;

        return map.get(notes[notes.length - level]).gtcount(notes);
    }

    public double level1GTCount(Note[] notes){
        if (level == 1) return gtcount;

        if (!map.containsKey(notes[notes.length - level])) return 0.0;

        return map.get(notes[notes.length - level]).level1GTCount(notes);
    }

    private void compute(){
        int i, j;
        double k, x, y, next_n, bigNPrime;
        boolean indiffValsSeen = false;
        int size = numberOfNgramsWithCount.size();

        TreeMap<Integer, Double> z = new TreeMap<>();
        TreeMap<Integer, Double> logR = new TreeMap<>();
        TreeMap<Integer, Double> logZ = new TreeMap<>();
        TreeMap<Integer, Double> rStar = new TreeMap<>();
        //TreeMap<Integer, Double> p = new TreeMap<>();

//        double[] logR = new double[size];
//        double[] logZ = new double[size];
//        double[] rStar = new double[size];
//        double[] p = new double[size];

        pZero = (numberOfNgramsWithCount.get(1) == null) ? 0 : numberOfNgramsWithCount.get(1) / (double)totalNGrams;


        boolean firstTime = true;
        i = 0;
        int prevKey = 0;
        double prevValue = 0.0;

        for (Map.Entry entry : numberOfNgramsWithCount.entrySet()){
            if (firstTime) firstTime = false;
            else i = prevKey;

            if (entry.equals(numberOfNgramsWithCount.lastEntry()))
                k = (double)(2 * ((Integer)entry.getKey()) - i);
            else k = (double)numberOfNgramsWithCount.higherKey((Integer)(entry.getKey()));

            z.put((Integer)entry.getKey(), 2 * (double)entry.getValue() / (k - i));
            logR.put((Integer)entry.getKey(), Math.log((double)(Integer)entry.getKey()));
            logZ.put((Integer)entry.getKey(), z.get(entry.getKey()));
        }

        double XYs, Xsquares, meanX, meanY;
        double slope;
        double intercept;

        XYs = Xsquares = meanX = meanY = 0.0;
        for (Integer entry : numberOfNgramsWithCount.keySet()){
            meanX += logR.get(entry);
            meanY += logZ.get(entry);
        }
        meanX /= size;
        meanY /= size;

        for (Integer entry : numberOfNgramsWithCount.keySet()){
            XYs += (logR.get(entry) - meanX) * (logZ.get(entry) - meanY);
            Xsquares += (logR.get(entry) - meanX) * (logR.get(entry) - meanX);
        }
        slope = XYs / Xsquares;
        intercept = meanY - slope * meanX;

        for (Map.Entry entry : numberOfNgramsWithCount.entrySet()){
            y = ((Integer)entry.getKey() + 1) * smoothed((Integer)entry.getKey() + 1, intercept, slope) /
                    smoothed((Integer)entry.getKey(), intercept, slope);
            if (numberOfNgramsWithCount.get((Integer)entry.getKey()) != null ||
                    numberOfNgramsWithCount.get((Integer)entry.getKey()) < 0){
                indiffValsSeen = true;
            }
            if (!indiffValsSeen){
                x = ((Integer) entry.getKey() + 1) *
                        (next_n = numberOfNgramsWithCount.get((Integer)entry.getKey() + 1)) /
                        (double)entry.getValue();
                if (Math.abs(x - y) <= CONFID_FACTOR *
                        Math.sqrt(((Double)entry.getKey() + 1) * ((Double)entry.getKey() + 1)) *
                        next_n / (((double)entry.getValue()) * ((double)entry.getValue())) *
                        (1 + next_n))
                    indiffValsSeen = true;
                else
                    rStar.put((Integer)entry.getKey(), x);
            }
            if (indiffValsSeen)
                rStar.put((Integer)entry.getKey(), y);
        }

        bigNPrime = 0.0;
        for (Map.Entry entry : numberOfNgramsWithCount.entrySet())
            bigNPrime += (Double)entry.getValue() * rStar.get((Integer)entry.getKey());
        for (Integer num : numberOfNgramsWithCount.keySet()){
            p.put(num, (1 - pZero) * rStar.get(num) / bigNPrime);
        }

        double tolerance = 1e-12;

        double sum = pZero;
        for (Map.Entry entry : numberOfNgramsWithCount.entrySet()){
            sum+= (numberOfNgramsWithCount.get((Integer)entry.getKey()) * p.get(entry.getKey()));
        }

        double err = 1.0 - sum;

        if (Math.abs(err) > tolerance)
            System.out.println("The probability distribution doesn't sum to 1");
    }

    private double smoothed(int i, double intercept, double slope){
        return (Math.exp(intercept + slope * Math.log(i)));
    }

    @Override
    public String toString() {
        return toString(this, 1).trim();
    }

    private String toString(MyNGramCounter root, int freq){
        if (root.map == null) return "";

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Note, MyNGramCounter> entry : root.map.entrySet()){
            if (entry.getValue().count >= freq){
                sb.append("Level: ").append(root.level).append("\n");
                sb.append(entry.getKey().nGramRelevantString()).append("\n");

                String returned = toString(entry.getValue(), freq);
                if (!returned.equals("")){
                    sb.append(returned).append("\n");
                }
            }
        }

        return sb.toString();
    }


    public String printOnlyMinFreq(int freq){
        return toString(this, freq).trim();
    }
}
