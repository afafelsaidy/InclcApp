package com.example.afaf.inclcapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by enterprise on 13/04/17.
 */

public class inserturl_noparent extends AppCompatActivity {


    public static final String MyPREFERENCES = "MyPrefs";
    public static final String URL = "URL";
    EditText url;
    Button btn;
    SharedPreferences sharedpreferences;


    String uRl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inserturl);
        setTitle("Settings");

        url = (EditText) findViewById(R.id.url);
        if (URL != null) {
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            uRl = sharedpreferences.getString("URL", null);
            url.setText(uRl);
        }

        btn = (Button) findViewById(R.id.confirm);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String x = url.getText().toString();

                sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString(URL, x);

                editor.commit();

//                final ProgressDialog progressDialog = ProgressDialog.show(getApplicationContext(), "Authenticating", "Please Wait ...", true);
//
//                new android.os.Handler().postDelayed(
//                        new Runnable() {
//                            public void run() {
//                                progressDialog.dismiss();
//
//                                Intent intent1= new Intent(getApplicationContext(), LoginActivity.class);
//                                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(intent1);
//                                finish();
//                            }
//                        }, 3000);

                Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                finish();

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                finish();
            }
        });


    }


    @Override
    public void onBackPressed() {

    }


}

