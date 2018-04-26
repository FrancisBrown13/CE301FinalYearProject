import midiExtract.Note;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class NgramIteratorTree implements Iterable<Note> {
    private ArrayList<Note> notes;
    private int pos;
    private int n;

    public NgramIteratorTree(int n, ArrayList<Note> notes){
        this.notes = notes;
        this.n = n;
        pos = 0 - (n - 1);
    }

    public NgramIteratorTree(int n, Note[] notes){
        this.notes = new ArrayList<>(Arrays.asList(notes));
        this.n = n;
        pos = 0 - (n - 1);
    }

    public NgramIteratorTree(int n, Set<Note> notes){
        this.notes = new ArrayList<>(notes);
        this.n = n;
        pos = 0 - (n - 1);
    }

    @Override
    public Iterator<Note> iterator() {
        Iterator<Note> it = new Iterator<Note>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < notes.size() && notes.get(currentIndex) != null;
            }

            @Override
            public Note next() {
                Note returned = notes.get(currentIndex);
                currentIndex++;
                return returned;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return it;
    }


}