import midiExtract.Note;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class NgramIterator {
    private ArrayList<Note> notes;
    private int pos;
    private int n;

    public NgramIterator(int n, ArrayList<Note> notes){
        this.notes = notes;
        this.n = n;
        pos = 0 - (n - 1);
    }

    public NgramIterator(int n, Note[] notes){
        this.notes = new ArrayList<>(Arrays.asList(notes));
        this.n = n;
        pos = 0 - (n - 1);
    }

    public NgramIterator(int n, Set<Note> notes){
        this.notes = new ArrayList<>(notes);
        this.n = n;
        pos = 0 - (n - 1);
    }

    public boolean hasNext() {
        return pos < notes.size();
    }

    public Note[] next() {
        Note[] returnNotes = new Note[n];
        for (int i = pos; i < pos + n; i++){
            int index = i - pos;
            if (i < 0) {
                returnNotes[index] = new Note(true);
            }
            else if (i > notes.size() - 1) returnNotes[index] = new Note(false);
            else returnNotes[index] = notes.get(i);
        }
        pos++;
        return returnNotes;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
