package com.example.firebase;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyGridAdapter extends ArrayAdapter {
    List<Date> dates;
    Calendar currentDate;
    List<Events> events;
    LayoutInflater inflater;
    int choose;//判斷是好友的還是自己的
    int index;//判斷List的第幾個開始是朋友的
    int myColor;//我的顏色


    public MyGridAdapter(Context context, List<Date> dates, Calendar currentDate, List<Events> events, int c, int index){
        super(context, R.layout.single_cell_layout);
        this.dates = dates;
        this.currentDate = currentDate;
        this.events = events;
        this.choose = c;
        this.index = index;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Date monthDate = dates.get(position);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);
        int DayNo = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCalendar.get(Calendar.MONTH)+1;
        int displayYear = dateCalendar.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH)+1;
        int currentYear = currentDate.get(Calendar.YEAR);


        View view = convertView;

        if(view==null){
            view = inflater.inflate(R.layout.single_cell_layout,parent,false);
        }

        if(displayMonth==currentMonth && displayYear==currentYear){
            view.setBackgroundColor(Color.parseColor("#FFECF5"));
        }
        else{
            view.setBackgroundColor(Color.parseColor("#F1E1FF"));
        }
        TextView Day_Number = view.findViewById(R.id.calendar_day);
        TextView EventNumber = view.findViewById(R.id.events_id);
        Day_Number.setText(String.valueOf(DayNo));

        Calendar eventCalendar = Calendar.getInstance();
        ArrayList<String> arrayList = new ArrayList<>();
        if(choose==1){
            for(int i = 0;i < events.size();i++){
                eventCalendar.setTime(ConvertStringToDate(events.get(i).getDATE()));
                if(DayNo == eventCalendar.get(Calendar.DAY_OF_MONTH) && displayMonth == eventCalendar.get(Calendar.MONTH)+1
                        && displayYear == eventCalendar.get(Calendar.YEAR)){
                    arrayList.add(events.get(i).getEVENT());
                    EventNumber.setText(arrayList.size() + " Events");
                    EventNumber.setTextColor(Color.parseColor("#820041"));
                    if(events.get(i).getTYPE().equals("period")){
                        Day_Number.setTextColor(Color.parseColor("#8F4586"));//字
                        Day_Number.setTypeface(Day_Number.getTypeface(), Typeface.BOLD_ITALIC);
                    }
                    else{
                        view.setBackgroundColor(Color.parseColor("#FFD9EC"));
                    }

                }
                myColor = ((ColorDrawable) view.getBackground()).getColor();
            }
        }
        else{
            for(int i = 0;i < events.size();i++){
                eventCalendar.setTime(ConvertStringToDate(events.get(i).getDATE()));
                if(DayNo == eventCalendar.get(Calendar.DAY_OF_MONTH) && displayMonth == eventCalendar.get(Calendar.MONTH)+1
                        && displayYear == eventCalendar.get(Calendar.YEAR) && i < index){
                    arrayList.add(events.get(i).getEVENT());
                    view.setBackgroundColor(Color.parseColor("#FFD9EC"));
                    myColor = ((ColorDrawable) view.getBackground()).getColor();
                }
                else if(DayNo == eventCalendar.get(Calendar.DAY_OF_MONTH) && displayMonth == eventCalendar.get(Calendar.MONTH)+1
                        && displayYear == eventCalendar.get(Calendar.YEAR) && i >= index){
                    arrayList.add(events.get(i).getEVENT());
                    if(((ColorDrawable)view.getBackground()).getColor()==myColor){
                        view.setBackgroundColor(Color.parseColor("#E6CAFF"));
                    }
                    else {
                        view.setBackgroundColor(Color.parseColor("#CECEFF"));
                    }
                }
            }

        }
        return view;
    }

    private Date ConvertStringToDate(String eventDate){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        Date date = null;
        try{
            date = format.parse(eventDate);
        }catch(ParseException e){
            e.printStackTrace();
        }
        return date;
    }


    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return dates.indexOf(item);
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }
}
