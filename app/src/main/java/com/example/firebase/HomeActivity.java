package com.example.firebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;



public class HomeActivity extends AppCompatActivity {
    Button btnLogout;
    ImageButton btnFriend, btnRemind;
    public static final String RESULT = "result";
    public static final String EVENT = "event";
    private static final int ADD_NOTE = 44;

    CustomCalendarView customCalendarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toast.makeText(HomeActivity.this, "Welcome to shared calendar!", Toast.LENGTH_SHORT).show();
        btnFriend=findViewById(R.id.friends_buttontest);
        btnRemind=findViewById(R.id.remind_buttontest);
        btnRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(HomeActivity.this, RemindActivity.class);
                startActivity(i);
            }
        });
        btnFriend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i=new Intent(HomeActivity.this, AddFriendActivity.class);
                startActivity(i);
            }
        });

        btnLogout=findViewById(R.id.logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intToMain=new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intToMain);
                //customCalendarView = (CustomCalendarView)findViewById(R.id.custom_calendar_view);
            }
        });
        //customCalendarView = (CustomCalendarView)findViewById(R.id.custom_calendar_view);
    }
}
