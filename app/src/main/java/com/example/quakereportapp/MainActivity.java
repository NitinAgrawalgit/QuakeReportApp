package com.example.quakereportapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    ListView earthQuakeListView;
    private CustomAdapter customAdapter;
    TextView fromDate;
    TextView toDate;
    ImageView searchBtn;

    int day, month, year;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fromDate = (TextView) findViewById(R.id.fromDate);
        toDate = (TextView) findViewById(R.id.toDate);
        searchBtn = (ImageView) findViewById(R.id.searchBtn);

        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        setInitialDate();
        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        i1 += 1;
                        String selectedDate = i + "-" + i1 + "-" + i2;
                        DateUtils.setStartDate(selectedDate);

                        fromDate.setText(selectedDate);
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        i1 += 1;
                        String selectedDate = i + "-" + i1 + "-" + i2;
                        DateUtils.setEndDate(selectedDate);

                        toDate.setText(selectedDate);
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetQuakeDataTask task = new GetQuakeDataTask();
                task.execute(DateUtils.stringUrl);

                searchBtn.setImageResource(R.drawable.refresh_ic_press);
            }
        });

        earthQuakeListView = findViewById(R.id.list);

        ArrayList<Earthquake> earthquakes = new ArrayList<>();

        customAdapter = new CustomAdapter(this, R.layout.custom_list_item, earthquakes);
        earthQuakeListView.setAdapter(customAdapter);

        earthQuakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(earthquakes.get(i).getmUrl()));
                startActivity(intent);
            }
        });

        GetQuakeDataTask task = new GetQuakeDataTask();
        task.execute(DateUtils.stringUrl);
    }

    private void setInitialDate(){
        int day = this.day;
        int month = this.month + 1;
        int year = this.year;

        String endDate = year + "-" + month + "-" + day;
        toDate.setText(endDate);
        DateUtils.setEndDate(endDate);

        String startDate = "";
        if(day == 1){
            if(month == 1){
                startDate += (year-1) + "-" + "12" + "-" + "01";
            }else {
                startDate += year + "-" + (month-1) + "-" + "01";
            }
        }else {
            startDate += year + "-" + month + "-" + (day-1);
        }
        fromDate.setText(startDate);
        DateUtils.setStartDate(startDate);
    }
    public class GetQuakeDataTask extends AsyncTask<String, Void, ArrayList<Earthquake>> {
        @Override
        protected ArrayList<Earthquake> doInBackground(String... strings) {
            if(DateUtils.stringUrl == null){
                return null;
            }

            ArrayList<Earthquake> earthquakes = QueryUtils.fetchEarthQuakeData(DateUtils.stringUrl);
            return earthquakes;
        }

        @Override
        protected void onPostExecute(ArrayList<Earthquake> earthquakes) {
            customAdapter.clear();
            searchBtn.setImageResource(R.drawable.refresh_ic);

            if(earthquakes!=null && !earthquakes.isEmpty()){
                customAdapter.addAll(earthquakes);
            }
        }
    }
}