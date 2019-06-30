package com.example.teknasyon.testing;

import android.widget.Filter;

import java.util.ArrayList;

public class NoteSearcher extends Filter
{
    private NoteAdapter noteAdapter;
    private ArrayList<Note> notes;

    public NoteSearcher(NoteAdapter noteAdapter, ArrayList<Note> notes)
    {
        this.noteAdapter = noteAdapter;
        this.notes = notes;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint)
    {
        FilterResults filterResults = new FilterResults();
         //check validity
        if (constraint != null && constraint.length() > 0)
        {
            constraint = constraint.toString().toUpperCase();
            ArrayList<Note> filteredNotes = new ArrayList<Note>();

            for (Note noteIter: notes)
            {
                if(noteIter.getNoteHeader().toUpperCase().contains(constraint))
                {
                    filteredNotes.add(noteIter);
                }//end if
            }//end for
            filterResults.count = filteredNotes.size();
            filterResults.values = filteredNotes;
        }//end if
        else
        {
            filterResults.count = notes.size();
            filterResults.values = notes;
        }//end else
        return filterResults;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results)
    {
        noteAdapter.setNotes((ArrayList<Note>) results.values);
        noteAdapter.notifyDataSetChanged();
    }
}
