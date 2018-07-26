package com.example.afaf.inclcapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;

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

/**
 * Created by enterprise on 11/04/17.
 */

public class appointment_activity extends AppCompatActivity implements View.OnClickListener {


    // login
    public static final String LoginPREFERENCES = "LoginPrefs";
    SharedPreferences sharedpreferencesLogin;
    String uname = "";
    String passw = "";
    String userId = "";

    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    String uRl = "";
    // static TextView starttime, endtime;
    public static TextView clinic, date;
    public static TextView nofcust, noofservedcust, starttime, endtime;


    private FloatingActionButton fab1,fab2;
    public static boolean flag = false;

   // public static
    String appID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_activiy);

        if (MainActivity.fabflag == false) {
            setTitle("Appointment");
        } else {
            setTitle("Add Appointment");
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
        clinic = (TextView) findViewById(R.id.editText1);
        nofcust = (TextView) findViewById(R.id.editText2);
        noofservedcust = (TextView) findViewById(R.id.editText3);
        starttime = (TextView) findViewById(R.id.textstarttime);
        endtime = (TextView) findViewById(R.id.textendtime);


        // --------------------------- fab button -----------------------------------------------------

        fab1 = (FloatingActionButton)findViewById(R.id.fab1);
        fab2 = (FloatingActionButton)findViewById(R.id.fab2);

        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);


        Intent i = getIntent();

        appID= i.getStringExtra("appId");



        if (MainActivity.fabflag == false) {
            // set data

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

            //clinic
            String clin = i.getStringExtra("appClinic");
            clinic.setText(clin);

            // no of cust
            String cust = i.getStringExtra("appNumOfCust");
            nofcust.setText(cust);

            // no of served cust
            String scust = i.getStringExtra("appNumOfServedCust");
            noofservedcust.setText(scust);

        }


    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }



@Override
public void onClick(View v) {

    int id = v.getId();
    switch (id){

        case R.id.fab1:

            new AlertDialog.Builder(this)
                    .setTitle("Cancel Appointment")
                    .setMessage("Would you like to Cancel?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            AsyncCallWS1 task= new AsyncCallWS1();
                            task.execute();

                            Toast.makeText(getApplicationContext(), "You ara successfully Canceled Appointment", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {


                        }
                    })
                    .show();

            break;
        case R.id.fab2:
            new AlertDialog.Builder(this)
                    .setTitle("Change appointment")
                    .setMessage("Would you like to Change appointment?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            AsyncCallWSChange task2= new AsyncCallWSChange();
                            task2.execute();
                            Toast.makeText(getApplicationContext(), "You ara successfully Changed Appointment", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();



            break;
    }
}


    @Override
    public void onBackPressed() {

        if (MainActivity.navcalender==true) {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("Window", "1");
            startActivity(i);
        }else{
            Intent i = new Intent(this, MainActivity.class);
            if (flag==true){
                i.putExtra("Window", "2");
            }else {
                i.putExtra("Window", "3");
            }
            startActivity(i);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==android.R.id.home) {
            if (MainActivity.navcalender==true) {
                Intent i = new Intent(this, MainActivity.class);
                i.putExtra("Window", "1");
                startActivity(i);
            }else{
                Intent i = new Intent(this, MainActivity.class);
                if (MainActivity.fabflag==true){
                    i.putExtra("Window", "2");
                }else {
                    i.putExtra("Window", "3");
                }
                startActivity(i);
            }
        }
        return true;
    }

    // cancel appointment
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

// cancel appointment
    private class AsyncCallWS1 extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            canel("/ws/com.opentus.inshape.clinic.cancelAppointment?");
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

    public String canel(String content) {
        try {

            HttpURLConnection conn = createConnection( content,"POST");
            conn.connect();
            ArrayList<Object> list=new ArrayList<Object>();

            list.add(0,appID);

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


    // change appointment
    private class AsyncCallWSChange extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            change("/ws/com.opentus.inshape.clinic.changeAppointment?");
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

    public String change(String content) {
        try {

            HttpURLConnection conn = createConnection( content,"POST");
            conn.connect();
            ArrayList<Object> list=new ArrayList<Object>();

            list.add(0,appID);


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
