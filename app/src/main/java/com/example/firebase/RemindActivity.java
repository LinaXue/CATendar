package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class RemindActivity extends AppCompatActivity {
    ImageButton backToMyCalendar, btnFriend;
    List<Events> eventLoadList=new ArrayList<>();
    List<Date> dates = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind);
        backToMyCalendar=findViewById(R.id.calender_buttontest);
        btnFriend=findViewById(R.id.friends_buttontest);

        backToMyCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(RemindActivity.this, HomeActivity.class);
                startActivity(i);
            }
        });
        btnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(RemindActivity.this, AddFriendActivity.class);
                startActivity(i);
            }
        });
        loadThisWeekEvent();
    }
    public void loadThisWeekEvent(){
        FirebaseFirestore db=CustomCalendarView.db;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Calendar c = Calendar.getInstance();
        String userEmail="";
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        String month=monthFormat.format(c.getTime());
        String year=yearFormat.format(c.getTime());
        if(user!=null){
            userEmail=user.getEmail();
        }
        // Set the calendar to Sunday of the current week
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        // Print dates of the current week starting on Sunday
        for (int i = 0; i < 7; i++) {
            //System.out.println(dateFormat.format(c.getTime()));
            dates.add(c.getTime());
            c.add(Calendar.DATE, 1);
        }
        db.collection("Events").document(userEmail).collection(year+month)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                for(int i=0;i<7;i++){
                                    if((document.get("date").toString()).equals(dateFormat.format(dates.get(i)))){
                                        if(!document.get("type").toString().equals("period")){
                                            eventLoadList.add(new Events(document.get("event").toString(), document.get("time").toString(), document.get("date").toString(), document.get("month").toString(), document.get("year").toString(), document.get("endDate").toString(), document.get("endMonth").toString(),document.get("endYear").toString(),document.get("type").toString()));
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d("damnbuggggg", "Error getting documents: ", task.getException());
                        }
                        LinearLayout linear=findViewById(R.id.remind_events);
                        for(int i=0;i<eventLoadList.size();i++){
                            TextView newText=new TextView(RemindActivity.this);
                            newText.setText("  ➤"+eventLoadList.get(i).getDATE()+"     "+eventLoadList.get(i).getTIME()+"    "+eventLoadList.get(i).getEVENT());
                            if(eventLoadList.get(i).getTYPE().equals("special")){
                                newText.setTextColor(Color.parseColor("#930093"));
                            }
                            else{
                                newText.setTextColor(Color.parseColor("#000000"));
                            }
                            newText.setTextSize(20);
                            linear.addView(newText);
                        }
                    }
                });
    }
}
