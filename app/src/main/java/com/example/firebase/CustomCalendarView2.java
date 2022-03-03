package com.example.firebase;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomCalendarView2 extends LinearLayout {
    Context context;
    String userEmail;
    String friendEmail=AddFriendActivity.getFriendEmail;//!!!!!!!!!!!!!!
    TextView CurrentDate;
    GridView gridView;
    private static final int MAX_CALENDAR_DAYS = 42;
    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    List<Events> eventLoadList = new ArrayList<>();
    List<Date> dates = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MyGridAdapter myGridAdapter;
    int index = 0;
    public FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public CustomCalendarView2(Context context) {
        super(context);
    }

    public CustomCalendarView2(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if(user!=null){
            userEmail = user.getEmail();
        }
        InitializeLayout();
        SetUpCalendar();
    }

    public CustomCalendarView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void InitializeLayout() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendar_layout2, this);
        CurrentDate = view.findViewById(R.id.current_Date);
        gridView = view.findViewById(R.id.gridview);
    }

    private void SetUpCalendar() {
        loadFirestorePerMonth(AddFriendActivity.endMonth, AddFriendActivity.endYear);//!!!!!!!!!!!!!!!!bug....
    }

    private void loadFirestorePerMonth(String Month, String year){
        if(Month==null||year==null){
            Toast.makeText(context,"choose month & year first",Toast.LENGTH_SHORT).show();
        }
        else{
            eventLoadList.clear();
            String currentDate = dateFormat.format(calendar.getTime());
            CurrentDate.setText(currentDate);
            dates.clear();
            final String mYear=year;
            final String mMonth=Month;
            db.collection("Events").document(userEmail).collection(year+Month)   //自己的
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    if(!document.get("type").toString().equals("period")){
                                        eventLoadList.add(new Events(document.get("event").toString(), document.get("time").toString(), document.get("date").toString(), document.get("month").toString(),document.get("year").toString(),document.get("endDate").toString(),document.get("endMonth").toString(),document.get("endYear").toString(),document.get("type").toString()));
                                    }
                                }
                            }
                            else {
                                Log.d("damnbuggggg", "Error getting documents: ", task.getException());
                                //Toast.makeText(context,"Nooooooooo",Toast.LENGTH_SHORT).show();
                            }

                            int intMonth=0;
                            String [] sMonth={"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                            for(int i=0;i<12;i++){
                                if(mMonth.equals(sMonth[i])){
                                    intMonth=i;
                                }
                            }
                            calendar.set(Calendar.MONTH, intMonth);
                            calendar.set(Calendar.YEAR, Integer.valueOf(mYear));


                            Calendar monthCalendar = (Calendar) calendar.clone();
                            monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
                            int FirstDayofMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;
                            monthCalendar.add(Calendar.DAY_OF_MONTH, -FirstDayofMonth);
                            while (dates.size() < MAX_CALENDAR_DAYS) {
                                dates.add(monthCalendar.getTime());
                                monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
                            }
                            gridView.setAdapter(myGridAdapter);
                            loadFriendPerMonth(mMonth, mYear);
                        }
                    });
        }
    }

    private void loadFriendPerMonth(String Month, String year){
        index = eventLoadList.size();
        String currentDate = dateFormat.format(calendar.getTime());
        CurrentDate.setText(currentDate);
        db.collection("Events").document(friendEmail).collection(year+Month)   //好友的
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if(!document.get("type").toString().equals("period")){
                                    eventLoadList.add(new Events(document.get("event").toString(), document.get("time").toString(), document.get("date").toString(), document.get("month").toString(),document.get("year").toString(),document.get("endDate").toString(),document.get("endMonth").toString(),document.get("endYear").toString(),document.get("type").toString()));
                                }
                            }
                        }
                        else {
                            Log.d("damnbuggggg", "Error getting documents: ", task.getException());
                        }

                        myGridAdapter = new MyGridAdapter(context, dates, calendar, eventLoadList, 2, index);
                        gridView.setAdapter(myGridAdapter);

                    }
                });
    }

}
