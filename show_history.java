package com.example.afaf.inclcapp;

import android.annotation.SuppressLint;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;


import com.example.afaf.inclcapp.helper_database.custhist_adapter;
import com.example.afaf.inclcapp.helper_database.custhist_helper;
import com.example.afaf.inclcapp.helper_database.custhist_model;

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
 * Created by enterprise on 24/04/17.
 */

public class show_history extends ListFragment {

    // login
    public static final String LoginPREFERENCES = "LoginPrefs";
    public static final String UserName = "username";
    public static final String PassWord = "password";
    public static final String MyPREFERENCES = "MyPrefs";
    public static String custhistId = "";
    public static String servicename = "";
    public static String serviceId = "";
    public static String sessionNo = "";
    public static String date = "";
    public static String customerId = "";
    public static String photo = "";
    public static String notes = "";

    List<custhist_model> list;
    custhist_helper db;
    String textName = "";
    SharedPreferences sharedpreferencesLogin;
    String uname = "";
    String passw = "";
    String userId = "";
    String BP = "";
    SharedPreferences sharedpreferences;
    String uRl = "";

    SwipeRefreshLayout swipeContainer;

    String s = "";

    public show_history() {
    }

    @SuppressLint("ValidFragment")
    public show_history(String s, Object o) {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.show_history, container, false);

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

            list = db.getAllcusthist();
            custhist_adapter adapter = new custhist_adapter(getListView().getContext(), list, db);
            setListAdapter(adapter);
        } else {
            list = db.getAllcusthist();
            custhist_adapter adapter = new custhist_adapter(getListView().getContext(), list, db);
            setListAdapter(adapter);

        }
        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeContainerHist);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (isNetworkAvailable() == true) {
                    AsyncCall task = new AsyncCall();
                    task.execute();

                    list = db.getAllcusthist();
                    custhist_adapter adapter = new custhist_adapter(getListView().getContext(), list, db);
                    setListAdapter(adapter);
                } else {
                    // Configure the refreshing colors

                    list = db.getAllcusthist();
                    custhist_adapter adapter = new custhist_adapter(getListView().getContext(), list, db);
                    setListAdapter(adapter);

                    Toast.makeText(getActivity(), "Could not update history", Toast.LENGTH_LONG).show();

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
        db = new custhist_helper(getActivity());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

//        MainActivity.fabflag = false;
//        int idint= (int) (id+1);
//        custhist_model model = null;
//        try {
//            model = db.readCustHist(idint);
//        } catch (JSONException e) {
//
//        }
//
//        Intent i = new Intent(getActivity(), showHist_activity.class);
//
//        i.putExtra("id", model.getId() + "");
//        i.putExtra("custhistId", model.getCusthistId());
//        i.putExtra("servicename", model.getServicename());
//        i.putExtra("serviceId", model.getServiceId());
//        i.putExtra("sessionNo", model.getSessionNo());
//        i.putExtra("date", model.getDate());
//        i.putExtra("customerId", model.getCustomerId());
//
//        startActivity(i);
//        getActivity().finish();
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

                if (textName.startsWith("service")) {

                    if (xpp.getAttributeCount() != 1) {
                        servicename = xpp.getAttributeValue(2);
                        serviceId = xpp.getAttributeValue(0);
                    } else {
                        servicename = "";
                        serviceId = "";
                    }


                }
                if (textName.startsWith("customer")) {

                    if (xpp.getAttributeCount() != 1) {
                        //custhistId = xpp.getAttributeValue(2);
                        customerId = xpp.getAttributeValue(0);
                    } else {
                        //   servicename = "";
                        customerId = "";
                    }

                    db.createCustHist(custhistId, servicename, serviceId, sessionNo, date, customerId, photo, notes);

                    textName = "";

                }
            } else if (eventType == XmlPullParser.TEXT) {

                if (textName.equals("id") && !xpp.getText().startsWith("\n")) {

                    if (!xpp.getText().startsWith("\n")) {
                        custhistId = xpp.getText();
                    } else {
                        custhistId = "";
                    }
                }
                if (textName.equals("sessionNo") && !xpp.getText().startsWith("\n")) {
                    //  xx[i] = xpp.getText();
                    if (!xpp.getText().startsWith("\n")) {
                        sessionNo = xpp.getText();
                    } else {
                        sessionNo = "";
                    }
                }
                if (textName.equals("date") && !xpp.getText().startsWith("\n")) {
                    //  xx[i] = xpp.getText();
                    if (!xpp.getText().startsWith("\n")) {
                        date = xpp.getText();
                    } else {
                        date = "";
                    }
                }

                if (textName.equals("photo") && !xpp.getText().startsWith("\n")) {
                    //  xx[i] = xpp.getText();
                    if (!xpp.getText().startsWith("\n")) {
                        photo = xpp.getText();
                    } else {
                        photo = "";
                    }
                }

                if (textName.equals("notes") && !xpp.getText().startsWith("\n")) {
                    //  xx[i] = xpp.getText();
                    if (!xpp.getText().startsWith("\n")) {
                        notes = xpp.getText();
                    } else {
                        notes = "";
                    }
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

    // get product
    private class AsyncCall extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Intent intent = getActivity().getIntent();
            String s = intent.getStringExtra("scustId");


            doTestGetRequest("/ws/com.opentus.inshape.clinic.session_history?custid=" + s);

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

