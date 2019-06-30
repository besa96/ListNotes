package com.example.teknasyon.testing;

public class Note
{
    private String noteHeader, noteDesc;
    //private short id;

    public Note()
    {
        this.noteHeader = null;
        this.noteDesc = null;
    }

    public Note(final String noteHeader, final String noteDesc)
    {
        this.noteHeader = noteHeader;
        this.noteDesc = noteDesc;
    }

    public String getNoteHeader()
    {
        return noteHeader;
    }

    public String getNoteDesc()
    {
        return noteDesc;
    }

    public void setNoteHeader(final String noteHeader)
    {
        this.noteHeader = noteHeader;
    }

    public void setNoteDesc(final String noteDesc)
    {
        this.noteDesc = noteDesc;
    }
}//end class
