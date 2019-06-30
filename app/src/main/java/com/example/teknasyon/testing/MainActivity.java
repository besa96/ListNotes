package com.example.teknasyon.testing;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class MainActivity extends AppCompatActivity
{
    private ArrayList<Note> note_arr = new ArrayList<Note>();
    static final int SAVE_NOTE = 1;
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private FloatingActionButton newButton;//create note button
    private FirebaseUser currentUser;
    private ArrayList<Boolean> hasGeofence, hasTimer;//to show icons for each future later on

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null)
        {
            signOut();
        }//end if

        loadSharedPref();//load stored Notes

        //onClickListener from NoteAdapter
        noteAdapter = new NoteAdapter(NotesProvider.getNotes(), new NoteAdapter.NoteAdapterClickListener()
        {
            @Override
            public void onClick(final int position)
            {
                final Intent intent = new Intent(MainActivity.this, WriteNoteActivity.class);
                intent.putExtra("pos", position);
                startActivityForResult(intent, SAVE_NOTE);
            }
        });

        setupRecyclerView();//+ swipe 2 delete

        newButton = findViewById(R.id.floatingActionButton);
        checkEmpty();

        newButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startWriteNote();
            }
        });
    }//end onCreate()

    private void setupSwipeToDelete(final RecyclerView recyclerView)
    {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT)
        {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target)
            {
                return false;
            }//end onMove

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir)
            {
                int position = viewHolder.getAdapterPosition();
                NotesProvider.removeNoteById(position);
                saveSharedPref();
                noteAdapter.notifyItemRemoved(position);
                checkEmpty();

                Toast.makeText(getBaseContext(), "Removed Note " + (position + 1) , Toast.LENGTH_SHORT).show();
            }//end onSwiped

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive)
            {
                //Paint Red
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                final ColorDrawable background = new ColorDrawable(Color.RED);
                background.setBounds(viewHolder.itemView.getRight(), viewHolder.itemView.getTop(), viewHolder.itemView.getRight() + (int) dX, viewHolder.itemView.getBottom());
                background.draw(c);

                //Trash Can Icon
                Drawable icon = ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_delete_forever_white_24dp);
                icon.setBounds(viewHolder.itemView.getRight() - 200, viewHolder.itemView.getTop() + 15, viewHolder.itemView.getRight(), viewHolder.itemView.getBottom() - 15);
                icon.draw(c);
            }//end onChildDraw
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        final MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_actionmenu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.aMenuSearch).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)//filter as we type
            {
                noteAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.aMenuAdd:
                startWriteNote();
                break;
            case R.id.aMenuLogout:
                signOut();
                break;
        }//end switch
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView()
    {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(noteAdapter);
        setupSwipeToDelete(recyclerView);
        //setupBackgroundColor(recyclerView);
    }

    private void loadSharedPref()//load Notes from SharedPreferences to NotesProvider
    {
        //SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        //Gson gson = new Gson();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Note");

        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                //GenericTypeIndicator<ArrayList<Note>> t = new GenericTypeIndicator<ArrayList<Note>>() {};
                ArrayList<Note> db_test = dataSnapshot.child(currentUser.getUid()).getValue(new GenericTypeIndicator<ArrayList<Note>>() {});
                if (db_test != null)
                {
                    NotesProvider.clearNotes();
                    NotesProvider.addAllNotes(db_test);
                    Toast.makeText(getBaseContext(), "Firebase Values Loaded", Toast.LENGTH_SHORT).show();
                    noteAdapter.notifyDataSetChanged();
                }//end if
                else
                {
                    Toast.makeText(getBaseContext(), "Firebase DB Empty, No Values Are Loaded", Toast.LENGTH_SHORT).show();
                }//end else
                checkEmpty();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Toast.makeText(getBaseContext(), "Couldn't Load Firebase DB Values", Toast.LENGTH_SHORT).show();
            }
        });

/*
        String json = sharedPref.getString("Notes", null);
        if(json != null)
        {
            NotesProvider.notes = gson.fromJson(json, new TypeToken<ArrayList<Note>>(){}.getType());
            Toast.makeText(this, "SharedPrefEditor Loaded", Toast.LENGTH_LONG).show();
        }//end if
*/
/*
        if (NotesProvider.notes == null)
        {
            NotesProvider.notes = new ArrayList<Note>();
        }//end if
        */
    }

    private void saveSharedPref()//save Notes from NotesProvider to SharedPreferences
    {
        /*
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        editor.putString("Notes", gson.toJson(NotesProvider.getNotes()));
        editor.apply();
        Toast.makeText(this, "SharedPrefEditor Saved", Toast.LENGTH_LONG).show();
        */

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Note");
        databaseReference.child(currentUser.getUid()).setValue(NotesProvider.getNotes());
        Toast.makeText(this, "Saved To Firebase DB", Toast.LENGTH_SHORT).show();
    }

    private void startWriteNote()
    {
        final Intent intent = new Intent(this, WriteNoteActivity.class);
        startActivityForResult(intent, SAVE_NOTE);
    }

    private void checkEmpty()
    {
        final TextView tVEmpty = findViewById(R.id.tVEmpty);
        final ImageView iVEmpty = findViewById(R.id.iVEmptyList);

        if (NotesProvider.getNotesSize() > 0)
        {
            recyclerView.setVisibility(View.VISIBLE);
            newButton.show();
            iVEmpty.setVisibility(View.GONE);
            tVEmpty.setVisibility(View.GONE);
        }//end else
        else
        {
            recyclerView.setVisibility(View.GONE);
            newButton.hide();
            iVEmpty.setVisibility(View.VISIBLE);
            tVEmpty.setVisibility(View.VISIBLE);
        }//end else
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data)
    {
        if(requestCode == SAVE_NOTE)
        {
            if(resultCode == RESULT_OK)
            {
                saveSharedPref();
                noteAdapter.notifyDataSetChanged();
                checkEmpty();
            }//end if
        }//end if
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void signOut()
    {
        FirebaseAuth.getInstance().signOut();
        final Intent logoutIntent = new Intent (this, LoginActivity.class);
        startActivity(logoutIntent);
        finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //saveSharedPref();
        NotesProvider.clearNotes();
    }//end onDestroy
}//end activity
