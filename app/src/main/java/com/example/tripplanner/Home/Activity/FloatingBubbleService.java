package com.example.tripplanner.Home.Activity;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.room.Room;

import com.example.tripplanner.R;
import com.example.tripplanner.TripData.Final;
import com.example.tripplanner.TripData.Trip;
import com.example.tripplanner.TripData.TripDatabase;

import java.util.ArrayList;
import java.util.List;

public class FloatingBubbleService extends Service {
 WindowManager.LayoutParams params;
private WindowManager manager;
private View viewFloatbubble;
private View viewcollaps;
private View viewexpandednote;
private List<CheckBox> checkBoxes;
private List<String> notes;
private int tripId;
private TripDatabase database;
private TextView txtNotes;
    public FloatingBubbleService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
      tripId=intent.getExtras().getInt(Final.TRIP_ID);

        new GetNotes().execute();


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        database= Room.databaseBuilder(this, TripDatabase.class,"tripDB").build();
        //init layout
        viewFloatbubble= LayoutInflater.from(this).inflate(R.layout.flout_bubble,null);
        initChecbox();
        notes=new ArrayList();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }
        else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }
        //view postion
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;


        //get service and add float bubble
        manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        manager.addView(viewFloatbubble, params);
        //get layout and add note view
         viewcollaps=viewFloatbubble.findViewById(R.id.layoutCollapsed);
         viewexpandednote=viewFloatbubble.findViewById(R.id.layoutExpanded);
          //add close buton
        viewFloatbubble.findViewById(R.id.buttonClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               stopSelf();
            }
        });
       viewexpandednote.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
       viewcollaps.setVisibility(View.VISIBLE);
       viewexpandednote.setVisibility(View.GONE);

       }
    });
      viewFloatbubble.findViewById(R.id.relativeLayoutParent).setOnTouchListener(new View.OnTouchListener() {
          private int initialX;
          private int initialY;
          private float initialTouchX;
          private float initialTouchY;
          @Override
          public boolean onTouch(View v, MotionEvent event) {
              switch (event.getAction()) {
                  case MotionEvent.ACTION_DOWN:
                      initialX = params.x;
                      initialY = params.y;
                      initialTouchX = event.getRawX();
                      initialTouchY = event.getRawY();
                      return true;
                  case MotionEvent.ACTION_UP:
                      //when the drag is ended switching the state of the widget
                      int Xdiff = (int) (event.getRawX() - initialTouchX);
                      int Ydiff = (int) (event.getRawY() - initialTouchY);

                      //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                      //So that is click event.
                      if (Xdiff < 10 && Ydiff < 10) {
                          if (isViewCollapsed()) {
                              //When user clicks on the image view of the collapsed layout,
                              //visibility of the collapsed layout will be changed to "View.GONE"
                              //and expanded view will become visible.
                              viewcollaps.setVisibility(View.GONE);
                              viewexpandednote.setVisibility(View.VISIBLE);
                          }
                      }

                      return true;
                  case MotionEvent.ACTION_MOVE:
                      //this code is helping the widget to move around the screen with fingers
                      params.x = initialX + (int) (event.getRawX() - initialTouchX);
                      params.y = initialY + (int) (event.getRawY() - initialTouchY);
                      manager.updateViewLayout(viewFloatbubble, params);
                      return true;
              }

              return false;
          }
      });









    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (viewFloatbubble!=null)
          manager.removeView(viewFloatbubble);
    }
    private boolean isViewCollapsed() {
        return viewFloatbubble == null || viewFloatbubble.findViewById(R.id.layoutCollapsed).getVisibility() == View.VISIBLE;
    }
   private void initChecbox()
    {
        checkBoxes = new ArrayList();
        checkBoxes.add(viewFloatbubble.findViewById(R.id.checkBox1));
        checkBoxes.add(viewFloatbubble.findViewById(R.id.checkBox2));
        checkBoxes.add(viewFloatbubble.findViewById(R.id.checkBox3));
        checkBoxes.add(viewFloatbubble.findViewById(R.id.checkBox4));
        checkBoxes.add(viewFloatbubble.findViewById(R.id.checkBox5));
        checkBoxes.add(viewFloatbubble.findViewById(R.id.checkBox6));
        checkBoxes.add(viewFloatbubble.findViewById(R.id.checkBox7));
        checkBoxes.add(viewFloatbubble.findViewById(R.id.checkBox8));
        checkBoxes.add(viewFloatbubble.findViewById(R.id.checkBox9));
        checkBoxes.add(viewFloatbubble.findViewById(R.id.checkBox10));
        txtNotes = viewFloatbubble.findViewById(R.id.textNotes);
    }
    private void attachNotesToCheckBoxes(Trip trip){
        if(trip.getNotes() != null && !trip.getNotes().isEmpty()) {
            txtNotes.setText("Notes");
            for (int i = 0; i < trip.getNotes().size(); i++) {
                checkBoxes.get(i).setText(trip.getNotes().get(i));
                checkBoxes.get(i).setVisibility(View.VISIBLE);
            }
        }else{
            txtNotes.setText("No Available Notes");
        }
    }
    private class GetNotes extends AsyncTask<Void, Void,Trip> {

        @Override
        protected Trip doInBackground(Void... voids) {
            return database.tripDAO().selectById(tripId);
        }

        @Override
        protected void onPostExecute(Trip trip) {
            super.onPostExecute(trip);
            if(trip != null){

                attachNotesToCheckBoxes(trip);
            }
        }
    }
}