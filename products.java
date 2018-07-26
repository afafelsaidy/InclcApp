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


import com.example.afaf.inclcapp.helper_database.product_adapter;
import com.example.afaf.inclcapp.helper_database.product_helper;
import com.example.afaf.inclcapp.helper_database.product_model;

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
 * Created by enterprise on 24/04/17.
 */

public class products extends ListFragment {


    // login
    public static final String LoginPREFERENCES = "LoginPrefs";
    public static final String UserName = "username";
    public static final String PassWord = "password";
    public static final String MyPREFERENCES = "MyPrefs";
    public static String nuitno = "";
    public static String productID = "";
    List<product_model> list;
    product_helper db;
    String textName = "";
    String productname = "";
    String sessionno = "";
    String price = "";
    String warehouseRuleType = "";
    String service = "";
    String serviceProductId="";
    String productHisId="";


    SharedPreferences sharedpreferencesLogin;
    String uname = "";
    String passw = "";
    String userId = "";
    String BP = "";
    SharedPreferences sharedpreferences;
    String uRl = "";

    SwipeRefreshLayout swipeContainer;
    int flag_update = 1;


    public products() {
    }

    @SuppressLint("ValidFragment")
    public products(String s, Object o) {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.products, container, false);

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

        if (isNetworkAvailable() == true && flag_update == 1 || MainActivity.if_update == false) {
            AsyncCall task = new AsyncCall();
            task.execute();
            flag_update = 0;

            System.out.print(flag_update);
            System.out.print(MainActivity.if_update);

            try {
                Thread.sleep(1300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            list = db.getAllProducts();
            product_adapter adapter = new product_adapter(getListView().getContext(), list, db);
            setListAdapter(adapter);

        } else if (isNetworkAvailable() == true && MainActivity.if_update == true) {
            list = db.getAllProducts();
            product_adapter adapter = new product_adapter(getListView().getContext(), list, db);
            setListAdapter(adapter);

        } else {
            list = db.getAllProducts();
            product_adapter adapter = new product_adapter(getListView().getContext(), list, db);
            setListAdapter(adapter);

        }
        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeContainerPro);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list = db.getAllProducts();
                product_adapter adapter = new product_adapter(getListView().getContext(), list, db);
                setListAdapter(adapter);

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
        db = new product_helper(getActivity());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        MainActivity.fabflag = false;
        int idint = (int) (id + 1);
        product_model model = null;
        try {
            model = db.readProduct(idint);
        } catch (JSONException e) {

        }


        String productname = model.getProductName();
        String session = model.getSessionno();
        String price = model.getNetunitprice();

        Intent i = new Intent(getActivity(), products_activity.class);

        i.putExtra("id", model.getId() + "");
        i.putExtra("productname", productname);
        i.putExtra("session", session);
        i.putExtra("price", price);
        i.putExtra("unitno", model.getUnitno());
        i.putExtra("productId", model.getProductID());
        i.putExtra("warehouseRuleType", model.getWarehouseRuleType());
        i.putExtra("service", model.getService());
        i.putExtra("serviceProductId", model.getServiceProductId());
        i.putExtra("productHisId", model.getProductHistoryID());


        startActivity(i);
        getActivity().finish();
    }
    // http://192.168.1.30:8080/openhats/ws/com.opentus.inshape.clinic.getproduct?custid=A155D579DAB541A598DED13A157C09A0

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
                    db.createProduct(productname, sessionno, price, nuitno, productID, warehouseRuleType, service, serviceProductId, productHisId );

                    textName = "";


                }


            } else if (eventType == XmlPullParser.TEXT) {

                if (textName.equals("id") && !xpp.getText().startsWith("\n")) {

                    if (!xpp.getText().startsWith("\n")) {
                        productHisId = xpp.getText();
                    } else {
                        productHisId = "";
                    }
                }

                if (textName.equals("sessionNo") && !xpp.getText().startsWith("\n")) {
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

                if (textName.equals("unitno") && !xpp.getText().startsWith("\n")) {
                    //  xx[i] = xpp.getText()

                    if (!xpp.getText().startsWith("\n")) {
                        nuitno = xpp.getText();
                    } else {
                        nuitno = "";
                    }

                }

                if (textName.equals("warehouseRuleType") && !xpp.getText().startsWith("\n")) {
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
            Intent i = getActivity().getIntent();
            String s = i.getStringExtra("patientId");

            doTestGetRequest("/ws/com.opentus.inshape.clinic.getproduct?custid=" + s);

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