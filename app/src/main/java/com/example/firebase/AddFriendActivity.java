package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddFriendActivity extends AppCompatActivity {
    public FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    EditText friendEmailId, userName;
    Button addFriend, mDate;
    AlertDialog alertDialog;
    DatePickerDialog.OnDateSetListener myDateSetListener;
    ImageButton backToMyCalendar, btnRemind;
    FirebaseFirestore db = CustomCalendarView.db;
    String TAG = "Bug Test: ";

    String userEmail;
    static String endDate;
    static String endMonth;
    static String endYear;
    static String getFriendEmail;
    LinearLayout linear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        friendEmailId = findViewById(R.id.editMail);
        addFriend = findViewById(R.id.addBtn);
        userName = findViewById(R.id.editname);
        mDate = findViewById(R.id.chooseDate);
        linear = findViewById(R.id.activity_addfriends);//!!!!!!!
        backToMyCalendar = findViewById(R.id.calender_button);
        btnRemind = findViewById(R.id.remind_button);
        if (user != null) {
            userEmail = user.getEmail();
        }

        intializeDatePicker();
        loadFriedns();

        backToMyCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddFriendActivity.this, HomeActivity.class);
                startActivity(i);
            }
        });
        btnRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddFriendActivity.this, RemindActivity.class);
                startActivity(i);
            }
        });

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String uEmail = userEmail;
                final String friendEmail = friendEmailId.getText().toString();
                final String suserName = userName.getText().toString();

                if (friendEmail.isEmpty() || suserName.isEmpty()) {
                    Toast.makeText(AddFriendActivity.this, "Please input email and name!", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
                    mFirebaseAuth.fetchSignInMethodsForEmail(friendEmail)
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    if (task.isSuccessful()) {
                                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                                        if (isNewUser) {
                                            Log.d("Bug Test", "Email does not exists!");
                                            Toast.makeText(AddFriendActivity.this, "Email does not exists! Please try again!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.d("Bug Test", "Is Old User!");
                                            addExistFriend(uEmail, friendEmail, suserName);
                                            Toast.makeText(AddFriendActivity.this, "Add friend: " + friendEmail, Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthInvalidCredentialsException e) {
                                            Toast.makeText(AddFriendActivity.this, "The email address format is wrong! Please try again!", Toast.LENGTH_SHORT).show();
                                            Log.d("Bug", "Invalid Email Address Format.");
                                        } catch (Exception e) {
                                            Log.d("Bug", e.getMessage());
                                        }
                                    }

                                }
                            });
                }


            }
        });
    }
    private void intializeDatePicker(){
        LinearLayout mSpinners = findViewById(AddFriendActivity.this.getResources().getIdentifier("android:id/pickers", null, null));
        if (mSpinners != null) {
            NumberPicker mMonthSpinner =  findViewById(AddFriendActivity.this.getResources().getIdentifier("android:id/month", null, null));
            NumberPicker mYearSpinner = findViewById(AddFriendActivity.this.getResources().getIdentifier("android:id/year", null, null));
            mSpinners.removeAllViews();
            if (mMonthSpinner != null) {
                mSpinners.addView(mMonthSpinner);
            }
            if (mYearSpinner != null) {
                mSpinners.addView(mYearSpinner);
            }
        }
        View dayPickerView = findViewById(AddFriendActivity.this.getResources().getIdentifier("android:id/day", null, null));
        if(dayPickerView != null){
            dayPickerView.setVisibility(View.GONE);
        }


        mDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Calendar cal= Calendar.getInstance();
                int year=cal.get(Calendar.YEAR);
                int month=cal.get(Calendar.MONTH);
                int day=cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddFriendActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,myDateSetListener,year,month,day){
                    @Override
                    protected  void onCreate(Bundle savedInstanceState){
                        super.onCreate(savedInstanceState);
                        LinearLayout mSpinners = (LinearLayout) findViewById(getContext().getResources().getIdentifier("android:id/pickers",null,null));
                        if (mSpinners != null) {
                            NumberPicker mMonthSpinner = (NumberPicker) findViewById(getContext().getResources().getIdentifier("android:id/month", null, null));
                            NumberPicker mYearSpinner = (NumberPicker) findViewById(getContext().getResources().getIdentifier("android:id/year", null, null));
                            mSpinners.removeAllViews();
                            if (mMonthSpinner != null) {
                                mSpinners.addView(mMonthSpinner);
                            }
                            if (mYearSpinner != null) {
                                mSpinners.addView(mYearSpinner);
                            }
                        }
                        View dayPickerView = findViewById(getContext().getResources().getIdentifier("android:id/day", null, null));
                        if(dayPickerView != null){
                            dayPickerView.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onDateChanged(DatePicker view, int year, int month, int day) {
                        super.onDateChanged(view, year, month, day);
                        setTitle("請選擇要同步的月份");
                    }
                };
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
                datePickerDialog.setTitle("請選擇要同步的月份");
            }
        });
        myDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int date) {
                String [] mMonth={"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                if(date<10){
                    endDate = "0"+date;
                }
                else {
                    endDate = String.valueOf(date);
                }
                endYear=String.valueOf(year);
                endMonth = mMonth[month];
            }
        };
    }
    public void loadFriedns(){
        db.collection("MyFriends").document(userEmail).collection("->").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (DocumentSnapshot document : task.getResult()) {
                                creatFriendView(document.get("name").toString(),document.get("friend").toString());
                            }
                        }
                        else{
                            Log.d("damnbuggggg", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void creatFriendView(String name, String email){
        Log.d("name",name);
        Log.d("email", email);
        final LinearLayout friendLayout = new LinearLayout(this);
        friendLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView newText=new TextView(AddFriendActivity.this);
        newText.setText("  ➤"+name);
        newText.setTextSize(25);
        newText.setMinHeight(50);
        newText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

        ImageButton newBtnGet= new ImageButton(AddFriendActivity.this);
        newBtnGet.setTag(email);
        newBtnGet.setImageResource(R.drawable.get);
        newBtnGet.setBackgroundColor(0xACD6FF);
        newBtnGet.setAdjustViewBounds(true);
        newBtnGet.setMaxHeight(150);
        newBtnGet.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        newBtnGet.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        ImageButton newBtnDelete= new ImageButton(AddFriendActivity.this);
        newBtnDelete.setTag(email);
        newBtnDelete.setImageResource(R.drawable.delete);
        newBtnDelete.setBackgroundColor(0xACD6FF);
        newBtnDelete.setAdjustViewBounds(true);
        newBtnDelete.setMaxHeight(150);
        newBtnDelete.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        newBtnDelete.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        friendLayout.addView(newText);
        friendLayout.addView(newBtnGet);
        friendLayout.addView(newBtnDelete);

        linear.addView(friendLayout);

        newBtnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddFriendActivity.this);
                builder.setCancelable(true);
                getFriendEmail=v.getTag().toString();
                final View addView = LayoutInflater.from(AddFriendActivity.this).inflate(R.layout.friend_calendar, null);
                builder.setView(addView);
                alertDialog = builder.create();
                alertDialog.show();
            }
        } );

        final String FriendEmail = newBtnDelete.getTag().toString();
        newBtnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                friendLayout.removeAllViews();

                db.collection("MyFriends").document(userEmail).collection("->")
                        .whereEqualTo("friend", FriendEmail)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                        Log.d("TAG", document.getId() + " => " + document.getData() + " Delete Success!");
                                        Toast.makeText(AddFriendActivity.this, document.get("name").toString() + " deleted",Toast.LENGTH_SHORT).show();
                                        document.getReference().delete();
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });
    }
    public void addExistFriend(String userEmail, String friendEmail, String suserName) {
        Log.d(" addExistFriend", "innnnnnn");
        Map<String, Object> dataToSave = new HashMap<String, Object>();
        dataToSave.put("friend", friendEmail);
        dataToSave.put("name", suserName);
        db.collection("MyFriends").document(userEmail).collection("->")
                .add(dataToSave)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("AddFriendActivity ", "DocumentSnapshot written with ID: " + documentReference.getId());
                        //Toast.makeText(AddFriendActivity.this,userEmail+" "+friendEmail,Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("AddFriendActivity ", "Error adding document", e);
                        //Toast.makeText(AddFriendActivity.this,"fail "+e,Toast.LENGTH_SHORT).show();
                    }
                });
        creatFriendView(suserName,friendEmail);
    }

}
