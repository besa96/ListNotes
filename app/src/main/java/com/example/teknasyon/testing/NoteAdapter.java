package com.example.teknasyon.testing;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;


public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> implements Filterable
{
    private ArrayList<Note> notes;
    private NoteAdapterClickListener noteAdapterClickListener;
    private NoteSearcher filter;

    public NoteAdapter(ArrayList<Note> notes, NoteAdapterClickListener noteAdapterClickListener)
    {
        this.notes = notes;
        this.noteAdapterClickListener = noteAdapterClickListener;
        this.filter = new NoteSearcher(this, this.notes);
    }

    @Override
    public Filter getFilter()
    {
        if (this.filter == null)
        {
            this.filter = new NoteSearcher(this, notes);
        }//end if
        return this.filter;
    }

    public interface NoteAdapterClickListener
    {
        void onClick(int position);
    }

    public void setNotes(ArrayList<Note> notes)
    {
        this.notes = notes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int viewType)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notes_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount()
    {
        return notes.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position)
    {
        viewHolder.tvHeader.setText(notes.get(position).getNoteHeader());
        viewHolder.tvDesc.setText(notes.get(position).getNoteDesc());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (NotesProvider.getById(position) != null)
                {
                    noteAdapterClickListener.onClick(position);
                }//end if
            }
        });
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView tvHeader, tvDesc;

        private ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.textView);
            tvDesc = itemView.findViewById(R.id.textView2);
        }//end constructor
    }//end class
}//end adapter