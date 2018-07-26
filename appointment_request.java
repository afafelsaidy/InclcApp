package com.example.afaf.inclcapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.afaf.inclcapp.helper_database.appointment_Model;
import com.example.afaf.inclcapp.helper_database.appointment_helper;
import com.example.afaf.inclcapp.helper_database.clinic_helper;
import com.example.afaf.inclcapp.helper_database.clinic_model;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * Created by enterprise on 11/04/17.
 */

public class appointment_request extends AppCompatActivity  {


    // login
    public static final String LoginPREFERENCES = "LoginPrefs";
    public static final String MyPREFERENCES = "MyPrefs";
    // static TextView starttime, endtime;
    public static Spinner clinic;
    public static TextView nofcust, noofservedcust, starttime, endtime, date;
    static boolean clickStartTimeFlag = false;
    static boolean clickEndTimeFlag = false;
    static boolean clickStartDateFlag = false;
    SharedPreferences sharedpreferencesLogin;
    String uname = "";
    String passw = "";
    String userId = "";
    String BP = "";
    SharedPreferences sharedpreferences;
    String uRl = "";

    // requried
    boolean check, cancel = false;
    View focusView = null;


    public static appointment_Model Em= new appointment_Model();
    String uuid="";


    String doctor="";
    String doctorid="";
    static String stime="";
    static String etime="";
    public static boolean requestfab=false;

    int x=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request);

        if (MainActivity.fabflag==true){
            setTitle("Add Appointment");
        }else {
            setTitle("Appointment");
        }
//-----------------------------url &  login  ------------------------------------------

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        uRl = sharedpreferences.getString("URL", null);

        if (uRl != null) {
            sharedpreferencesLogin = getSharedPreferences(LoginPREFERENCES, Context.MODE_PRIVATE);
            uname = sharedpreferencesLogin.getString("uName", null);
            passw = sharedpreferencesLogin.getString("passWord", null);
            userId=sharedpreferencesLogin.getString("userId", null);
            if (uname == null && passw == null) {
                Intent i1 = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i1);
                finish();
            }
        }
        else{
            Intent i = new Intent(getApplicationContext(), insert_url.class);
            startActivity(i);
            //finish();
        }


        date = (TextView) findViewById(R.id.textstartdate);
        date.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                date.setError(null);
                cancel=false;
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });



        clinic = (Spinner) findViewById(R.id.spiner);
        // change color of selected item
        clinic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) view).setTextColor(Color.BLACK); //Change selected text color
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        nofcust = (TextView) findViewById(R.id.editText2);
        noofservedcust = (TextView) findViewById(R.id.editText3);
        starttime = (TextView) findViewById(R.id.textstarttime);
        starttime.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                starttime.setError(null);
                cancel=false;
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });


        endtime = (TextView) findViewById(R.id.textendtime);
        endtime.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                endtime.setError(null);
                cancel=false;
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });





        starttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(view);
                clickStartTimeFlag = true;
                clickEndTimeFlag = false;
            }
        });
        endtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(view);
                clickEndTimeFlag = true;
                clickStartTimeFlag = false;
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
                clickStartDateFlag = true;
            }
        });

        //--------------------------------------------------------------
        Intent i = getIntent();

        String doc= i.getStringExtra("appId");
        doctorid=doc;



        //clinic
        String  clinicc= i.getStringExtra("appClinic");

        clinic_helper db = new clinic_helper(this);
        clinic_model spinnerLists = null;
        List<String> list = new ArrayList<String>();

        System.out.println(db.getAllClinics().size());
        for (int ii = 0; ii < db.getAllClinics().size(); ii++) {
            try {
                spinnerLists = db.readClinic(ii+1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            list.add(spinnerLists.getName());

            if (spinnerLists.getName().toString().equals(clinicc)) {
                x=ii+1;
            }
        }
        ArrayAdapter<String> spinnerAdaptera = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        spinnerAdaptera.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clinic.setAdapter(spinnerAdaptera);


        if (MainActivity.fabflag == false) {
            // set data


            doctor= i.getStringExtra("appDoctor");

            //date
            String dd = i.getStringExtra("appDate");
            SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
            Date d = null;
            try {
                d = output.parse(dd);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long millisecond = d.getTime();

            String currentDate = getDate(millisecond, "yyyy-MM-dd");

            date.setText(currentDate);

            // starttime
            String stime = i.getStringExtra("appStartTime");
            Date datetime = null;
            try {
                datetime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(stime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String newString = new SimpleDateFormat("H:mm").format(datetime);
            starttime.setText(newString);

            // endtime
            String etime = i.getStringExtra("appEndTime");
            Date datetime1 = null;
            try {
                datetime1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(etime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String newString1 = new SimpleDateFormat("H:mm").format(datetime1);
            endtime.setText(newString1);



            clinic.setSelection(x-1);

            System.out.println(clinic.getSelectedItem()+"");

            // no of cust
            String cust = i.getStringExtra("appNumOfCust");
            nofcust.setText(cust);

            // no of served cust
            String scust = i.getStringExtra("appNumOfServedCust");
            noofservedcust.setText(scust);

        }


    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment1 = new TimePickerFragment();
        newFragment1.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    // --------------------------------- Date ------------------------------------
    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


    @Override
    public void onBackPressed() {

        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("Window", "3");
        startActivity(i);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("Window", "3");
            startActivity(i);
        }
        else  if (id==R.id.done){

            String appdate= date.getText()+"";
            String appDoctor= doctor;
            String appClinic= clinic.getSelectedItem()+"";
            String appSTime= starttime.getText()+"";
            String appETime= endtime.getText()+"";
            String appNoofCust= nofcust.getText()+"";
            String appNoofservedCust= noofservedcust.getText()+"";

            uuid = UUID.randomUUID().toString().replaceAll("-", "");

            appointment_helper h = new appointment_helper(this);
            Em.setId(h.getAllAppointments().size()+1);
            Em.setAppointmentId(uuid);
            Em.setDate(appdate);
            Em.setDoctor(appDoctor);
            Em.setClinic(appClinic);
            Em.setStartTime(appSTime);
            Em.setEndTime(appETime);
            Em.setNumOfCustomer(appNoofCust);
            Em.setNumOfServedCustomer(appNoofservedCust);

            check();
            if (cancel==true) {
                // cancel=false;
                check=true;

            }else{
                h.insertAppointment(Em);


                AsyncCallWS task= new AsyncCallWS();
                task.execute();

            }


            if (check==false) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("Window", "3");
                startActivity(intent);
            }

        }
        else if(id==R.id.close){
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("Window", "3");
            startActivity(i);
        }
        return true;
    }

    public void check(){

            if (TextUtils.isEmpty(date.getText())) {
                date.setError(getString(R.string.error_field_required));
                focusView = date;
                cancel = true;
            }

            if (TextUtils.isEmpty(starttime.getText())) {
                starttime.setError(getString(R.string.error_field_required));
                focusView = starttime;
                cancel = true;
            }
            if (TextUtils.isEmpty(endtime.getText())) {
                endtime.setError(getString(R.string.error_field_required));
                focusView = endtime;
                cancel = true;
            }



        //  return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return true;
    }



    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {
       // final Calendar c = Calendar.getInstance();
      //  final Calendar c1 = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");



        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
         final Calendar c= Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);


            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            if (clickStartTimeFlag == true) {
                // ---------------------------

                       Calendar date = Calendar.getInstance();
                                    date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    date.set(Calendar.MINUTE, minute);
                                    date.set(Calendar.AM_PM, date.get(Calendar.AM_PM));
                                    SimpleDateFormat dateFormateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                  stime = dateFormateTime.format(date.getTime());
                // --------------------------------

                starttime.setText(hourOfDay + ":" + minute);


            } else if (clickEndTimeFlag == true) {
                endtime.setText(hourOfDay + ":" + minute);

                Calendar date = Calendar.getInstance();
              date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                             date.set(Calendar.MINUTE, minute);
              date.set(Calendar.AM_PM, date.get(Calendar.AM_PM));
              SimpleDateFormat dateFormateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            etime = dateFormateTime.format(date.getTime());
            }
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

           // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            if (clickStartDateFlag == true) {
                month = month + 1;
                date.setText(year + "-" + month + "-" + day);
            }
        }
    }


    // insert appointment

    public HttpURLConnection createConnection(String wsPart, String method) throws Exception {
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(uname,passw.toCharArray());
            }
        });

        final URL url = new URL(uRl + wsPart);
        final HttpURLConnection hc = (HttpURLConnection) url.openConnection();
        hc.setRequestMethod(method);
        hc.setAllowUserInteraction(false);
        hc.setDefaultUseCaches(false);
        hc.setDoOutput(true);
        hc.setDoInput(true);
        hc.setInstanceFollowRedirects(true);
        hc.setUseCaches(false);
        hc.setRequestProperty("Content-Type", "text/xml");
        return hc;
    }

    private class AsyncCallWS extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            insert("/ws/com.opentus.inshape.clinic.insertAppointment?");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


        }

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }

    public String insert(String content) {
        try {

            sharedpreferencesLogin = getSharedPreferences(LoginPREFERENCES, Context.MODE_PRIVATE);
            BP = sharedpreferencesLogin.getString("BP", null);

            HttpURLConnection conn = createConnection( content,"POST");
            conn.connect();
            ArrayList<Object> list=new ArrayList<Object>();

            String appdate= date.getText()+"";
            String appDoctor= BP;
            String appClinic= clinic.getSelectedItem()+"";
            String appSTime= starttime.getText()+"";
            String appETime= endtime.getText()+"";
            String appNoofCust= nofcust.getText()+"";
            String appNoofservedCust= noofservedcust.getText()+"";

            uuid = UUID.randomUUID().toString().replaceAll("-", "");


            list.add(0,uuid);
            list.add(1,appdate);
            list.add(2,appDoctor);
            //clinic
            clinic_helper db = new clinic_helper(this);
            clinic_model spinnerLists = null;
            for (int ii = 1; ii < db.getAllClinics().size(); ii++) {
                try {
                    spinnerLists = db.readClinic(ii);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (spinnerLists.getName().toString().equals(appClinic)) {
                    list.add(3,spinnerLists.getClinicId());
                    break;
                }
            }
       //     list.add(3,"");
            list.add(4,stime);
            list.add(5,etime);
            list.add(6,appNoofCust);
            list.add(7,appNoofservedCust);


            JSONArray jsArray = new JSONArray(list);


            OutputStream os = conn.getOutputStream();

            String s=jsArray.toString();


            os.write(s.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;


    }


}
