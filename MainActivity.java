package com.example.afaf.inclcapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.example.afaf.inclcapp.helper_database.clinic_helper;
import com.example.afaf.inclcapp.helper_database.myAccount_helper;
import com.example.afaf.inclcapp.helper_database.productSelector_helper;
import com.example.afaf.inclcapp.helper_database.product_helper;
import com.example.afaf.inclcapp.helper_database.sercat_helper;
import com.example.afaf.inclcapp.helper_database.service_selector_helper;
import com.example.afaf.inclcapp.helper_database.services_helper;

import org.json.JSONArray;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.StringTokenizer;

public class MainActivity extends ActionBarActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String MyPREFERENCES = "MyPrefs";
    // ----------------------- log in ------------------------------------------------------
    public static final String LoginPREFERENCES = "LoginPrefs";
    public static boolean fabflag = false;
    public static boolean request, navcalender = false;
    SharedPreferences sharedpreferences;
    String uRl = "";
    SharedPreferences sharedpreferencesLogin;
    public static String uname = "";
    public static String passw = "";
    String userId2 = "";
    String BP = "";
    // clinic selector
    clinic_helper dbClinic = new clinic_helper(this);
    String key = "";
    String name = "";
    String clinicId = "";
    // service category selector
    sercat_helper dbCat = new sercat_helper(this);
    String sercatkey = "";
    String sercatname = "";
    String sercatId = "";
    // product selector
    service_selector_helper dbsersel = new service_selector_helper(this);
    String servid = "";
    String servname = "";
    String productHisId="";


    // service selector
    productSelector_helper dbprod = new productSelector_helper(this);
    String prodkey = "";
    String prodname = "";
    String prodId = "";


    // my account data
    myAccount_helper dbMyAccount = new myAccount_helper(this);
    String balance = "";
    String totalDebit = "";
    String totalCredit = "";



    services_helper db_service;
    public static String serviceName = "";
    public static String addserviceId = "";
    public static String cost = "";
    public static String Price = "";
    public static String serviceCategory = "";
    public static String serviceID = "";
    public static String serviceCategoryId = "";
    public static String unum = "";


    public static int login_flag = 0;


    public static boolean if_update = false;
    product_helper db_product;
    String textName = "";
    String productname = "";
    String productID = "";
    String sessionno = "";
    String price = "";
    String nuitno = "";
    String warehouseRuleType = "";
    String service = "";
    String serviceProductId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setcontent();

        AsyncCallWS task = new AsyncCallWS();
        task.execute();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //-----------------------------url &  login  ------------------------------------------
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        uRl = sharedpreferences.getString("URL", null);
        if (uRl != null) {
            sharedpreferencesLogin = getSharedPreferences(LoginPREFERENCES, Context.MODE_PRIVATE);
            uname = sharedpreferencesLogin.getString("uName", null);
            passw = sharedpreferencesLogin.getString("passWord", null);
            userId2 = sharedpreferencesLogin.getString("userId", null);
            BP = sharedpreferencesLogin.getString("BP", null);

            if (uname == null && passw == null) {
                Intent i1 = new Intent(getApplicationContext(), insert_url.class);
                startActivity(i1);
                finish();

            }
        } else {
            Intent i = new Intent(getApplicationContext(), inserturl_noparent.class);
            startActivity(i);
            finish();

        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              //  fabflag = true;
                if (request == true && fabflag == true) {
                    Intent intent = new Intent(getApplicationContext(), appointment_request.class);
                    startActivity(intent);
                    finish();
                }


            }
        });


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // username in nav view
        View hView = navigationView.getHeaderView(0);
        TextView t = (TextView) hView.findViewById(R.id.userName);
        t.setText(uname);

    }


    private void setcontent() {

        Intent intent = getIntent();
        String director = intent.getStringExtra("Window");
        if (director != null) {
            if (director.equals("2")) {
                navcalender = false;
                fabflag = false;
                Fragment fragment = new appointments("", null);
                FragmentManager fragmentManager = getSupportFragmentManager();

                fragmentManager.beginTransaction().replace(R.id.lin, fragment).commit();

                setTitle("Appointments");
            } else if (director.equals("3")) {
                navcalender = false;
                fabflag = false;
                Fragment fragment = new requests("", null);
                FragmentManager fragmentManager = getSupportFragmentManager();

                fragmentManager.beginTransaction().replace(R.id.lin, fragment).commit();

                setTitle("Request Appointments");
            } else if (director.equals("4")) {
                navcalender = false;
                fabflag = true;

                Fragment fragment = new patients("", null);
                FragmentManager fragmentManager = getSupportFragmentManager();

                fragmentManager.beginTransaction().replace(R.id.lin, fragment).commit();

                setTitle("Patients");
            }
            if (director.equals("1")) {
                navcalender = true;
                fabflag = false;

                Fragment fragment = new CalenderFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();

                fragmentManager.beginTransaction().replace(R.id.lin, fragment).commit();

                setTitle("Calender");
            }

        } else {
            navcalender = true;
            fabflag = false;
            Fragment fragment = new CalenderFragment();

            FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.beginTransaction().replace(R.id.lin, fragment).commit();

            setTitle("Calender");

        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        int id = item.getItemId();

//        if (id == R.id.nav_notification) {
//
//        } else
        if (id == R.id.nav_calender) {
            fragment = new CalenderFragment();
            request = false;
            navcalender = true;
            fabflag = false;

        } else if (id == R.id.nav_appointment) {
            fragment = new appointments("", null);
            request = false;
            navcalender = false;
            fabflag = false;

        } else if (id == R.id.nav_request) {
            fragment = new requests("", null);
            request = true;
            navcalender = false;
            fabflag = true;

        } else if (id == R.id.nav_current) {
            fragment = new patients("", null);
            request = false;
            navcalender = false;
            fabflag = false;

        } else if (id == R.id.nav_account) {
            Intent i = new Intent(this, MyAccount.class);
            startActivity(i);
            request = false;
            navcalender = false;
            fabflag = false;

        }
        else if (id == R.id.nav_update) {
            AsyncCallProduct task = new AsyncCallProduct();
            task.execute();
            AsyncCallService task1 = new AsyncCallService();
            task1.execute();
            if_update = true;
            fabflag = false;

            Toast.makeText(this, "Products and Services ara updated successfully !!", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_logout) {
            request = false;
            navcalender = false;
            fabflag = false;
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Would you like to logout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            Intent i = new Intent(getApplicationContext(), insert_url.class);
                            startActivity(i);
                            finish();
                            Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            finish();
                            SharedPreferences.Editor editor = sharedpreferencesLogin.edit();
                            editor.putString("uName", null);
                            editor.putString("passWord", null);
                            editor.putString("userId", null);
                            editor.commit();

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent1);
                            finish();

                        }
                    })
                    .show();


        } else if (id == R.id.nav_inserturl) {
            request = false;
            navcalender = false;
            login_flag = 1;
            fabflag = false;
            Intent i = new Intent(this, insert_url.class);
            startActivity(i);

        }

        if (item.getItemId() != R.id.nav_inserturl && item.getItemId() != R.id.nav_logout && item.getItemId() != R.id.nav_update && item.getItemId()!= R.id.nav_account) {
            setTitle(item.getTitle());

            FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.beginTransaction().replace(R.id.lin, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    // -------------------------------------Start of spinners------------------------------------------
    public HttpURLConnection createConnectionact(String wsPart, String method) throws Exception {
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

    //--------------------------------clinic_selector --------------------------------
    public String clinic_selector(String wsPart) {
        try {

            final HttpURLConnection hc = createConnectionact(wsPart, "GET");
            hc.connect();
            dbClinic.onUpgrade(dbClinic.getWritableDatabase(), 1, 2);

            final InputStream is = hc.getInputStream();
            //  final StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = reader.readLine();

            JSONArray newarray;
            String s;
            StringTokenizer st = new StringTokenizer(line, "#*#");
            while (st.hasMoreTokens()) {
                s = st.nextToken();

                newarray = new JSONArray(s);

                if (newarray != null) {
                    clinicId = newarray.getString(0);
                    name = newarray.getString(1);
                    key = newarray.getString(2);

                }

                dbClinic.createClinic(key, name, clinicId);

            }


        } catch (final Exception e) {

        }

        return null;
    }

    //--------------------------------service category_selector --------------------------------
    public String serviceCategory_selector(String wsPart) {
        try {

            final HttpURLConnection hc = createConnectionact(wsPart, "GET");
            hc.connect();
            dbCat.onUpgrade(dbCat.getWritableDatabase(), 1, 2);

            final InputStream is = hc.getInputStream();
            //  final StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = reader.readLine();

            JSONArray newarray;
            String s;
            StringTokenizer st = new StringTokenizer(line, "#*#");
            while (st.hasMoreTokens()) {
                s = st.nextToken();

                newarray = new JSONArray(s);

                if (newarray != null) {
                    sercatId = newarray.getString(0);
                    sercatname = newarray.getString(1);
                    sercatkey = newarray.getString(2);

                }

                dbCat.createServiceCat(sercatkey, sercatname, sercatId);

            }


        } catch (final Exception e) {

        }

        return null;
    }

    //--------------------------------product   Selector --------------------------------
    public String productSelector(String wsPart) {
        try {

            final HttpURLConnection hc = createConnectionact(wsPart, "GET");
            hc.connect();
            dbprod.onUpgrade(dbprod.getWritableDatabase(), 1, 2);

            final InputStream is = hc.getInputStream();
            //  final StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = reader.readLine();

            JSONArray newarray;
            String s;
            StringTokenizer st = new StringTokenizer(line, "#*#");
            while (st.hasMoreTokens()) {
                s = st.nextToken();

                newarray = new JSONArray(s);

       //         System.out.println(newarray);

                if (newarray != null) {
                    prodId = newarray.getString(0);
                    prodname = newarray.getString(1);
                    prodkey = newarray.getString(2);

                }

                dbprod.createProductSelector(prodkey, prodname, prodId);

            }


        } catch (final Exception e) {

        }

        return null;
    }


    //--------------------------------get my account data  --------------------------------
    public String getMyaccountData(String wsPart) {
        try {

            final HttpURLConnection hc = createConnectionact(wsPart, "GET");
            hc.connect();
            dbMyAccount.onUpgrade(dbMyAccount.getWritableDatabase(), 1, 2);

            final InputStream is = hc.getInputStream();
            //  final StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = reader.readLine();

            JSONArray newarray;
            String s;
            StringTokenizer st = new StringTokenizer(line, "#*#");
            while (st.hasMoreTokens()) {
                s = st.nextToken();

                newarray = new JSONArray(s);

                if (newarray != null) {
                    balance = newarray.getString(0);
                    totalDebit = newarray.getString(1);
                    totalCredit = newarray.getString(2);

                }

                dbMyAccount.createMyAccount(balance, totalDebit, totalCredit);

            }


        } catch (final Exception e) {

        }

        return null;
    }


    private class AsyncCallWS extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            clinic_selector("/ws/com.opentus.inshape.clinic.clinic_selector?");
            serviceCategory_selector("/ws/com.opentus.inshape.clinic.sercat_selector?");
            productSelector("/ws/com.opentus.inshape.clinic.product_selector?");
            service_selector("/ws/com.opentus.inshape.clinic.service_selector?docid="+BP);
            getMyaccountData("/ws/com.opentus.inshape.clinic.getdoctordata?docid="+BP);

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

    //--------------------------------service selector --------------------------------
    public String service_selector(String wsPart) {
        try {

            final HttpURLConnection hc = createConnectionact(wsPart, "GET");
            hc.connect();
            dbsersel.onUpgrade(dbsersel.getWritableDatabase(), 1, 2);

            final InputStream is = hc.getInputStream();
            //  final StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = reader.readLine();

            JSONArray newarray;
            String s;
            StringTokenizer st = new StringTokenizer(line, "#*#");
            while (st.hasMoreTokens()) {
                s = st.nextToken();

                newarray = new JSONArray(s);

                if (newarray != null) {
                    servid = newarray.getString(0);
                    servname = newarray.getString(1);

                }

                dbsersel.createServiceSelector(servname , servid);

            }


        } catch (final Exception e) {

        }

        return null;
    }


    //--------------------------------update product-----------------------------------

    public HttpURLConnection createGetConnection(String wsPart, String method) throws Exception {
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(uname, passw.toCharArray());
            }
        });

        //"http://192.168.1.30:8080/openhats"
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

    public String doTestGetRequest(String wsPart,int flag) {
        try {


            final HttpURLConnection hc = createGetConnection(wsPart, "GET");
            hc.connect();
            //  final SAXReader sr = new SAXReader();
            final InputStream is = hc.getInputStream();
            final StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            if (flag == 1) {
                converter1(sb.toString());
            }else{
                converter_service(sb.toString());
            }
            // -Ahmed Ali Eldeeb----------------------------------------------------------------------------------------

        } catch (Exception e) {

        }

        return null;
    }

    public void converter1(String content) throws XmlPullParserException, IOException {
        db_product.onUpgrade(db_product.getWritableDatabase(), 1, 2);
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        // Log.d("aaa",content);

        xpp.setInput(new StringReader(content));
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (eventType == XmlPullParser.START_TAG) {
                textName = xpp.getName();

                if (textName.startsWith("product")) {

                    if (xpp.getAttributeCount() != 1) {
                        productname = xpp.getAttributeValue(2);
                        productID = xpp.getAttributeValue(0);

                    } else {
                        productname = "";
                        productID = "";

                    }


                }
                if (textName.startsWith("ndDimension")) {

                    if (xpp.getAttributeCount() != 1) {
                        service = xpp.getAttributeValue(2);
                        serviceProductId = xpp.getAttributeValue(0);

                    } else {
                        service = "";
                        serviceProductId = "";

                    }

                    db_product.createProduct(productname, sessionno, price, nuitno, productID, warehouseRuleType, service, serviceProductId , productHisId);

                    textName = "";
                }


            } else if (eventType == XmlPullParser.TEXT) {

                if (textName.equals("id") && !xpp.getText().startsWith("\n")) {

                    if (!xpp.getText().startsWith("\n")) {
                        productID = xpp.getText();
                    } else {
                        productID = "";
                    }
                }

                if (textName.equals("sessionNo")) {
                    //  xx[i] = xpp.getText();

                    if (!xpp.getText().startsWith("\n")) {
                        sessionno = xpp.getText();
                    } else {
                        sessionno = "";
                    }

                }

                if (textName.equals("netUnitPrice") && !xpp.getText().startsWith("\n")) {
                    //  xx[i] = xpp.getText();

                    if (!xpp.getText().startsWith("\n")) {
                        price = xpp.getText();
                    } else {
                        price = "";
                    }

                }

                if (textName.equals("unitno")) {
                    //  xx[i] = xpp.getText()

                    if (!xpp.getText().startsWith("\n")) {
                        nuitno = xpp.getText();
                    } else {
                        nuitno = "";
                    }

                }

                if (textName.equals("warehouseRuleType")) {
                    //  xx[i] = xpp.getText()

                    if (!xpp.getText().startsWith("\n")) {
                        warehouseRuleType = xpp.getText();
                    } else {
                        warehouseRuleType = "";
                    }



                }


            }
            eventType = xpp.next();
        }

    }

    private class AsyncCallProduct extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Intent i = getIntent();
            String s = i.getStringExtra("patientId");

            doTestGetRequest("/ws/com.opentus.inshape.clinic.getproduct?custid=" + s,1);

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

    //-------------------------------update service-------------------------------------

    public void converter_service(String content) throws XmlPullParserException, IOException {
        db_service.onUpgrade(db_service.getWritableDatabase(), 1, 2);
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        // Log.d("aaa",content);

        xpp.setInput(new StringReader(content));
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (eventType == XmlPullParser.START_TAG) {
                textName = xpp.getName();

                if (textName.equals("ndDimension")) {

                    if (xpp.getAttributeCount() != 1) {
                        serviceName = xpp.getAttributeValue(2);
                        serviceID= xpp.getAttributeValue(0);
                    } else {
                        serviceName = "";
                        serviceID = "";
                    }

                }

            } else if (eventType == XmlPullParser.TEXT) {

                if (textName.equals("id") && !xpp.getText().startsWith("\n")) {

                    if (!xpp.getText().startsWith("\n")) {
                        addserviceId = xpp.getText();
                    } else {
                        addserviceId = "";
                    }
                }


                if (textName.equals("unitNo") && !xpp.getText().startsWith("\n")) {
                    //  xx[i] = xpp.getText()

                    if (!xpp.getText().startsWith("\n")) {
                        unum = xpp.getText();
                    } else {
                        unum = "";
                    }

                    db_service.createService(addserviceId, serviceName, serviceID,cost , price, serviceCategory, serviceCategoryId, unum);

                    textName = "";

                }

            }
            eventType = xpp.next();
        }

    }

    public class AsyncCallService extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Intent i = getIntent();
            String s = i.getStringExtra("patientId");


            doTestGetRequest("/ws/com.opentus.inshape.clinic.getservices?custid="+ s ,0);

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