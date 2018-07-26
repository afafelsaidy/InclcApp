package com.example.afaf.inclcapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.afaf.inclcapp.helper_database.productSelector_helper;
import com.example.afaf.inclcapp.helper_database.productSelector_model;
import com.example.afaf.inclcapp.helper_database.product_model;
import com.example.afaf.inclcapp.helper_database.service_selecor_model;
import com.example.afaf.inclcapp.helper_database.service_selector_helper;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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
 * Created by Marim on 20-Apr-17.
 */


public class products_activity extends AppCompatActivity {

    // login
    public static final String LoginPREFERENCES = "LoginPrefs";
    SharedPreferences sharedpreferencesLogin;
    String uname = "";
    String passw = "";
    String userId = "";
    String BP = "";

    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    String uRl = "";

    EditText price, unitno;
    Spinner productname, type;
    public static String productID;
    public static String PID;

    public static boolean listproduct = false;

    public static product_model pm = new product_model();
    String uuid = "";
    // requried
    boolean check, cancel = false;
    View focusView = null;

    int y, x, xx = 0;
    Spinner servicename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_activity);

        productname = (Spinner) findViewById(R.id.spinername);
        //  sessionno = (EditText) findViewById(R.id.sessionnoprod);
        price = (EditText) findViewById(R.id.priceprod);
        unitno = (EditText) findViewById(R.id.unitnopro);
        type = (Spinner) findViewById(R.id.spinertype);
        servicename = (Spinner) findViewById(R.id.spinerservice);


        setTitle("Products");
        //   sessionno.setEnabled(false);
        price.setEnabled(false);
        unitno.setEnabled(false);

//-----------------------------url &  login  ------------------------------------------

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        uRl = sharedpreferences.getString("URL", null);

        if (uRl != null) {
            sharedpreferencesLogin = getSharedPreferences(LoginPREFERENCES, Context.MODE_PRIVATE);
            uname = sharedpreferencesLogin.getString("uName", null);
            passw = sharedpreferencesLogin.getString("passWord", null);
            userId = sharedpreferencesLogin.getString("userId", null);
            if (uname == null && passw == null) {
                Intent i1 = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i1);
                finish();
            }
        } else {
            Intent i = new Intent(getApplicationContext(), insert_url.class);
            startActivity(i);
            //finish();
        }

        // -------------------------------------------------------------------------------


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.DelProductfab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isNetworkAvailable() == true) {
                    AsyncCall task = new AsyncCall();
                    task.execute();

                    Toast.makeText(getApplicationContext(), "This Product was successfully deleted!", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.putExtra("Window", "4");
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();

                }
            }
        });


        Intent i = getIntent();

        productID = i.getStringExtra("productId");
        PID = i.getStringExtra("productHisId");

        // product
        String products = i.getStringExtra("productname");


        productSelector_helper db = new productSelector_helper(this);
        productSelector_model spinnerLists = null;
        List<String> list = new ArrayList<String>();

        //  System.out.println(db.getAllServiceCat().size());
        for (int ii = 0; ii < db.getAllProductSelector().size(); ii++) {
            try {
                spinnerLists = db.readproductSelector(ii + 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            list.add(spinnerLists.getProdname());

            if (spinnerLists.getProdId().toString().equals(productID)) {
                x = ii + 1;
            }
        }
        ArrayAdapter<String> spinnerAdaptera = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        spinnerAdaptera.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productname.setAdapter(spinnerAdaptera);


        //  SERVICE  BROKEN
        // type
        String typee = i.getStringExtra("warehouseRuleType");
        List<String> listtype = new ArrayList<String>();

        listtype.add("SERVICE");
        listtype.add("BROKEN");
        for (int ii = 0; ii < listtype.size(); ii++) {
            if (listtype.get(ii).toString().equals(typee)) {
                xx = ii;
            }
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listtype);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(spinnerAdapter);

        // service
        String nameee = i.getStringExtra("service");
        service_selector_helper dbser = new service_selector_helper(this);
        service_selecor_model serList = null;
        List<String> listspinner = new ArrayList<String>();

        for (int a = 0; a < dbser.getAllServiceSel().size(); a++) {
            try {
                serList = dbser.readServiceSel(a + 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listspinner.add(serList.getServicename());
            if (serList.getServicename().toString().equals(nameee)) {
                y = a + 1;
            }
        }
        ArrayAdapter<String> spinnerAdapterser = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listspinner);
        spinnerAdapterser.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        servicename.setAdapter(spinnerAdapterser);


        //  if (Main2Activity.fabflagmain2 == false) {
        // set data

        //product name
        productname.setSelection(x - 1);

        // no. of session
        //  String sess = i.getStringExtra("session");
        //  sessionno.setText(sess+"");

        // price
        String p = i.getStringExtra("price");
        System.out.print(p);
        price.setText(p + "");

        //unit no.
        String unit = i.getStringExtra("unitno");
        unitno.setText(unit);

        type.setSelection(xx);

        servicename.setSelection(y-1);

    }

//
//    }


    @Override
    public void onBackPressed() {

        Intent i = new Intent(this, Main2Activity.class);
        listproduct = true;
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
            listproduct = true;
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
        //  getMenuInflater().inflate(R.menu.nav_menu, menu);
        return true;
    }

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

    public String deleteProduct (String wsPart) {
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




    private class AsyncCall extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            deleteProduct("/ws/com.opentus.inshape.clinic.deleteproduct?pid=" + PID);

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