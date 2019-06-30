package com.example.teknasyon.testing;

import android.Manifest;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class WriteNoteActivity extends AppCompatActivity
{
    private static final String TAG = "GeofenceDebug";
    private EditText etWriteNoteTitle, etWriteNoteMessage;
    //private GeofenceManager geofenceManager;
    private Note tempNote;
    private Bundle tempBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_note);
        etWriteNoteMessage = findViewById(R.id.etWriteNoteMessage);
        etWriteNoteTitle = findViewById(R.id.etWriteNoteTitle);

        final Bundle bundle = getIntent().getExtras();

        if (bundle != null)//already existing note
        {
            etWriteNoteMessage.setText(NotesProvider.getById(bundle.getInt("pos")).getNoteDesc());
            etWriteNoteTitle.setText(NotesProvider.getById(bundle.getInt("pos")).getNoteHeader());
        }//end if

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//back button
        }//end if

    }//end onCreate()

    private void save(final boolean withLocation)
    {
        final Bundle bundle = getIntent().getExtras();//get already existant note index for updating
        final Note note = new Note(etWriteNoteTitle.getText().toString(), etWriteNoteMessage.getText().toString());

        if (withLocation)//attach geofence location to note(notification only)
        {
            saveLocNote(note, bundle);
        }//end if
        else if (bundle != null)
        {
            NotesProvider.updateNoteById(bundle.getInt("pos"), note);
        }//end if
        else
        {
            NotesProvider.addNote(note);
        }//end else
        //SaveSharedPref(note);
        setResult(RESULT_OK);
        finish();
    }

    private void erase()//by ID
    {
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {
            NotesProvider.removeNoteById(bundle.getInt("pos"));
        }//end if
        else
        {
            Toast.makeText(this, "Var Olmayan Not Silinemez", Toast.LENGTH_SHORT).show();
        }
        setResult(RESULT_OK);
        finish();
    }

    private void showAlertDialog(final String title, final String message, final String pButton, final String nButton)
    {
        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        }//end if
        else
        {
            builder = new AlertDialog.Builder(this);
        }//end else
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(pButton, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                erase();
            }
        }).setNegativeButton(nButton, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //do nothing
            }
        }).setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case 0:
            case 1:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    saveLocNote(tempNote, tempBundle);
                }//end if
                else
                {
                    //U got denied
                    Log.d(TAG, "Permission Denied... requestCode = " + requestCode);
                }//end else
            }//end case
            break;
        }//end switch
    }

    public void saveLocNote(final Note note, final Bundle bundle)//check permissions before creating Geofence
    {
        this.tempNote = note;
        this.tempBundle = bundle;
        final GeofenceManager geofenceManager = new GeofenceManager(this);

        //check the permissions first
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            geofenceManager.getFusedLocation(note, bundle);//get location via FusedLocationProvider in order to create Geofence
        }//end if
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);//request
        }//end else
    }

    private void removeGeofence()
    {
        final GeofenceManager geofenceManager = new GeofenceManager(this);
        geofenceManager.removeGeofence();
    }

    private void saveAlarmNote()
    {
        final Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);

        final NoteAlertManager noteAlertManager = new NoteAlertManager(this);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfDay)
            {
                noteAlertManager.createAlarm(etWriteNoteTitle.getText().toString(), hourOfDay, minuteOfDay);
            }
        }, hour, minute, true);
        timePickerDialog.setTitle("Zamanı Seçin");
        timePickerDialog.show();
    }

    private void cancelAlarmNote()
    {
        final NoteAlertManager noteAlertManager = new NoteAlertManager(this);
        noteAlertManager.cancelAlarm(etWriteNoteTitle.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        final MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.save:
                save(false);
                break;
            case R.id.erase:
                showAlertDialog("Notu Sil", "Bu notu silmek istediğinizden emin misiniz?", "Evet", "Hayır");
                break;
            case R.id.shareMenuItem:
                share();
                break;
            case R.id.saveWAlarm:
                saveAlarmNote();
                break;
            case R.id.cancelAlarm:
                cancelAlarmNote();
                break;
            case R.id.saveWLoc:
                save(true);
                break;
            case R.id.removeLocFences:
                removeGeofence();
                break;
        }//end switch
        return super.onOptionsItemSelected(item);
    }

    public void share()
    {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, etWriteNoteMessage.getText().toString());
        shareIntent.setType("text/plain");
        startActivity(shareIntent);
    }
}//end activity
