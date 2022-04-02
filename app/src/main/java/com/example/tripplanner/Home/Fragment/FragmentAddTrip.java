package com.example.tripplanner.Home.Fragment;

import static android.app.Activity.RESULT_OK;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.tripplanner.Home.Activity.AddActivity;
import com.example.tripplanner.Home.Activity.Home_Activity;
import com.example.tripplanner.Home.Activity.WorkManagerRepository;
import com.example.tripplanner.TripData.Final;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.example.tripplanner.R;
import com.example.tripplanner.TripData.Trip;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;


public class FragmentAddTrip extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Places.initialize(requireContext(), Final.API_KEY);
        sharedPreferences =requireActivity().getSharedPreferences(PREF_NAME,0);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =inflater.inflate(R.layout.fragment_add_trip, container, false);

        //Method have declared variable
         initComponent();

        calenderNormal = Calendar.getInstance();
        calendarRound = Calendar.getInstance();
        //Handle Edit Trip Button
        if(AddActivity.key==2) {
            btnAddTrip.setText("Edit");
            radioGroup.setVisibility(View.GONE);
            constraintLayoutRoundTrip.setVisibility(View.GONE);
            btnAddNotes.setVisibility(View.GONE);
            new getRoomData().execute();
        }
        //on click listener methods
        //Place Api
        etStartPoint.setOnClickListener(v -> placesAutocompletes(StartPointFlag));
        etEndPoint.setOnClickListener(v -> placesAutocompletes(ENDPOINTFlag));

        //start handle calender(views for calender)
        //manger of calender
        getParentFragmentManager().setFragmentResultListener("requestkey", this, (requestKey, result) -> {
            //add date and time in text view
            resultNotes=new ArrayList();
            resultNotes = result.getStringArrayList("bundleKey");
            String date=result.getString("date");
            String time=result.getString("time");
            String date2=result.getString("date2");
            String time2=result.getString("time2");
            tvDate.setText(date);
            tvTime.setText(time);
            tvRoundDate.setText(date2);
            tvRoundTime.setText(time2);
        });
       //Handle Round Button
        radioButtonOneDirection.setOnClickListener(v -> {
            tvRoundTime.setText("");
            tvRoundDate.setText("");
            constraintLayoutRoundTrip.setVisibility(View.GONE);
            isRound =false;
        });
        radioButtonRoundTrip.setOnClickListener(v -> {
            isRound =true;
            if (isRound =true) {
                constraintLayoutRoundTrip.setVisibility(View.VISIBLE);
            }
        });
        //button of time
        imageButtonTime.setOnClickListener(view -> {
            if (isDateCorrect)
                Time(tvTime, 1,calenderNormal);
            else
                Toast.makeText(getContext(), "Please choose date first", Toast.LENGTH_SHORT).show();

            isFirstTimeSelected = true;
        });
        //button of date
        imageButtonDate.setOnClickListener(view -> {
            Date(tvDate, 1,calenderNormal);
            tvTime.setText("");
        });
        //date2
        imageButtonRoundDate.setOnClickListener(v -> {
            if(isDateCorrect) {
                Date(tvRoundDate, 2,calendarRound);
                tvRoundTime.setText("");
            }else
                Toast.makeText(getContext(), "Please choose date of the first trip", Toast.LENGTH_SHORT).show();
        });
        //time 2
        imageButtonRoundTime.setOnClickListener(v -> {
            if (isDateCorrectRoundTrip && isFirstTimeSelected)
                Time(tvRoundTime, 2,calendarRound);

            else
                Toast.makeText(getContext(), "Please choose date of the round trip", Toast.LENGTH_SHORT).show();
        });

        //buttonAddNote
        btnAddNotes.setOnClickListener(v -> {
            if(isFirstAddNotes){
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.setReorderingAllowed(true);
                // Replace  fragment with add note fragment
                transaction.replace(R.id.switchFragment, AddNoteFragment.class,null);
                transaction.addToBackStack("name");
                transaction. setReorderingAllowed(true);
                transaction.commit();
                // send dat
                Bundle result = new Bundle();
                if (!TextUtils.isEmpty(tvDate.getText()))
                    result.putString("date", tvDate.getText().toString());
                if (!TextUtils.isEmpty(tvTime.getText()))
                    result.putString("time", tvTime.getText().toString());
                if (!TextUtils.isEmpty(tvRoundDate.getText()))
                    result.putString("date2", tvRoundDate.getText().toString());
                if (!TextUtils.isEmpty(tvRoundTime.getText()))
                    result.putString("time2", tvRoundTime.getText().toString());
                getParentFragmentManager().setFragmentResult("datakey", result);

            }
        });

        btnAddTrip.setOnClickListener(v -> {
            checkData();
            new alarmWork().execute();

        });

        return view;
    }
    //save data of time on bundle
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("Date", tvDate.getText().toString());
        outState.putString("Time", tvTime.getText().toString());
        outState.putString("DateRound", tvRoundDate.getText().toString());
        outState.putString("TimeRound", tvRoundTime.getText().toString());
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            tvDate.setText(savedInstanceState.getString("Date"));
            tvTime.setText(savedInstanceState.getString("Time"));
            tvRoundDate.setText(savedInstanceState.getString("DateRound"));
            tvRoundTime.setText(savedInstanceState.getString("TimeRound"));
        }

    }
    //place Code
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // check place request code
        if (requestCode == StartPointFlag) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                placeStartPoint = Autocomplete.getPlaceFromIntent(data);

                if (placeStartPoint.getLatLng() != null) {
                    etStartPoint.setText(placeStartPoint.getName());
                }
            }
            else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                assert data != null;
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("STATUS", status.getStatusMessage());
            }
            return;
        }
        else if (requestCode == ENDPOINTFlag) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                placeEndPoint  = Autocomplete.getPlaceFromIntent(data);
                etEndPoint.setText(placeEndPoint.getName());
            }
            else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("STATUS", status.getStatusMessage());
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Method to handle place
    private void placesAutocompletes(int flag) {
        List<Place.Field> fieldList= Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY
                ,fieldList).build(requireContext());

        startActivityForResult(intent, flag);
    }

    //Date
    public void Date(TextView textViewDate1, int check, Calendar incomingCal) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (datePicker, year, month, day) -> {
            int nowYear;
            int nowMonth;
            int nowDay;
            month = month + 1;
            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            Log.i("Check Date",date);
            if(check == 1) {
                String[] dateTotal = date.split("-");
                nowYear = Integer.parseInt(dateTotal[2]);
                nowMonth = Integer.parseInt(dateTotal[1]);
                nowDay = Integer.parseInt(dateTotal[0]);
            }
            else{
                String oneTripDate = tvDate.getText().toString();
                String[] oneTripDateSplits = oneTripDate.split("-");
                nowYear = Integer.parseInt(oneTripDateSplits[2]);
                nowMonth = Integer.parseInt(oneTripDateSplits[1]);
                nowDay = Integer.parseInt(oneTripDateSplits[0]);
            }
            if(year == nowYear && month == nowMonth && day== nowDay){
                if(check==1)
                    isDateToday=true;
                else
                    isDateTodayRoundTrip=true;
            }else{
                if(check==1)
                    isDateToday=false;
                else
                    isDateTodayRoundTrip=false;
            }
            if (year > nowYear) {
                if (check == 1) {
                    isDateCorrect = true;
                } else {
                    isDateCorrectRoundTrip = true;

                }
                //calender
                incomingCal.set(Calendar.DAY_OF_MONTH,day);
                incomingCal.set(Calendar.MONTH,month-1);
                incomingCal.set(Calendar.YEAR,year);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                textViewDate1.setText(format.format(incomingCal.getTime()));
            }else if(year == nowYear){
                if (month > nowMonth) {

                    if (check == 1) {
                        isDateCorrect = true;
                    } else {
                        isDateCorrectRoundTrip = true;
                    }
                    //calender
                    incomingCal.set(Calendar.DAY_OF_MONTH, day);
                    incomingCal.set(Calendar.MONTH, month - 1);
                    incomingCal.set(Calendar.YEAR, year);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                    textViewDate1.setText(format.format(incomingCal.getTime()));

                } else if(month == nowMonth){

                    if (day >= nowDay) {

                        if (check == 1) {
                            isDateCorrect = true;
                        } else {
                            isDateCorrectRoundTrip = true;
                        }
                        //calender
                        incomingCal.set(Calendar.DAY_OF_MONTH,day);
                        incomingCal.set(Calendar.MONTH,month-1);
                        incomingCal.set(Calendar.YEAR,year);
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                        textViewDate1.setText(format.format(incomingCal.getTime()));

                    } else {
                        Toast.makeText(getContext(), "Date is wrong", Toast.LENGTH_SHORT).show();
                        if (check == 1) {
                            isDateCorrect = false;
                        } else {
                            isDateCorrectRoundTrip = false;
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Date is wrong", Toast.LENGTH_SHORT).show();
                    if (check == 1) {
                        isDateCorrect = false;
                    } else {
                        isDateCorrectRoundTrip = false;
                    }
                }
            } else {
                Toast.makeText(getContext(), "Date is wrong", Toast.LENGTH_SHORT).show();
                if (check == 1) {
                    isDateCorrect = false;
                } else {
                    isDateCorrectRoundTrip = false;
                }
            }
        }, year, month, day);
        datePickerDialog.show();
    }
    //Time
    public void Time(TextView textViewTime1, int check, Calendar incomingCal) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (timePicker, selectedHours, selectedMinute) -> {
            int nowHour;
            int nowMin;

            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00"));
            Date currentLocalTime = cal.getTime();
            @SuppressLint("SimpleDateFormat") DateFormat date = new SimpleDateFormat("HH:mm");
            String localTime = date.format(currentLocalTime);
            if(check==1){
                String[] localTimeSplit = localTime.split(":");
                nowHour = Integer.parseInt(localTimeSplit[0]);
                nowMin =  Integer.parseInt(localTimeSplit[1]);
            }
            else{
                String[] localTimeFirstTrip = tvTime.getText().toString().split(":");
                nowHour = Integer.parseInt(localTimeFirstTrip[0]);
                nowMin = Integer.parseInt(localTimeFirstTrip[1]);
            }
            if (isDateToday || isDateTodayRoundTrip) {
                if (selectedHours > nowHour) {
                    if (check == 1) {
                        isTimeCorrect = true;
                    } else {
                        isTimeCorrectRoundTrip = true;
                    }

                    incomingCal.set(Calendar.HOUR_OF_DAY,selectedHours);
                    incomingCal.set(Calendar.MINUTE,selectedMinute);
                    incomingCal.set(Calendar.SECOND,0);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    textViewTime1.setText(format.format(incomingCal.getTime()));
                } else {
                    if (selectedHours == nowHour) {
                        if (selectedMinute > nowMin) {
                            if (check == 1)
                                isTimeCorrect = true;
                            else
                                isTimeCorrectRoundTrip = true;

                            incomingCal.set(Calendar.HOUR_OF_DAY,selectedHours);
                            incomingCal.set(Calendar.MINUTE,selectedMinute);
                            incomingCal.set(Calendar.SECOND,0);
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                            textViewTime1.setText(format.format(incomingCal.getTime()));

                        } else {
                            Toast.makeText(getContext(), "Time is not correct", Toast.LENGTH_SHORT).show();
                            if (check == 1)
                                isTimeCorrect = false;
                            else
                                isTimeCorrectRoundTrip = false;
                        }
                    } else {
                        Toast.makeText(getContext(), "Time is not correct", Toast.LENGTH_SHORT).show();
                        if (check == 1)
                            isTimeCorrect = false;
                        else
                            isTimeCorrectRoundTrip = false;
                    }

                }
            }else{
                incomingCal.set(Calendar.HOUR_OF_DAY,selectedHours);
                incomingCal.set(Calendar.MINUTE,selectedMinute);
                incomingCal.set(Calendar.SECOND,0);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                textViewTime1.setText(format.format(incomingCal.getTime()));
                sharedPreferenceSaveData();
            }
        }, 12, 0, false);
        timePickerDialog.show();
    }

    public void sharedPreferenceSaveData(){

        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putLong("CalendarNormal",calenderNormal.getTimeInMillis());
        editor.putLong("CalendarRound",calendarRound.getTimeInMillis());
        editor.apply();
    }
    //saveDataButton
    public void checkData(){
        if(!TextUtils.isEmpty(etTripName.getText())){
            etTripName.setError(null);
            if(!TextUtils.isEmpty(etStartPoint.getText())){
                etStartPoint.setError(null);
                if(!TextUtils.isEmpty(etEndPoint.getText())){
                    etEndPoint.setError(null);
                    if(!TextUtils.isEmpty(tvDate.getText())){
                        tvDate.setError(null);
                        if(!TextUtils.isEmpty(tvTime.getText())){
                            tvTime.setError(null);
                            //Edit trip
                            if(AddActivity.key==2){

                                new Thread(() -> {
                                    if (placeEndPoint==null) {
                                          Home_Activity.database.tripDAO().EditTrip(AddActivity.ID,
                                                  etTripName.getText().toString(), etStartPoint.getText().toString(),
                                                  etEndPoint.getText().toString(), selectedTrip.getEndPointLatitude(),
                                                  selectedTrip.getEndPointLongitude(), tvDate.getText().toString(),
                                                  tvTime.getText().toString(),calenderNormal.getTimeInMillis());
                                    }else {
                                          Home_Activity.database.tripDAO().EditTrip(AddActivity.ID, etTripName.getText().toString(),
                                                  etStartPoint.getText().toString(),etEndPoint.getText().toString(), placeEndPoint.getLatLng().latitude
                                                    ,placeEndPoint.getLatLng().longitude, tvDate.getText().toString(),
                                                   tvTime.getText().toString(),calenderNormal.getTimeInMillis());
                                           }
                                    requireActivity().finish();
                                  
                                }).start();
                                return;
                            }

                            if (resultNotes != null) {
                                //Create first Trip
                                trip = new Trip(Home_Activity.fireBaseUserId, etTripName.getText().toString(), placeStartPoint.getName(), placeStartPoint.getLatLng().latitude,
                                        placeStartPoint.getLatLng().longitude, placeEndPoint.getName(), placeEndPoint.getLatLng().latitude, placeEndPoint.getLatLng().longitude,
                                        tvDate.getText().toString(), tvTime.getText().toString(),
                                        Final.UPCOMING_TRIP_STATUS, sharedPreferences.getLong("CalendarNormal", 0), resultNotes);
                                if (isRound) {
                                    if (!TextUtils.isEmpty(tvRoundDate.getText())) {
                                        tvRoundDate.setError(null);
                                        if (!TextUtils.isEmpty(tvRoundTime.getText())) {
                                            tvRoundTime.setError(null);
                                            //Create Second Trip
                                            Trip tripRound = new Trip(Home_Activity.fireBaseUserId, etTripName.getText().toString() + " Round", placeEndPoint.getName(), placeEndPoint.getLatLng().latitude, placeEndPoint.getLatLng().longitude,
                                                    placeStartPoint.getName(), placeStartPoint.getLatLng().latitude, placeStartPoint.getLatLng().longitude,
                                                    tvRoundDate.getText().toString(), tvRoundTime.getText().toString(),

                                                    Final.UPCOMING_TRIP_STATUS, sharedPreferences.getLong("CalendarRound", 0), resultNotes);
                                            //insert them to database as separate trips
                                            insertRoom(trip);
                                            insertRoom(tripRound);
                                            requireActivity().finish();
                                        } else {
                                            tvRoundTime.setError("Valid Time");
                                            Toast.makeText(getContext(), "Please, Enter Valid Time for round trip", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        tvRoundDate.setError("Valid Date");
                                        Toast.makeText(getContext(), "Please, Enter Valid Date for round trip", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    insertRoom(trip);
                                    getActivity().finish();
                                }
                            }
                            else{
                                trip = new Trip(Home_Activity.fireBaseUserId, etTripName.getText().toString(), placeStartPoint.getName(), placeStartPoint.getLatLng().latitude,
                                        placeStartPoint.getLatLng().longitude, placeEndPoint.getName(), placeEndPoint.getLatLng().latitude, placeEndPoint.getLatLng().longitude,
                                        tvDate.getText().toString(), tvTime.getText().toString(),
                                        Final.UPCOMING_TRIP_STATUS, calenderNormal.getTimeInMillis(), null);
                                if (isRound) {
                                    if (!TextUtils.isEmpty(tvRoundDate.getText())) {
                                        tvRoundDate.setError(null);
                                        if (!TextUtils.isEmpty(tvRoundTime.getText())) {
                                            tvRoundTime.setError(null);
                                            //create two obj
                                            Trip tripRound = new Trip(Home_Activity.fireBaseUserId, etTripName.getText().toString() + " Round", placeEndPoint.getName(), placeEndPoint.getLatLng().latitude, placeEndPoint.getLatLng().longitude,
                                                    placeStartPoint.getName(), placeStartPoint.getLatLng().latitude, placeStartPoint.getLatLng().longitude,
                                                    tvRoundDate.getText().toString(), tvRoundTime.getText().toString(),Final.UPCOMING_TRIP_STATUS, calendarRound.getTimeInMillis(), null);

                                            insertRoom(trip);
                                            insertRoom(tripRound);
                                            requireActivity().finish();

                                        } else {
                                            tvRoundTime.setError("Invalid Time");
                                            Toast.makeText(getContext(), "Please, Enter Valid Time for round trip", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        tvRoundDate.setError("Invalid Date");
                                        Toast.makeText(getContext(), "Please, Enter Valid Date for round trip", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    insertRoom(trip);
                                    requireActivity().finish();
                                }
                            }

                        }
                        else{
                            tvTime.setError("Valid Time");
                            Toast.makeText(getContext(),"Please, Enter Valid Time",Toast.LENGTH_SHORT).show(); }
                    }
                    else{
                        tvDate.setError("Please Enter Valid Date");
                        Toast.makeText(getContext(),"Please, Enter Valid Date",Toast.LENGTH_SHORT).show();}
                }
                else{
                    etStartPoint.setError(" Required End Point");
                    Toast.makeText(getContext(),"Please, Enter End Point",Toast.LENGTH_SHORT).show(); }
            }
            else{
                etEndPoint.setError("Please Enter Valid Start Point");
                Toast.makeText(getContext(),"Please, Enter Start Point",Toast.LENGTH_SHORT).show();}
        }
        else{
            etTripName.setError("Required");
            Toast.makeText(getContext(),"Please, Enter Trip Name",Toast.LENGTH_SHORT).show(); }
    }






    //put data on room
    public void insertRoom(Trip trip) {
        new Thread(() -> Home_Activity.database.tripDAO().insert(trip)).start();
    }

    //get data from room
    private class getRoomData extends AsyncTask<Void, Void, Trip> {

        @Override
        protected Trip doInBackground(Void... voids) {
            return Home_Activity.database.tripDAO().selectById(AddActivity.ID);



        }
        @Override
        protected void onPostExecute(Trip trip) {
            super.onPostExecute(trip);
            selectedTrip = trip;
            if (selectedTrip!=null) {
                etTripName.setText(selectedTrip.getTripName());
                etStartPoint.setText(selectedTrip.getStartPoint());
                etEndPoint.setText(selectedTrip.getEndPoint());
                tvDate.setText(selectedTrip.getDate());
                tvTime.setText(selectedTrip.getTime());
            }
        }
    }

    private class alarmWork extends AsyncTask<Void, Void, List<Trip>> {

        @Override
        protected List<Trip> doInBackground(Void... voids) {
            return Home_Activity.database.tripDAO().selectUpcomingTrip(Home_Activity.fireBaseUserId, Final.UPCOMING_TRIP_STATUS);
        }
        @Override
        protected void onPostExecute(List<Trip> trips) {
            super.onPostExecute(trips);
            for(int i=0;i<trips.size();i++){
                if(trips.get(i).getTripStatus().equals( Final.UPCOMING_TRIP_STATUS)){
                    try {
                        WorkManagerRepository.setWorkers(trips);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    //declare variable
    private void initComponent() {
        etTripName = view.findViewById(R.id.ediTxt_tripName);
        tvTripName = view.findViewById(R.id.txtView_tripName);
        tvDate = view.findViewById(R.id.txtView_date);
        imageButtonDate = view.findViewById(R.id.btn_date);
        tvTime = view.findViewById(R.id.txtview_time);
        imageButtonTime = view.findViewById(R.id.btn_time);
        etStartPoint = view.findViewById(R.id.editTxt_startPoint);
        etStartPoint.setFocusable(false);
        etEndPoint = view.findViewById(R.id.editTxt_endPoint);
        etEndPoint.setFocusable(false);
        tvRoundTime = view.findViewById(R.id.txtView_Time2);
        tvRoundDate = view.findViewById(R.id.txtView_Date2);
        imageButtonRoundDate = view.findViewById(R.id.btn_date2);
        imageButtonRoundDate.setFocusable(false);
        imageButtonRoundTime = view.findViewById(R.id.btn_time2);
        imageButtonRoundTime.setFocusable(false);
        radioGroup=view.findViewById(R.id.radioGroup_typeTrip);
        radioButtonOneDirection = view.findViewById(R.id.radioBtn_oneDirection);
        radioButtonRoundTrip = view.findViewById(R.id.radioBtn_roundTrip);
        btnAddTrip = view.findViewById(R.id.btn_saveTrip);
        btnAddNotes = view.findViewById(R.id.btn_addNotes);
        constraintLayoutRoundTrip=view.findViewById(R.id.constraintLayoutAddRound);

    }



    //declare Variables
    EditText etTripName,etStartPoint,etEndPoint;
    TextView tvTripName,tvDate,tvRoundDate,tvTime, tvRoundTime;
    Button btnAddTrip;
    ImageButton btnAddNotes,imageButtonDate,imageButtonRoundDate
            ,imageButtonTime,imageButtonRoundTime;
    RadioButton radioButtonOneDirection,radioButtonRoundTrip;
    RadioGroup radioGroup;
    ConstraintLayout constraintLayoutRoundTrip;
    View view;
    //for place Api (link-startActivity flag for start and end)
    private static final int StartPointFlag = 1;
    private static final int ENDPOINTFlag = 2;
    Place placeStartPoint,placeEndPoint;
    //Calender
    Calendar calender = Calendar.getInstance();
    final int year = calender.get(Calendar.YEAR);
    final int month = calender.get(Calendar.MONTH);
    final int day = calender.get(Calendar.DAY_OF_MONTH);
    Calendar calenderNormal;
    Calendar calendarRound;
    SharedPreferences sharedPreferences;

    public static final String PREF_NAME="MY_PREF";
    boolean isDateCorrect = false;
    boolean isTimeCorrect = false;
    boolean isDateToday=false;
    Boolean isRound = false;
    boolean isDateCorrectRoundTrip = false;
    boolean isTimeCorrectRoundTrip = false;
    boolean isDateTodayRoundTrip=false;
    boolean isFirstTimeSelected =false;
    boolean isFirstAddNotes=true;
    Trip trip;
    Trip selectedTrip;
   // List<Trip>tripList;
    ArrayList<String> resultNotes;
}
