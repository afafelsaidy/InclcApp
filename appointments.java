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
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;


import com.example.afaf.inclcapp.helper_database.appointment_Model;
import com.example.afaf.inclcapp.helper_database.appointment_adapter;
import com.example.afaf.inclcapp.helper_database.appointment_helper;

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
 * Created by enterprise on 11/04/17.
 */

@SuppressLint("ValidFragment")
public class appointments extends ListFragment {
    // login
    public static final String LoginPREFERENCES = "LoginPrefs";
    public static final String UserName = "username";
    public static final String PassWord = "password";
    public static final String MyPREFERENCES = "MyPrefs";
    public static String appointmentId = "";
    List<appointment_Model> list;
    appointment_helper db;
    String appointName = "";
    String date = "";
    String doctor = "";
    String clinic = "";
    String startTime = "";
    String endTime = "";
    String numOfCustomer = "";
    String numOfServedCustomer = "";
    String appointmentStatus = "";
    SharedPreferences sharedpreferencesLogin;
    String uname = "";
    String passw = "";
    String userId = "";
    SharedPreferences sharedpreferences;
    String uRl = "";

    SwipeRefreshLayout swipeContainer;


    @SuppressLint("ValidFragment")
    public appointments(String s, Object o) {
    }

    public appointments() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.appointment_fragment, container, false);

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

        if (isNetworkAvailable() == true) {
            AsyncCall task = new AsyncCall();
            task.execute();


            list = db.getAllAppointmentsFuture("FUTURE");
            appointment_adapter adapter = new appointment_adapter(getListView().getContext(), list, db);
            setListAdapter(adapter);


        } else {
            list = db.getAllAppointmentsFuture("FUTURE");
            appointment_adapter adapter = new appointment_adapter(getListView().getContext(), list, db);
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


                    list = db.getAllAppointmentsFuture("FUTURE");
                    appointment_adapter adapter = new appointment_adapter(getListView().getContext(), list, db);
                    setListAdapter(adapter);

                } else {
                    // Configure the refreshing colors

                    list = db.getAllAppointmentsFuture("FUTURE");
                    appointment_adapter adapter = new appointment_adapter(getListView().getContext(), list, db);
                    setListAdapter(adapter);


                    Toast.makeText(getActivity(), "Could not update appointments", Toast.LENGTH_LONG).show();

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
        db = new appointment_helper(getActivity());
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

//        MainActivity.fabflag = false;
//
//        int idint = (int) (position + 1);
//        appointment_Model model = null;
//        try {
//            model = db.readAppointment(idint);
//        } catch (JSONException e) {
//
//        }
//        String appDate = model.getDate();
//        String appDoctor = model.getDoctor();
//        String appClinic = model.getClinic();
//        String appStartTime = model.getStartTime();
//        String appEndTime = model.getEndTime();
//        String appNumOfCust = model.getNumOfCustomer();
//        String appNumOfServedCust = model.getNumOfServedCustomer();
//        String appStatus = model.getAppointmentStatus();
//
//        Intent i = new Intent(getActivity(), appointment_activity.class);
//
//        i.putExtra("id", model.getId() + "");
//        i.putExtra("appDate", appDate);
//        i.putExtra("appDoctor", appDoctor);
//        i.putExtra("appClinic", appClinic);
//        i.putExtra("appStartTime", appStartTime);
//        i.putExtra("appEndTime", appEndTime);
//        i.putExtra("appNumOfCust", appNumOfCust);
//        i.putExtra("appNumOfServedCust", appNumOfServedCust);
//        i.putExtra("appStatus", appStatus);
//        i.putExtra("appId", model.getAppointmentId());
//
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
            // -----------------------------------------------------------------------------------------

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
                appointName = xpp.getName();

                if (appointName.startsWith("businessPartner")) {

                    if (xpp.getAttributeCount() != 1) {
                        doctor = xpp.getAttributeValue(2);
                        // d = xpp.getAttributeValue(0);

                    } else {
                        doctor = "";
                        //actRelatedLead="";

                    }

                }

                if (appointName.startsWith("clinic")) {

                    if (xpp.getAttributeCount() != 1) {
                        clinic = xpp.getAttributeValue(2);
                    } else {
                        clinic = null;
                    }
                }


            } else if (eventType == XmlPullParser.TEXT) {
                if (appointName.equals("id") && !xpp.getText().startsWith("\n")) {

                    if (!xpp.getText().startsWith("\n")) {
                        appointmentId = xpp.getText();
                    } else {
                        appointmentId = "";
                    }
                }
                if (appointName.equals("noofcusomers") && !xpp.getText().startsWith("\n")) {
                    //  xx[i] = xpp.getText();


                    if (!xpp.getText().startsWith("\n")) {
                        numOfCustomer = xpp.getText();
                    } else {
                        numOfCustomer = "";
                    }

                }
                if (appointName.equals("date") && !xpp.getText().startsWith("\n")) {
                    //   xx[i] = xpp.getText();
                    if (!xpp.getText().startsWith("\n")) {
                        date = xpp.getText();
                    } else {
                        date = "";
                    }


                }
                if (appointName.equals("noofservedcustomers") && !xpp.getText().startsWith("\n")) {
                    //   xx[i] = xpp.getText();

                    if (!xpp.getText().startsWith("\n")) {
                        numOfServedCustomer = xpp.getText();
                    } else {
                        numOfServedCustomer = "";
                    }

                }
                if (appointName.equals("appointmentstatus") && !xpp.getText().startsWith("\n")) {

                    if (!xpp.getText().startsWith("\n")) {
                        appointmentStatus = xpp.getText();
                    } else {
                        appointmentStatus = "";
                    }


                }

                if (appointName.equals("startingtime") && !xpp.getText().startsWith("\n")) {
                    //   xx[i] = xpp.getText();
                    if (!xpp.getText().startsWith("\n")) {
                        startTime = xpp.getText();
                    } else {
                        startTime = "";
                    }


                }

                if (appointName.equals("endingtime") && !xpp.getText().startsWith("\n")) {
                    //   xx[i] = xpp.getText();
                    if (!xpp.getText().startsWith("\n")) {
                        endTime = xpp.getText();
                    } else {
                        endTime = "";
                    }
                    db.createAppointment(date, doctor, clinic
                            , startTime, endTime, numOfCustomer, numOfServedCustomer,
                            appointmentStatus, appointmentId);
                    appointName = "";

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

    // get appointment
    private class AsyncCall extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            doTestGetRequest("/ws/com.opentus.inshape.clinic.getappointment?userid=" + userId);

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
