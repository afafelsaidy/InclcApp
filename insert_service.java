package com.example.afaf.inclcapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;


import com.example.afaf.inclcapp.helper_database.service_selecor_model;
import com.example.afaf.inclcapp.helper_database.service_selector_helper;
import com.example.afaf.inclcapp.helper_database.services_helper;
import com.example.afaf.inclcapp.helper_database.services_model;

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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by enterprise on 10/05/17.
 */

public class insert_service extends AppCompatActivity {

    // login
    public static final String LoginPREFERENCES = "LoginPrefs";
    public static final String MyPREFERENCES = "MyPrefs";
    public static String serviceID;
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
    Spinner servicename;
    // requried
    boolean check, cancel, fabflagservice = false;
    View focusView = null;

    int x, y = 0;

    EditText unitnum;

public static boolean serviceflag=false;
    String bbbb="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.insert_service);


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
        servicename.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                serviceflag= true;

                Intent i = new Intent(getApplicationContext(), serviceSearch.class);
                startActivity(i);
                return false;
            }


        });


            setTitle("Add Service");


        unitnum = (EditText) findViewById(R.id.nom);


        Intent i = getIntent();

        serviceID = i.getStringExtra("serviceId");

        //name
        String id = i.getStringExtra("serviceId");


        String servicen = i.getStringExtra("servname");
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

            if (serList.getServicename().toString().equals(servicen)) {
                y = a + 1;
            }
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listspinner);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        servicename.setAdapter(spinnerAdapter);

        if(servicen != null) {
            servicename.setSelection(y - 1);
        }




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
        } else if (id == R.id.done) {

            String sername = servicename.getSelectedItem() + "";


            uuid = UUID.randomUUID().toString().replaceAll("-", "");

            services_helper h = new services_helper(this);
            Em.setId(h.getAllServices().size() + 1);
            Em.setServiceId(uuid);
            Em.setServiceName(sername);
            Em.setUnitnum(unitnum.getText()+"");

            Em.setCost("");
            Em.setPrice("");
            Em.setServiceCategory("");
            Em.setServiceCategoryId("");
            //Em.setSessions("");




                h.insertServices(Em);


                AsyncCallWS task = new AsyncCallWS();
                task.execute();
                Main2Activity.fabflagmain2 = false;
                fabflagservice = false;




                Intent i = new Intent(this, Main2Activity.class);
                listservice = true;
                i.putExtra("patientId", patient_info.patientid);
                i.putExtra("serviceId", patient_info.serviceIid);
                i.putExtra("patientname", patient_info.patName);
                startActivity(i);
                finish();



        } else if (id == R.id.close) {
            Intent i = new Intent(this, Main2Activity.class);
            listservice = true;
            i.putExtra("patientId", patient_info.patientid);
            i.putExtra("serviceId", patient_info.serviceIid);
            i.putExtra("patientname", patient_info.patName);
            startActivity(i);
            finish();

        }
        return true;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_menu, menu);
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

    public String insert(String content) {
        try {

            HttpURLConnection conn = createConnection(content, "POST");
            conn.connect();
            ArrayList<Object> list = new ArrayList<Object>();

            String sername = servicename.getSelectedItem() + "";

            uuid = UUID.randomUUID().toString().replaceAll("-", "");



            list.add(0, uuid);

            // service name
            service_selector_helper dbser = new service_selector_helper(this);
            service_selecor_model serList = null;
            for (int ii = 0; ii < dbser.getAllServiceSel().size(); ii++) {
                try {
                    serList = dbser.readServiceSel(ii + 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (serList.getServicename().toString().equals(sername)) {
                    list.add(1, serList.getSerID());
                    break;
                }
            }
            bbbb= patient_info.bpid;

            list.add(2, bbbb);

            String un= unitnum.getText()+"";
            list.add(3, un);

            JSONArray jsArray = new JSONArray(list);


            OutputStream os = conn.getOutputStream();

            String s = jsArray.toString();


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

    private class AsyncCallWS extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            insert("/ws/com.opentus.inshape.clinic.insertservice?");
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


}
