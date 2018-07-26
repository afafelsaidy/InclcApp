package com.example.afaf.inclcapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.example.afaf.inclcapp.helper_database.productSelector_helper;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by enterprise on 22/05/17.
 */

public class productSearch extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs";
    // ----------------------- log in ------------------------------------------------------
    public static final String LoginPREFERENCES = "LoginPrefs";
    public static boolean fabflag = false;
    public static boolean searchflag = false;
    SharedPreferences sharedpreferences;
    String uRl = "";
    SharedPreferences sharedpreferencesLogin;
    String uname = "";
    String passw = "";
    String userId2 = "";
    String BP = "";

    productSelector_helper dbprod = new productSelector_helper(this);
    String prodkey = "";
    String prodname = "";
    String prodId = "";



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

        Fragment fragment = new searchfragment();
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.liner, fragment).commit();

       // setTitle("");

       AsyncCallWS task = new AsyncCallWS();
        task.execute();

    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent(this, insert_products.class);
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
                    searchflag= true;
                    Fragment fragment = new searchfragment("", newText);
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

    private class AsyncCallWS extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {


            productSelector("/ws/com.opentus.inshape.clinic.product_selector?");


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

                System.out.println(newarray);

                if (newarray != null) {
                    prodId = newarray.getString(0);
                        prodname = newarray.getString(1);
                        prodkey = newarray.getString(2);

                }

                dbprod.createProductSelector(prodkey, prodname, prodId);

            }


        } catch (final Exception e) {

            e.getMessage();
        }

        return null;
    }

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
}
