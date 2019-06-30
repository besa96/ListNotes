package com.example.teknasyon.testing;

import java.util.ArrayList;

public class NotesProvider
{
    //public static int id = 1;
    private static ArrayList<Note> notes = new ArrayList<Note>();

    public static ArrayList<Note> getNotes()
    {
        return notes;
    }

    public static Note getById(final int id)
    {
        return notes.get(id);
    }

    public static int getNotesSize()
    {
        return notes.size();
    }

    public static void addNote(Note note)
    {
        notes.add(note);
    }

    public static void addAllNotes(ArrayList<Note> notes)
    {
        NotesProvider.notes.addAll(notes);
    }

    public static void addNoteById(final int id, Note note)
    {
        notes.add(id, note);
    }

    public static void updateNoteById(final int id, Note note)
    {
        notes.set(id, note);
    }

    public static void removeNoteById(final int id)
    {
        notes.remove(id);
    }

    public static void clearNotes()
    {
        notes.clear();
    }
}//end class
