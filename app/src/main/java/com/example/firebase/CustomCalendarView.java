package com.example.firebase;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

public class CustomCalendarView extends LinearLayout {
    public FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userEmail;
    ImageButton NextButton, PreviousButton;
    TextView CurrentDate;
    DatePickerDialog.OnDateSetListener myDateSetListener;
    GridView gridView;
    private static final int MAX_CALENDAR_DAYS=42;
    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    Context context;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy",Locale.ENGLISH);
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM",Locale.ENGLISH);
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy",Locale.ENGLISH);
    SimpleDateFormat eventDateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);

    int alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinute;

    MyGridAdapter myGridAdapter;
    AlertDialog alertDialog;
    List<Date> dates = new ArrayList<>();
    List<Events> eventLoadList = new ArrayList<>();

    String endDate;
    String endMonth;
    String endYear;
    String type;

    private static final String TAG = "Bug Test";
    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public CustomCalendarView(Context context) {
        super(context);
    }

    public CustomCalendarView(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if(user!=null){
            userEmail = user.getEmail();
        }
        InitializeLayout();
        SetUpCalendar();

        PreviousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                SetUpCalendar();
            }
        });
        NextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1);
                SetUpCalendar();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                final View addView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_newevent_layout, null);
                final EditText EventName = addView.findViewById(R.id.eventname);
                final TextView EventTime = addView.findViewById(R.id.eventtime);
                final TextView ShowEndLine = addView.findViewById(R.id.showEndLine);
                final RadioGroup radioGroup = addView.findViewById(R.id.settype);
                final RadioButton period=addView.findViewById(R.id.period);

                final String date = eventDateFormat.format(dates.get(position));
                final String month = monthFormat.format(dates.get(position));
                final String year = yearFormat.format(dates.get(position));

                endDate = new SimpleDateFormat("dd",Locale.ENGLISH).format(dates.get(position));
                endMonth = new SimpleDateFormat("MM",Locale.ENGLISH).format(dates.get(position));
                endYear = new SimpleDateFormat("yyyy",Locale.ENGLISH).format(dates.get(position));

                final ImageButton SetTime = addView.findViewById(R.id.seteventtime);
                Button AddEvent = addView.findViewById(R.id.addevent);
                //qqqqqqqq
                Button setEndDate = addView.findViewById(R.id.setenddate);
                ShowEndLine.setText(endYear+"-"+endMonth+"-"+endDate);

                //new
                Calendar dateCalendar = Calendar.getInstance();
                dateCalendar.setTime(dates.get(position));
                alarmYear = dateCalendar.get(Calendar.YEAR);
                alarmMonth = dateCalendar.get(Calendar.MONTH);
                alarmDay = dateCalendar.get(Calendar.DAY_OF_MONTH);

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {//!!!!!!!!!!
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if(period.isChecked()){
                            //Toast.makeText(context,"22222",Toast.LENGTH_SHORT).show();
                            EventName.setVisibility(View.GONE);
                            EventTime.setVisibility(View.GONE);
                            SetTime.setVisibility(View.GONE);
                        }
                        else{
                            EventName.setVisibility(View.VISIBLE);
                            EventTime.setVisibility(View.VISIBLE);
                            SetTime.setVisibility(View.VISIBLE);
                        }
                    }
                });

                SetTime.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        int hours = calendar.get(Calendar.HOUR_OF_DAY);
                        final int minutes = calendar.get(calendar.MINUTE);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(addView.getContext(), R.style.Theme_AppCompat_Dialog
                                , new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                c.set(Calendar.MINUTE,minute);
                                c.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
                                SimpleDateFormat hformat = new SimpleDateFormat("K:mm a", Locale.ENGLISH);
                                String event_Time = hformat.format(c.getTime());
                                EventTime.setText(event_Time);
                                //new
                                alarmHour = c.get(Calendar.HOUR_OF_DAY);
                                alarmMinute = c.get(Calendar.MINUTE);
                            }
                        },hours,minutes,false);
                        timePickerDialog.show();
                    }
                });

                //qqqqqqqq
                setEndDate.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        DatePickerDialog datePickerDialog = new DatePickerDialog(addView.getContext(),
                                android.R.style.Theme_Holo_Light_Dialog_MinWidth,myDateSetListener,Integer.valueOf(endYear),Integer.valueOf(endMonth)-1,Integer.valueOf(endDate));
                        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        datePickerDialog.show();
                    }
                });

                myDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int date) {
                        if(date<10){
                            endDate = "0"+String.valueOf(date);
                        }
                        else {
                            endDate = String.valueOf(date);
                        }
                        if(month+1<10){
                            endMonth = "0" + String.valueOf(month+1);
                        }
                        else {
                            endMonth = String.valueOf(month + 1);
                        }
                        endYear = String.valueOf(year);

                        ShowEndLine.setText(endYear+"-"+endMonth+"-"+endDate);
                    }
                };
                AddEvent.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type = ((RadioButton)(addView.findViewById(radioGroup.getCheckedRadioButtonId()))).getText().toString();
                        //new
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(alarmYear,alarmMonth,alarmDay,alarmHour,alarmMinute);

                        AddEventToFirestore(EventName.getText().toString(), EventTime.getText().toString(), date, month, year, endDate, endMonth, endYear, type);
                        SaveDuration(EventName.getText().toString(),EventTime.getText().toString(),date,month,year,endDate,endMonth,endYear,type);
                        SetUpCalendar();
                        alertDialog.dismiss();
                    }
                });
                builder.setView(addView);
                alertDialog = builder.create();
                alertDialog.show();

            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String date = eventDateFormat.format(dates.get(position));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View showView = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_layout,null);
                RecyclerView recyclerView = showView.findViewById(R.id.EventsRV);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(showView.getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                EventRecyclerAdapter eventRecyclerAdapter = new EventRecyclerAdapter(showView.getContext(),
                        loadFirestoreDate(date));
                recyclerView.setAdapter(eventRecyclerAdapter);
                eventRecyclerAdapter.notifyDataSetChanged();

                builder.setView(showView);
                alertDialog = builder.create();
                alertDialog.show();

                //here
                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        SetUpCalendar();
                    }
                });
                return true;
            }
        });


    }

    public CustomCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //new
    private void setAlarm(String event,String time,String year,String month,String day,int requestCode){
        Intent intent = new Intent(context.getApplicationContext(),AlarmReceiver.class);
        Calendar c = Calendar.getInstance();
        int intMonth=0;
        String [] sMonth={"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        for(int i=0;i<12;i++){
            if(month.equals(sMonth[i])){
                intMonth=i;
            }
        }

        String[] temp = time.split(":");
        int hour, minute;

        if(time.charAt(time.length()-2)=='A'){
            hour = Integer.valueOf(temp[0]);
            minute = Integer.valueOf(temp[1].split(" ")[0]);
        }
        else{
            hour = Integer.valueOf(temp[0])+12;
            minute = Integer.valueOf(temp[1].split(" ")[0]);
        }

        c.set(Integer.valueOf(year), intMonth, Integer.valueOf(day),hour,minute,0 );
        Log.d("tag",c.getTime().toString());
        intent.putExtra("event",event);
        intent.putExtra("time",time);
        intent.putExtra("id",requestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,requestCode,intent,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        //提早半小時提醒使用者
        alarmManager.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis()-30*60*1000,pendingIntent);
    }


    //qqqqqqqqqqqqq
    private void SaveDuration(String event,String time, String date,String month,String year, String endDate, String endMonth, String endYear, String type){
        String str = date;
        int startDay = Integer.valueOf(str.substring(8,10));
        int startMonth = Integer.valueOf(str.substring(5,7));

        Calendar cal = new GregorianCalendar(Integer.valueOf(year),startMonth-1,startDay,0,0,0);
        Date d1 = new GregorianCalendar(Integer.valueOf(year),startMonth-1,startDay,0,0,0).getTime();
        Date d2 = new GregorianCalendar(Integer.valueOf(endYear),Integer.valueOf(endMonth)-1,Integer.valueOf(endDate),0,0,0).getTime();

        long days = (d2.getTime()-d1.getTime())/(1000*60*60*24);
        for(int i = 1 ; i<=days;i++){
            cal.add(Calendar.DAY_OF_MONTH,1);
            AddEventToFirestore(event,time,eventDateFormat.format(cal.getTime()),monthFormat.format(cal.getTime()),yearFormat.format(cal.getTime()), endDate, endMonth, endYear, type);
        }

    }


    private void InitializeLayout(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendar_layout, this);
        PreviousButton = view.findViewById(R.id.previousBtn);
        NextButton = view.findViewById(R.id.nextBtn);
        CurrentDate = view.findViewById(R.id.current_Date);
        gridView = view.findViewById(R.id.gridview);
    }

    private void SetUpCalendar(){
        loadFirestorePerMonth(monthFormat.format(calendar.getTime()), yearFormat.format(calendar.getTime()));
    }

    private void AddEventToFirestore(String event, String time, String date, String month, String year, String endDate, String endMonth, String endYear, String type) {

        Map<String, Object> dataToSave = new HashMap<String, Object>();
        dataToSave.put("event", event);
        dataToSave.put("time", time);
        dataToSave.put("date", date);
        dataToSave.put("month", month);
        dataToSave.put("year", year);
        dataToSave.put("endDate", endDate);
        dataToSave.put("endMonth", endMonth);
        dataToSave.put("endYear", endYear);
        dataToSave.put("type", type);

        db.collection("Events").document(userEmail).collection(year + month).add(dataToSave)  //會被蓋掉嗎???
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        int intCode = (int)(System.currentTimeMillis() % Integer.MAX_VALUE);    //不重複的notifyID
                        //Log.d(TAG, "intCode: " + intCode);
                        //Alarm 結合 Notification : 在時間到達時發出通知來提醒使用者
                        setAlarm(event,time,year,month,date.substring(8,10),intCode);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void loadFirestorePerMonth(String Month, String year){//!!!!!!!!!!
        eventLoadList.clear();
        String currentDate = dateFormat.format(calendar.getTime());
        CurrentDate.setText(currentDate);
        dates.clear();

        db.collection("Events").document(userEmail).collection(year+Month)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                eventLoadList.add(new Events(document.get("event").toString(), document.get("time").toString(), document.get("date").toString(), document.get("month").toString(),document.get("year").toString(),document.get("endDate").toString(),document.get("endMonth").toString(),document.get("endYear").toString(),document.get("type").toString()));
                            }
                        }
                        else {
                            Log.d("damnbuggggg", "Error getting documents: ", task.getException());
                        }


                        Calendar monthCalendar = (Calendar) calendar.clone();
                        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
                        int FirstDayofMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;
                        monthCalendar.add(Calendar.DAY_OF_MONTH, -FirstDayofMonth);
                        while (dates.size() < MAX_CALENDAR_DAYS) {
                            dates.add(monthCalendar.getTime());
                            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
                        }
                        myGridAdapter = new MyGridAdapter(context, dates, calendar, eventLoadList, 1, eventLoadList.size());
                        gridView.setAdapter(myGridAdapter);

                    }
                });
    }
    private ArrayList<Events> loadFirestoreDate(String date){
        ArrayList<Events> events=new ArrayList();//有近來這
        for(int i=0;i<eventLoadList.size();i++){//沒進來..........
            if(eventLoadList.get(i).getDATE().equals(date)){
                events.add(eventLoadList.get(i));
            }
        }
        return events;
    }
}
