package com.example.afaf.inclcapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.afaf.inclcapp.helper_database.service_selecor_model;
import com.example.afaf.inclcapp.helper_database.service_selector_helper;
import com.example.afaf.inclcapp.helper_database.services_model;
import com.github.clans.fab.FloatingActionButton;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by enterprise on 25/04/17.
 */

public class services_activity extends AppCompatActivity {

    // login
    public static final String LoginPREFERENCES = "LoginPrefs";
    public static final String MyPREFERENCES = "MyPrefs";
    public static String addserviceID;
    public static boolean listservice = false;
    public static services_model Em = new services_model();
    public static String uuid = "";
    SharedPreferences sharedpreferencesLogin;
    String uname = "";
    String passw = "";
    String userId = "";
    String BP = "";
    SharedPreferences sharedpreferences;
    String uRl = "";
    EditText sessionno, cost, price;
    Spinner cat, servicename;
    // requried
    boolean check, cancel, fabflagservice = false;
    View focusView = null;

    int x, y = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.serivce_activity);


//-----------------------------url &  login  ------------------------------------------

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        uRl = sharedpreferences.getString("URL", null);

        sharedpreferencesLogin = getSharedPreferences(LoginPREFERENCES, Context.MODE_PRIVATE);
        BP = sharedpreferencesLogin.getString("BP", null);

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
        // -------------------------------------------------------------------------------

        servicename = (Spinner) findViewById(R.id.spinerservice);
        sessionno = (EditText) findViewById(R.id.sessionno);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.DelServicefab);

        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable() == true) {
                    AsyncCallWS task = new AsyncCallWS();
                    task.execute();

                    Toast.makeText(getApplicationContext(), "This Service was successfully deleted!", Toast.LENGTH_SHORT).show();

//                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
//                    i.putExtra("Window", "4");
//                    startActivity(i);
//                    finish();
                    Intent i = new Intent(getApplicationContext(), Main2Activity.class);
                    i.putExtra("patientId", patient_info.patientid);
                    i.putExtra("serviceId", patient_info.serviceIid);
                    i.putExtra("patientname", patient_info.patName);
                    i.putExtra("scustId", patient_info.bpid);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();

                }

            }
        });


        if (Main2Activity.fabflagmain2 == false) {
            setTitle("Service");

        } else {
            setTitle("Add Service");

        }


        Intent i = getIntent();

        addserviceID = i.getStringExtra("addserviceId");





        //name
        String name = i.getStringExtra("servicename");

        service_selector_helper dbser = new service_selector_helper(this);
        service_selecor_model serList = null;
        List<String> listspinner = new ArrayList<String>();

        //  System.out.println(db.getAllServiceCat().size());
        for (int a = 0; a < dbser.getAllServiceSel().size(); a++) {
            try {
                serList = dbser.readServiceSel(a + 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listspinner.add(serList.getServicename());


            if (serList.getServicename().toString().equals(name)) {
                y = a + 1;
            }
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listspinner);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        servicename.setAdapter(spinnerAdapter);


      //  if (Main2Activity.fabflagmain2 == false) {
            // set data

            //name

            servicename.setSelection(y - 1);

            // no of session
            String units = i.getStringExtra("units");
            sessionno.setText(units);

//            // cost
//            String c = i.getStringExtra("cost");
//            cost.setText(c);
//
//            // price
//            String p = i.getStringExtra("price");
//            price.setText(p);
//
//            cat.setSelection(x - 1);

     //   }


    }


    @Override
    public void onBackPressed() {

        Intent i = new Intent(this, Main2Activity.class);
        listservice = true;
        i.putExtra("patientId", patient_info.patientid);
        i.putExtra("serviceId", patient_info.serviceIid);
        i.putExtra("patientname", patient_info.patName);
        startActivity(i);
        finish();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent i = new Intent(this, Main2Activity.class);
            listservice = true;
            i.putExtra("patientId", patient_info.patientid);
            i.putExtra("serviceId", patient_info.serviceIid);
            i.putExtra("patientname", patient_info.patName);
            startActivity(i);
            finish();
        }
//        else if (id == R.id.close) {
//            Intent i = new Intent(this, Main2Activity.class);
//            listservice = true;
//            i.putExtra("patientId", patient_info.patientid);
//            i.putExtra("serviceId", patient_info.serviceIid);
//            i.putExtra("patientname", patient_info.patName);
//            startActivity(i);
//            finish();
//
//        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
     //   getMenuInflater().inflate(R.menu.nav_menu, menu);
        return true;
    }


    // insert service
    public HttpURLConnection createConnection(String wsPart, String method) throws Exception {
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(uname, passw.toCharArray());
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


    public String deleteService(String wsPart) {
        try {

            HttpURLConnection conn = createConnection(wsPart, "POST");
            conn.connect();


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

    private class AsyncCallWS extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            deleteService("/ws/com.opentus.inshape.clinic.deleteservice?sid=" + addserviceID);
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
