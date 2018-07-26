package com.example.afaf.inclcapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;


import com.example.afaf.inclcapp.helper_database.productPrice_helper;
import com.example.afaf.inclcapp.helper_database.productSelector_helper;
import com.example.afaf.inclcapp.helper_database.productSelector_model;
import com.example.afaf.inclcapp.helper_database.product_helper;
import com.example.afaf.inclcapp.helper_database.product_model;
import com.example.afaf.inclcapp.helper_database.service_selecor_model;
import com.example.afaf.inclcapp.helper_database.service_selector_helper;

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
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * Created by enterprise on 10/05/17.
 */

public class insert_products extends AppCompatActivity {

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

    EditText  unitno;
    Spinner  type;
 //   AutoCompleteTextView productname;
    Spinner productname;

    public static String productID;

    public static boolean listproduct = false;

    public static product_model pm = new product_model();
    String uuid = "";
    // requried
    boolean check, cancel = false;
    View focusView = null;
    Spinner servicename;
    int x , xx, y= 0;


    // product price
    productPrice_helper dbPP = new productPrice_helper(this);
    String product = "";
    String unitprice = "";
    String PPId = "";

    String s="";

    String bbbb= "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_product);

        productname = (Spinner) findViewById(R.id.spinernamep);
        unitno = (EditText) findViewById(R.id.unitnopro);
        type= (Spinner) findViewById(R.id.spinertype);
        servicename = (Spinner) findViewById(R.id.spinerservice);


        if (Main2Activity.fabflagmain2 == false) {
            setTitle("Products");
            unitno.setEnabled(false);
        } else {
            setTitle("Add Product");
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

        // -------------------------------------------------------------------------------

        productname.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Intent i = new Intent(getApplicationContext(), productSearch.class);
                startActivity(i);
                return false;
            }


        });



        servicename.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                insert_service.serviceflag=false;

                Intent i = new Intent(getApplicationContext(), serviceSearch.class);
                startActivity(i);
                return false;
            }


        });




        Intent i = getIntent();

        productID = i.getStringExtra("productId");

        bbbb= patient_info.bpid;



       //  product
        String products = i.getStringExtra("Name");
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

            if (spinnerLists.getProdname().toString().equals(products)) {
                x = ii + 1;
            }
        }
        ArrayAdapter<String> spinnerAdaptera = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        spinnerAdaptera.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productname.setAdapter(spinnerAdaptera);
        if (products!=null) {
            productname.setSelection(x - 1);
        }


            //type
        //  SERVICE  BROKEN
        // type
        String typee = i.getStringExtra("warehouseRuleType");
        List<String> listtype = new ArrayList<String>();

        listtype.add("SERVICE");
        listtype.add("BROKEN");
        listtype.add("FREE");
        for (int ii = 0; ii < listtype.size(); ii++) {
            if (listtype.get(ii).toString().equals(typee)) {
                xx = ii ;
            }
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listtype);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(spinnerAdapter);


        // service
        String servicen = i.getStringExtra("servname");
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
            if (serList.getServicename().toString().equals(servicen)) {
                y= a + 1;
            }
        }
        ArrayAdapter<String> spinnerAdapterser = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listspinner);
        spinnerAdapterser.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        servicename.setAdapter(spinnerAdapterser);
        if(servicen != null) {
            servicename.setSelection(y - 1);
        }


    }



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
            i.putExtra("scustId", patient_info.bpid);
            startActivity(i);
            finish();
        } else if (id == R.id.done) {

            String product_name = productname.getSelectedItem()+ "";

            String unitnoo = unitno.getText() + "";


            uuid = UUID.randomUUID().toString().replaceAll("-", "");

            product_helper h = new product_helper(this);
            pm.setId(h.getAllProducts().size() + 1);
            pm.setProductID(uuid);
            pm.setProductName(product_name);
            pm.setUnitno(unitnoo);
            pm.setWarehouseRuleType(type.getSelectedItem()+"");
            pm.setService(servicename.getSelectedItem()+"");


            check();
            if (cancel == true) {
                // cancel=false;
                check = true;

            } else {
                h.insertProduct(pm);


                AsyncCallWS task = new AsyncCallWS();
                task.execute();

            }


            if (check == false) {
                listproduct = true;
                Intent i = new Intent(this, Main2Activity.class);
                i.putExtra("patientId",patient_info.patientid);
                i.putExtra("serviceId",patient_info.serviceIid);
                i.putExtra("patientname",patient_info.patName);
                startActivity(i);
                finish();
            }

        } else if (id == R.id.close) {
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

    public void check() {

//
        if (TextUtils.isEmpty(unitno.getText())) {
            unitno.setError(getString(R.string.error_field_required));
            focusView = unitno;
            cancel = true;
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return true;
    }



    // insert product
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

    private class AsyncCallWS extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            insert("/ws/com.opentus.inshape.clinic.insertproduct?");
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

            HttpURLConnection conn = createConnection(content, "POST");
            conn.connect();
            ArrayList<Object> list = new ArrayList<Object>();

            String prodname = productname.getSelectedItem() + "";
            String unumber = unitno.getText() + "";
            uuid = UUID.randomUUID().toString().replaceAll("-", "");


            list.add(0, uuid);


            productSelector_helper db = new productSelector_helper(this);
            productSelector_model spinnerLists = null;

            //  System.out.println(db.getAllServiceCat().size());
            for (int ii = 0; ii < db.getAllProductSelector().size(); ii++) {
                try {
                    spinnerLists = db.readproductSelector(ii + 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (spinnerLists.getProdname().toString().equals(prodname)) {
                    list.add(1, spinnerLists.getProdId());
                    break;
                }
            }


            list.add(2, unumber);


            list.add(3, bbbb);
            list.add(4, type.getSelectedItem()+"");
            // service name
            service_selector_helper dbser = new service_selector_helper(this);
            service_selecor_model serList = null;
            for (int ii = 0; ii < dbser.getAllServiceSel().size(); ii++) {
                try {
                    serList = dbser.readServiceSel(ii + 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (serList.getServicename().toString().equals(servicename.getSelectedItem()+"")) {
                    list.add(5, serList.getSerID());
                    break;
                }
            }




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




}
