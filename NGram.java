import midiExtract.Note;

import java.util.Arrays;

public class NGram {
    private final int n;
    private final Note[] precedingNotes;
    private final Note value;

    public NGram(Note[] precedingNotes, Note note, int n){
        if (n < 1) throw new IllegalArgumentException("Cannot have an N-Gram less than 1");
        if (precedingNotes.length == 0) throw new IllegalArgumentException("Notes array is empty");
        if (note == null) throw new IllegalArgumentException("Note value should not be null");

        this.n = n;
        this.precedingNotes = precedingNotes;
        value = note;
    }

    public NGram(Note[] precedingNotes, int n){
        if (n < 1) throw new IllegalArgumentException("Cannot have an N-Gram less than 1");
        if (precedingNotes.length == 0) throw new IllegalArgumentException("Notes array is empty");

        this.n = n;
        if (n < 2) {
            value = precedingNotes[0];
            this.precedingNotes = new Note[0];
        }
        else {
            value = precedingNotes[precedingNotes.length - 1];
            this.precedingNotes = Arrays.copyOfRange(precedingNotes, 0, precedingNotes.length - 1);
        }
    }

    public Note[] getPrecedingNotes() {
        return precedingNotes;
    }

    public Note getValue() {
        return value;
    }

    public Note[] getAllNotes(){
        Note[] returnValue = new Note[precedingNotes.length + 1];
        System.arraycopy(precedingNotes, 0, returnValue, 0, precedingNotes.length);
        returnValue[precedingNotes.length] = value;
        return returnValue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Note note : precedingNotes){
            sb.append(note.toString()).append("\n");
        }
        sb.append(value.toString()).append("\n");
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != NGram.class) return false;
        NGram o = (NGram)obj;
        return value != o.getValue() && Arrays.deepEquals(precedingNotes, o.getPrecedingNotes());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + n;
        result = 31 * result + value.hashCode();
        result = 31 * result + Arrays.deepHashCode(precedingNotes);
        return result;
    }
}
