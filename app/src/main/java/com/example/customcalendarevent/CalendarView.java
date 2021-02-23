package com.example.customcalendarevent;


import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarView extends LinearLayout {
    ImageButton NextButton,PreviousButton;
    TextView CurrentDate;
    GridView GridView;
    private static final int MAX_CALENDAR_DAYS = 42;//42=6 lines of 7 days each:) like regular calendar apps
    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    Context context;
    SimpleDateFormat dateFormat = new SimpleDateFormat( "MMMM yyyy", Locale.ENGLISH);
    SimpleDateFormat monthFormat = new SimpleDateFormat( "MMMM", Locale.ENGLISH);
    SimpleDateFormat yearFormat = new SimpleDateFormat( "yyyy", Locale.ENGLISH);
    SimpleDateFormat eventDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    DBOpenHelper dbOpenHelper;
    MyGridAdapter myGridAdapter;
    AlertDialog alertDialog;
    ArrayList<Date> dates = new ArrayList<>();
    ArrayList<Event> events = new ArrayList<>();


    public CalendarView(Context context) {
        super(context);

    }
    public CalendarView(final Context context, @Nullable AttributeSet attrs) {
        super(context,attrs);
        this.context=context;
        initializeLayout();

        PreviousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                setUpCalendar();
            }
        });

        NextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1);
                setUpCalendar();
            }
        });


        GridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//add event
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                final View addView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_event_layout, null);
                final EditText EventName = addView.findViewById(R.id.event_name);
                final TextView EventTime = addView.findViewById(R.id.event_time);
                Button SetTime = addView.findViewById(R.id.set_event_time);
                Button AddEvent = addView.findViewById(R.id.add_event);
                Button AddImage = addView.findViewById(R.id.buttonLoadPicture);
                SetTime.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        int hours = calendar.get(Calendar.HOUR_OF_DAY);
                        int minutes = calendar.get(Calendar.MINUTE);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(addView.getContext(), R.style.Theme_AppCompat_Dialog,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {                  // new window that pops in to select time
                                        Calendar c = Calendar.getInstance();
                                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        c.set(Calendar.MINUTE, minute);
                                        c.setTimeZone(TimeZone.getDefault());
                                        SimpleDateFormat hformat = new SimpleDateFormat("K:mm a", Locale.ENGLISH);
                                        String event_Time = hformat.format(c.getTime());
                                        EventTime.setText(event_Time);
                                    }
                                }, hours, minutes, true);

                        timePickerDialog.show();
                    }
                });

                ImagePicker imagePicker = new ImagePicker();
                final String date = eventDateFormat.format(dates.get(position));
                final String month = monthFormat.format(dates.get(position));
                final String year = yearFormat.format(dates.get(position));
                final String image =imagePicker.getImageUri();

                AddEvent.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SaveEvent(EventName.getText().toString(), EventTime.getText().toString(), date, month, year, image);
                        setUpCalendar();
                        alertDialog.dismiss();
                    }
                });

                builder.setView(addView);
                alertDialog = builder.create();
                alertDialog.show();

            }


        });

        GridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {// show and delete event
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String date = eventDateFormat.format(dates.get(position));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View showView = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_layout,null);
                RecyclerView recyclerView = showView.findViewById(R.id.events_RV);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(showView.getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                RecyclerAdapter recyclerAdapter = new RecyclerAdapter(showView.getContext(),collectEventByDate(date));
                recyclerView.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();

                builder.setView(showView);
                alertDialog = builder.create();
                alertDialog.show();
                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {// after we delete an event it refreshes
                    //the number of events present in the day
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        setUpCalendar();
                    }
                });
                return true;
            }
        });
    }

    private ArrayList<Event> collectEventByDate(String date){
        ArrayList<Event> list = new ArrayList<>();
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.readEvents(date,database);
        while(cursor.moveToNext()){
            String event = cursor.getString(cursor.getColumnIndex(DBStructure.EVENT));
            String time = cursor.getString(cursor.getColumnIndex(DBStructure.TIME));
            date = cursor.getString(cursor.getColumnIndex(DBStructure.DATE));
            String month = cursor.getString(cursor.getColumnIndex(DBStructure.MONTH));
            String year = cursor.getString(cursor.getColumnIndex(DBStructure.YEAR));
            String image = cursor.getString(cursor.getColumnIndex(DBStructure.IMAGE));
            Event e = new Event(event,time,date,month,year,image);
            list.add(e);
        }
        cursor.close();
        dbOpenHelper.close();
        return list;

    }
    public CalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    private void  SaveEvent(String event, String time, String date, String month, String year, String image){
        dbOpenHelper= new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.saveEvent(event,time,date,month,year,image,database);
        dbOpenHelper.close();
        Toast.makeText(context,"Event sauve-garde", Toast.LENGTH_SHORT).show();//Toast is used for small messages and
        // it blinks


    }

    private void initializeLayout(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendarlayout,this);
        NextButton = view.findViewById(R.id.nextBtn);
        PreviousButton= view.findViewById(R.id.previousBtn);
        CurrentDate = view.findViewById(R.id.currentDate);
        GridView = view.findViewById(R.id.gridview);

    }

    private void setUpCalendar(){
        String currentDate = dateFormat.format(calendar.getTime());
        CurrentDate.setText(currentDate);
        dates.clear();
        Calendar monthCalendar = (Calendar) calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH,0);// our calendar is for French people in this case sane people
        // and thus the week starts with Monday

        int FirstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK)-2;// The days are between 1 and 7
        monthCalendar.add(Calendar.DAY_OF_MONTH, -FirstDayOfMonth);
        collectEventsPerMonth(monthFormat.format(calendar.getTime()),yearFormat.format(calendar.getTime()));

        while (dates.size() < MAX_CALENDAR_DAYS){
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH,1);
        }

        myGridAdapter = new MyGridAdapter(context,dates,calendar,events);
        GridView.setAdapter(myGridAdapter);



    }

    private void collectEventsPerMonth(String month, String year){
        events.clear();
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.readEventsPerMonth(month,year,database);//the cursor takes all events,times,dates,etc. in that specific month
        while(cursor.moveToNext()){
            String event = cursor.getString(cursor.getColumnIndex(DBStructure.EVENT));
            String time = cursor.getString(cursor.getColumnIndex(DBStructure.TIME));
            String date = cursor.getString(cursor.getColumnIndex(DBStructure.DATE));
            month = cursor.getString(cursor.getColumnIndex(DBStructure.MONTH));
            year = cursor.getString(cursor.getColumnIndex(DBStructure.YEAR));
            String image = cursor.getString(cursor.getColumnIndex(DBStructure.IMAGE));
            Event e = new Event(event,time,date,month,year,image);
            events.add(e);

        }
        cursor.close();
        dbOpenHelper.close();
    }
}
