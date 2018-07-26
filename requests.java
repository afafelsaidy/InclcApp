package com.example.afaf.inclcapp;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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


import com.example.afaf.inclcapp.helper_database.appointment_Model;
import com.example.afaf.inclcapp.helper_database.appointment_adapter;
import com.example.afaf.inclcapp.helper_database.appointment_helper;

import java.util.List;

/**
 * Created by enterprise on 11/04/17.
 */

@SuppressLint("ValidFragment")
public class requests extends ListFragment {
    // login
    public static final String LoginPREFERENCES = "LoginPrefs";
    public static final String UserName = "username";
    public static final String PassWord = "password";
    public static final String MyPREFERENCES = "MyPrefs";
    List<appointment_Model> list;
    appointment_helper db;
    appointment_Model model = null;
    SharedPreferences sharedpreferencesLogin;
    String uname = "";
    String passw = "";
    String userId = "";
    SharedPreferences sharedpreferences;
    String uRl = "";

    SwipeRefreshLayout swipeContainer;


    public requests() {
    }

    @SuppressLint("ValidFragment")
    public requests(String s, Object o) {
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
            list = db.getAllAppointmentsFuture("DRAFT");
            appointment_adapter adapter = new appointment_adapter(getListView().getContext(), list, db);
            setListAdapter(adapter);


        } else {
            list = db.getAllAppointmentsFuture("DRAFT");
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
                    list = db.getAllAppointmentsFuture("DRAFT");
                    appointment_adapter adapter = new appointment_adapter(getListView().getContext(), list, db);
                    setListAdapter(adapter);

                } else {
                    // Configure the refreshing colors

                    list = db.getAllAppointmentsFuture("DRAFT");
                    appointment_adapter adapter = new appointment_adapter(getListView().getContext(), list, db);
                    setListAdapter(adapter);


                    Toast.makeText(getActivity(), "Could not update requested appointments", Toast.LENGTH_LONG).show();

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

        MainActivity.fabflag = false;


        List<appointment_Model> allappoints = db.getAllAppointmentsFuture("DRAFT");
        for (int i = 0; i < allappoints.size(); i++) {

            model = allappoints.get(i);

        }


        String appDate = model.getDate();
        String appDoctor = model.getDoctor();
        String appClinic = model.getClinic();
        String appStartTime = model.getStartTime();
        String appEndTime = model.getEndTime();
        String appNumOfCust = model.getNumOfCustomer();
        String appNumOfServedCust = model.getNumOfServedCustomer();
        String appStatus = model.getAppointmentStatus();

        Intent i = new Intent(getActivity(), appointment_request.class);

        i.putExtra("id", model.getId() + "");
        i.putExtra("appDate", appDate);
        i.putExtra("appDoctor", appDoctor);
        i.putExtra("appClinic", appClinic);
        i.putExtra("appStartTime", appStartTime);
        i.putExtra("appEndTime", appEndTime);
        i.putExtra("appNumOfCust", appNumOfCust);
        i.putExtra("appNumOfServedCust", appNumOfServedCust);
        i.putExtra("appStatus", appStatus);
        i.putExtra("appId", model.getAppointmentId());

        startActivity(i);
        getActivity().finish();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
