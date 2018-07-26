package com.example.afaf.inclcapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by enterprise on 22/05/17.
 */

public class serviceSearch extends AppCompatActivity{

    public static final String MyPREFERENCES = "MyPrefs";
    // ----------------------- log in ------------------------------------------------------
    public static final String LoginPREFERENCES = "LoginPrefs";
    public static boolean fabflag = false;
    public static boolean  servicesearchflag = false;
    SharedPreferences sharedpreferences;
    String uRl = "";
    SharedPreferences sharedpreferencesLogin;
    String uname = "";
    String passw = "";
    String userId2 = "";
    String BP = "";


    MenuItem searchItem = null;
    SearchView searchView = null;


    String mType="";
    String mKey=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productsearch);

        //-----------------------------url &  login  ------------------------------------------
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        uRl= sharedpreferences.getString("URL",null);
        if (uRl != null) {
            sharedpreferencesLogin = getSharedPreferences(LoginPREFERENCES, Context.MODE_PRIVATE);
            uname = sharedpreferencesLogin.getString("uName", null);
            passw = sharedpreferencesLogin.getString("passWord", null);
            userId2 = sharedpreferencesLogin.getString("userId", null);


            if (uname == null && passw == null) {
                Intent i1 = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i1);
                finish();

            }
        }
        else {
            Intent i = new Intent(getApplicationContext(), inserturl_noparent.class);
            startActivity(i);
            finish();

        }

        Fragment fragment = new serviceSearch_fragment();
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.liner, fragment).commit();

        // setTitle("");

    }


    @Override
    public void onBackPressed() {

        Intent i = new Intent(this, insert_service.class);
        startActivity(i);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.productsearch, menu);

        searchItem = menu.findItem(R.id.action_search);

        if (searchItem != null) {
            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    //some operation
                    return false;
                }
            });
            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //some operation
                }
            });

            View searchPlateView = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
            searchPlateView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            // use this method for search process
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    servicesearchflag=true;
                    Fragment fragment = new serviceSearch_fragment("", newText);
                    FragmentManager fragmentManager = getSupportFragmentManager();

                    fragmentManager.beginTransaction().replace(R.id.liner, fragment).commit();
                    try {
                        Thread.sleep(500);

                    } catch (InterruptedException e) {

                    }

                    return true;

                }

            });


        }
        return super.onCreateOptionsMenu(menu);
    }


}
