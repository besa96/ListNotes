package com.example.teknasyon.testing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class GeofenceManager
{
    private Context context;
    private ArrayList<Geofence> geofences = new ArrayList<>();
    private GeofencingRequest monitor;
    private PendingIntent geofencePendingIntent;
    private GeofencingClient geofencingClient;

    public GeofenceManager(final Context context)
    {
        this.context = context;
    }

    @SuppressLint("MissingPermission")//taken care of in saveLocNote
    public void getFusedLocation(final Note note, final Bundle bundle)
    {
        final FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        geofencingClient = LocationServices.getGeofencingClient(context);

        //get current location
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener((Activity) context, new OnSuccessListener<Location>()
        {
            @Override
            public void onSuccess(Location location) //Callback
            {
                if (location != null)
                {
                    //get location to local variables
                    final double longitude = location.getLongitude();
                    final double latitude = location.getLatitude();

                    //Geofence ID = Note Title/Header
                    createGeofence(note.getNoteHeader(), latitude, longitude, (float) 30);// 5 km radius (30m for test)

                    monitor = createGeofenceMonitor(geofences);

                    addGeofence();//add geofence for monitoring (link it with IntentService)

                    //add note
                    if (bundle != null)
                    {
                        NotesProvider.updateNoteById(bundle.getInt("pos"), note);
                    }//end if
                    else
                    {
                        NotesProvider.addNote(note);
                    }//end else
                }//end if
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.e(TAG, "Failure Getting Location" + e.getMessage());
            }
        });
    }

    public void createGeofence(final String ID, final double latitude, final double longitude, final float radius)
    {
        geofences.add(new Geofence.Builder()
                .setRequestId(ID)
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER).build());//trigger on enter
    }

    public GeofencingRequest createGeofenceMonitor(final ArrayList<Geofence> geofences)
    {
        final GeofencingRequest.Builder gfMonitorBuilder = new GeofencingRequest.Builder();
        gfMonitorBuilder.addGeofences(geofences);
        gfMonitorBuilder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);//for testing
        return gfMonitorBuilder.build();
    }

    @SuppressLint("MissingPermission")//taken care of in saveLocNote
    public void addGeofence()
    {
        geofencingClient.addGeofences(monitor, getGeofencePendingIntent()).addOnSuccessListener((Activity) context, new OnSuccessListener<Void>()//Callback
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                Log.d(TAG, "Added Geofence Succesfully");
                printGeofenceIDs();
            }
        }).addOnFailureListener((Activity) context, new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.e(TAG, "Failed To Add Geofence" + e.getMessage());
            }
        });
    }

    public PendingIntent getGeofencePendingIntent()
    {/*
        if (geofencePendingIntent != null)
        {
            return geofencePendingIntent;
        }//end if
*/
        //final Intent intent = new Intent(this, GeofenceTransitionsReceiver.class);
        final Intent intent = new Intent(context, GeofenceTransitionsIntentService.class);
        geofencePendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //geofencePendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    public void removeGeofence()
    {
        printGeofenceIDs();
        geofencingClient = LocationServices.getGeofencingClient(context);

        geofencingClient.removeGeofences(getGeofencePendingIntent()).addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                Log.d(TAG, "Removed Geofence Succesfully");
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.e(TAG, "Failed To Remove Geofence(s)" + e.getMessage());
            }
        });
    }

    public void printGeofenceIDs()
    {
        for (Geofence geoiter : geofences)
        {
            Log.d(TAG, geoiter.getRequestId());
        }//end for
    }
}
