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
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.afaf.inclcapp.helper_database.myAccount_helper;
import com.example.afaf.inclcapp.helper_database.myAccount_model;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class balance_fragment extends Fragment {
    //------------------- url -------------------------------------------------------------
    public static final String MyPREFERENCES = "MyPrefs";
    // ----------------------- log in ------------------------------------------------------
    public static final String LoginPREFERENCES = "LoginPrefs";
    public static String patientid = null;
    SharedPreferences sharedpreferences;
    String uRl = "";
    SharedPreferences sharedpreferencesLogin;
    String uname = "";
    String passw = "";
    String userId = "";

    myAccount_helper db;

    TextView balance, credit, debit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View rootview = inflater.inflate(R.layout.balance_fragment, container, false);
        return rootview;
    }


    @SuppressLint("WrongViewCast")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = new myAccount_helper(getActivity());

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

        balance = (TextView) view.findViewById(R.id.balance);
        credit = (TextView) view.findViewById(R.id.credit);
        debit = (TextView) view.findViewById(R.id.debit);

        //-----------------------------------------------------------------------------------

        List<myAccount_model> emlist;
        myAccount_model em;
        emlist = db.getAllMyAccountData();


        if (emlist.isEmpty()) {

            balance.setText("No data to show");
            debit.setText("No data to show");
            credit.setText("No data to show");

        } else {
            em = emlist.get(0);

            balance.setText(em.getBalance());
            debit.setText(em.getTotalDebit());
            credit.setText(em.getTotalCredit());
        }

    }


}
