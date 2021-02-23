package com.example.customcalendarevent;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyGridAdapter extends ArrayAdapter {
    ArrayList<Date> dates;
    Calendar currentDate;
    ArrayList<Event> events;
    LayoutInflater inflater;

    public MyGridAdapter(@NonNull Context context, ArrayList<Date> dates, Calendar currentDate, ArrayList<Event> events) {
        super(context, R.layout.cell_layout);

        this.dates = dates;
        this.currentDate = currentDate;
        this.events = events;
        inflater= LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Date monthDate = dates.get(position);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);
        int dayNo = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCalendar.get(Calendar.MONTH)+1;// January is month 0 :)
        int displayYear = dateCalendar.get(Calendar.YEAR);

        int currentMonth= currentDate.get(Calendar.MONTH)+1;
        int currentYear= currentDate.get(Calendar.YEAR);

        View view = convertView;
        if(view == null){
            view = inflater.inflate(R.layout.cell_layout,parent,false);
        }

        if(displayMonth == currentMonth && displayYear == currentYear)
            view.setBackgroundColor(getContext().getResources().getColor(R.color.green));

        else view.setBackgroundColor(Color.parseColor("#d9d8d7"));

        TextView day_number = view.findViewById(R.id.calendar_day);
        TextView event_number = view.findViewById(R.id.events_id);

        day_number.setText(String.valueOf(dayNo));
        Calendar eventCalendar = Calendar.getInstance();
        ArrayList<String> list = new ArrayList<>();
        for(int i = 0; i <events.size(); i++){
            eventCalendar.setTime(convertStringToDate(events.get(i).getDATE()));
            if(dayNo == eventCalendar.get(Calendar.DAY_OF_MONTH)&& displayMonth == eventCalendar.get(Calendar.MONTH)+1&&
            displayYear == eventCalendar.get(Calendar.YEAR)){
                list.add(events.get(i).getEVENT());
                event_number.setText(list.size()+" Events");//number of events in that specific day :)
            }
        }



        return view;
    }

    private Date convertStringToDate(String eventDate){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);
        Date date = null;
        try{
            date = format.parse(eventDate);
        } catch (ParseException e){
            e.printStackTrace();// tells you what happened and where in the code this happened
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
