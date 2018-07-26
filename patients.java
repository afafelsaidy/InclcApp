package com.example.afaf.inclcapp;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;


import com.example.afaf.inclcapp.helper_database.patient_adapter;
import com.example.afaf.inclcapp.helper_database.patient_helper;
import com.example.afaf.inclcapp.helper_database.patient_model;

import org.json.JSONException;
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
import java.util.List;

/**
 * Created by Marim on 20-Apr-17.
 */


@SuppressLint("ValidFragment")
public class patients extends ListFragment {
    // login
    public static final String LoginPREFERENCES = "LoginPrefs";
    public static final String UserName = "username";
    public static final String PassWord = "password";
    public static final String MyPREFERENCES = "MyPrefs";
    public static String patientId = "";
    public static String serviceID = "";
    List<patient_model> list;
    patient_helper db;
    patient_model model = null;
    String textName = "";
    String patientname = "";
    String servicename = "";
    String sqe = "";
    String remainn = "";
    String custID = "";
    SharedPreferences sharedpreferencesLogin;
    String uname = "";
    String passw = "";
    String userId = "";
    String BP = "";
    SharedPreferences sharedpreferences;
    String uRl = "";

    SwipeRefreshLayout swipeContainer;


    public patients() {
    }

    @SuppressLint("ValidFragment")
    public patients(String s, Object o) {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.patient_fragment, container, false);

        return v;
    }

    // ---------------------------------------------------------------------------------------
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        uRl = sharedpreferences.getString("URL", null);

        sharedpreferencesLogin = getActivity().getSharedPreferences(LoginPREFERENCES, Context.MODE_PRIVATE);
        uname = sharedpreferencesLogin.getString("uName", null);
        passw = sharedpreferencesLogin.getString("passWord", null);
        userId = sharedpreferencesLogin.getString("userId", null);
        BP = sharedpreferencesLogin.getString("BP", null);
        if (isNetworkAvailable() == true) {
            AsyncCall task = new AsyncCall();
            task.execute();

            list = db.getAllPatients();
            patient_adapter adapter = new patient_adapter(getListView().getContext(), list, db);
            setListAdapter(adapter);


        } else {
            list = db.getAllPatients();
            patient_adapter adapter = new patient_adapter(getListView().getContext(), list, db);
            setListAdapter(adapter);

        }


        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (isNetworkAvailable() == true) {
                    AsyncCall task = new AsyncCall();
                    task.execute();

                    list = db.getAllPatients();
                    patient_adapter adapter = new patient_adapter(getListView().getContext(), list, db);
                    setListAdapter(adapter);
                } else {
                    // Configure the refreshing colors

                    list = db.getAllPatients();
                    patient_adapter adapter = new patient_adapter(getListView().getContext(), list, db);
                    setListAdapter(adapter);

                    Toast.makeText(getActivity(), "Could not update patients", Toast.LENGTH_LONG).show();

                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeContainer.setRefreshing(false);
                    }
                }, 2000);

            }
        });


        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        db = new patient_helper(getActivity());
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        MainActivity.fabflag = false;
        int idint = (int) (id + 1);
        patient_model model = null;
        try {
            model = db.readPatient(idint);
        } catch (JSONException e) {

        }


        //   String patientname = model.getPatientName();
        String service = model.getServiceName();
        String sqe = model.getSqe();

        Intent i = new Intent(getActivity(), Main2Activity.class);

        i.putExtra("id", model.getId() + "");
        i.putExtra("patientname", model.getPatientName());
        i.putExtra("service", service);
        i.putExtra("sqe", sqe);
        i.putExtra("patientId", model.getPatientId());
        i.putExtra("serviceId", model.getServiceID());
        i.putExtra("scustId", model.getCustID());
        i.putExtra("remain", model.getRemain());

        startActivity(i);
        getActivity().finish();
    }

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

    // -------------------------------------Start of spinners------------------------------------------

    public String doTestGetRequest(String wsPart) {
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
            converter1(sb.toString());
            // -Ahmed Ali Eldeeb----------------------------------------------------------------------------------------

        } catch (Exception e) {

        }

        return null;
    }

    public void converter1(String content) throws XmlPullParserException, IOException {
        db.onUpgrade(db.getWritableDatabase(), 1, 2);
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        // Log.d("aaa",content);

        xpp.setInput(new StringReader(content));
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (eventType == XmlPullParser.START_TAG) {
                textName = xpp.getName();

                if (textName.startsWith("businessPartner")) {

                    if (xpp.getAttributeCount() != 1) {
                        patientname = xpp.getAttributeValue(2);
                        custID = xpp.getAttributeValue(0);

                    } else {
                        patientname = "";
                        custID = "";

                    }

                }

                if (textName.startsWith("service")) {


                    if (xpp.getAttributeCount() != 1) {
                        servicename = xpp.getAttributeValue(2);
                        serviceID = xpp.getAttributeValue(0);
                    } else {
                        servicename = null;
                    }


                }


            } else if (eventType == XmlPullParser.TEXT) {


                if (textName.equals("id") && !xpp.getText().startsWith("\n")) {

                    if (!xpp.getText().startsWith("\n")) {
                        patientId = xpp.getText();
                    } else {
                        patientId = "";
                    }
                }
                if (textName.equals("sequence") && !xpp.getText().startsWith("\n")) {
                    //  xx[i] = xpp.getText();


                    if (!xpp.getText().startsWith("\n")) {
                        sqe = xpp.getText();
                    } else {
                        sqe = "";
                    }

                }
                if (textName.equals("remain")) {
                    //  xx[i] = xpp.getText();
                    if (!xpp.getText().startsWith("\n")) {
                        remainn = xpp.getText();
                    } else {
                        remainn = "";
                    }
                    db.createPatient(patientname, servicename, sqe, patientId, serviceID, custID, remainn);

                    textName = "";
                }


            }
            eventType = xpp.next();
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // get patient
    private class AsyncCall extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            doTestGetRequest("/ws/com.opentus.inshape.clinic.getpatient?docid=" + BP);

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

