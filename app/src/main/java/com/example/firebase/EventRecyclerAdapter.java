package com.example.firebase;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.MyViewHolder>{

    Context context;
    ArrayList<Events>arrayList;
    String userEmail;

    private static final String TAG = "Bug Test";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String[] englishMonth = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    public EventRecyclerAdapter(Context context, ArrayList<Events> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_rowlayout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final Events events = arrayList.get(position);
        holder.Event.setText((events.getEVENT()));
        holder.DateTxt.setText((events.getDATE()));
        holder.Time.setText((events.getTIME()));
        holder.EndLine.setText((events.getENDYEAR()+"-"+events.getENDMONTH()+"-"+events.getENDDATE()));
        holder.EventType.setText(events.getTYPE());
        holder.delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                initUserEmail();
                deleteCalendarEventFromFirebase(events.getEVENT(),events.getYEAR(), events.getMONTH(), events.getDATE(), events.getTIME(), events.getENDDATE(), events.getENDMONTH(), events.getENDYEAR());
                arrayList.remove(position);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView DateTxt,Event,Time,EndLine,EventType;
        //here
        Button delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            DateTxt = itemView.findViewById(R.id.eventdate);
            Event = itemView.findViewById(R.id.eventname);
            Time = itemView.findViewById(R.id.eventtime);
            EndLine = itemView.findViewById(R.id.endline);
            EventType = itemView.findViewById(R.id.eventtype);
            delete = itemView.findViewById(R.id.delete);
        }
    }

    private void deleteCalendarEventFromFirebase(String event,String year, String month, String date, String time, String endDate, String endMonth, String endYear){
        do {
            db.collection("Events").document(userEmail).collection(year + month)
                    .whereEqualTo("event", event)
                    .whereEqualTo("time", time)
                    .whereEqualTo("endDate", endDate)
                    .whereEqualTo("endMonth", endMonth)
                    .whereEqualTo("endYear", endYear)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                    Log.d("TAG", document.getId() + " => " + document.getData() + " Delete Success!");
                                    document.getReference().delete();

                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
            if(month.equals("September")){
                month = "January";
                year = String.valueOf(Integer.valueOf(year)+1);
            }
            else {
                int temp = Arrays.asList(englishMonth).indexOf(month);
                month = englishMonth[temp+1];
            }
        }while(!month.equals(englishMonth[Integer.valueOf(endMonth)]) || !year.equals(endYear));
    }

    private void initUserEmail() {
        if(MainActivity.user!=null) {
            userEmail = MainActivity.user.getEmail();
        }
    }

}
