package com.example.afaf.inclcapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import com.example.afaf.inclcapp.helper_database.patient_helper;
import com.example.afaf.inclcapp.helper_database.patient_model;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by enterprise on 24/04/17.
 */

public class patient_info extends Fragment {

    //------------------- url -------------------------------------------------------------
    public static final String MyPREFERENCES = "MyPrefs";
    // ----------------------- log in ------------------------------------------------------
    public static final String LoginPREFERENCES = "LoginPrefs";
    public static EditText pName, serv, seq, remain;
    public static String serviceIid = null;
    public static String patientid = null;
    public static String patName = null;
    public static String bpid = "";
    SharedPreferences sharedpreferences;
    String uRl = "";
    SharedPreferences sharedpreferencesLogin;
    String uname = "";
    String passw = "";
    String userId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View rootview = inflater.inflate(R.layout.patient_info, container, false);


        return rootview;
    }


    @SuppressLint("WrongViewCast")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //-----------------------------url &  login  ------------------------------------------
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        uRl = sharedpreferences.getString("URL", null);

        if (uRl != null) {
            sharedpreferencesLogin = getActivity().getSharedPreferences(LoginPREFERENCES, Context.MODE_PRIVATE);
            uname = sharedpreferencesLogin.getString("uName", null);
            passw = sharedpreferencesLogin.getString("passWord", null);
            userId = sharedpreferencesLogin.getString("userId", null);
            if (uname == null && passw == null) {
                Intent i1 = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(i1);
                getActivity().finish();

            }
        } else {
            Intent i = new Intent(getActivity().getApplicationContext(), insert_url.class);
            startActivity(i);
            getActivity().finish();

        }

        // --------------------------------------------------------------------------
        pName = (EditText) view.findViewById(R.id.pname);
        serv = (EditText) view.findViewById(R.id.ser);
        seq = (EditText) view.findViewById(R.id.seq);
        remain = (EditText) view.findViewById(R.id.remain);

        Intent i = getActivity().getIntent();


        String pid = i.getStringExtra("patientId");
        patientid = pid;


        List<patient_model> emlist;
        patient_model em;
        patient_helper dbPatient = new patient_helper(getActivity());
        emlist = dbPatient.getPatientWithId(patientid);
        if (emlist.size() > 0) {
            em = emlist.get(0);
            String idser = i.getStringExtra("serviceId");
            serviceIid = idser;

            //patient name
            patName = em.getPatientName();
            try {
                final String s = new String(patName.getBytes(), "UTF-8");
                pName.setText(s);
            } catch (UnsupportedEncodingException e) {

            }
            //service name
            String phnum = em.getServiceName();
            try {
                final String ss = new String(phnum.getBytes(), "UTF-8");
                serv.setText(ss);
            } catch (UnsupportedEncodingException e) {

            }

            // sequence
            String sq = em.getSqe();
            seq.setText(sq);

            String rem =emlist.get(0).getRemain();
            remain.setText(rem);

            String x= emlist.get(0).getCustID();
            bpid= x;


        }
    }


}
