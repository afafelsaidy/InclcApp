package com.example.afaf.inclcapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afaf.inclcapp.helper_database.product_helper;
import com.example.afaf.inclcapp.helper_database.product_model;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    //------------------- url -------------------------------------------------------------
    public static final String MyPREFERENCES = "MyPrefs";
    // ----------------------- log in ------------------------------------------------------
    public static final String LoginPREFERENCES = "LoginPrefs";
    public static final String UserName = "username";
    public static final String PassWord = "password";
    protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
    // take photo
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int BUFFER_SIZE = 4096;
    private static final String TAG = "upload";
    private static final int CAMERA_REQUEST = 1888;
    private static final int CONTENT_REQUEST = 1337;
    public static String nuitno = "";
    //  public static final String URL = "nameKey";
    public static String productID = "";
    public static String imageFileName;
    public static boolean fabflagmain2 = false;
    private static String mFileName = null;
    List<product_model> list;
    product_helper db;
    String textName = "";
    String productname = "";
    String sessionno = "";
    String price = "";
    SharedPreferences sharedpreferences;
    String uRl = "";
    SharedPreferences sharedpreferencesLogin;
    String uname = "";
    String passw = "";
    String userId = "";
    String patientName;
    String mCurrentPhotoPath;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private File output, dir = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        // set title
        Intent i = getIntent();
        patientName = i.getStringExtra("patientname");
        if (patientName != null) {
            this.setTitle(patientName);
        }

        //-----------------------------url &  login  ------------------------------------------
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        uRl = sharedpreferences.getString("URL", null);


        if (uRl != null) {
            sharedpreferencesLogin = getSharedPreferences(LoginPREFERENCES, Context.MODE_PRIVATE);
            uname = sharedpreferencesLogin.getString("uName", null);
            passw = sharedpreferencesLogin.getString("passWord", null);
            userId=sharedpreferencesLogin.getString("userId", null);
            if (uname == null && passw == null) {
                Intent i1 = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i1);
                finish();
            }
        }
        else{
            Intent in = new Intent(getApplicationContext(), insert_url.class);
            startActivity(in);
            //finish();
        }

        // ---------------------------------------------------------------------

        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        // fab actions
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        FloatingActionButton fab4 = (FloatingActionButton) findViewById(R.id.fab4);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabflagmain2 = true;
                Intent i = new Intent(getApplicationContext(), insert_service.class);

                startActivity(i);
                finish();

            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabflagmain2 = true;
                Intent i = new Intent(getApplicationContext(), insert_products.class);

                startActivity(i);
                finish();

            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
                String currentTimeStamp = dateFormat.format(new Date());
                output = new File(dir, "photo_" + currentTimeStamp + ".jpg");
                Uri uri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getApplicationContext().getPackageName() + ".provider", output);

                i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(i, REQUEST_TAKE_PHOTO);

            }
        });



        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabflagmain2 = true;
                AsyncCallWS1 task1 = new AsyncCallWS1();
                task1.execute();

                Toast.makeText(getApplicationContext(), "This Check successfully Closed", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("Window", "4");
                startActivity(i);
                finish();

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_TAKE_PHOTO) {
                    if (resultCode == RESULT_OK) {
                        Toast.makeText(this, "Your photo was successfully captured", Toast.LENGTH_SHORT).show();

                        AsyncCallWS task = new AsyncCallWS();
                        task.execute();
                    }

                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {

            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("Window", "4");
            startActivity(i);
            finish();

        }

        return super.onOptionsItemSelected(item);
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    // -------------------------------------Start of spinners------------------------------------------
    public HttpURLConnection createConnection(String wsPart, String method) throws Exception {
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

    //-----------------------------------close check-----------------------------
    private class AsyncCallWS1 extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            closeCheck("/ws/com.opentus.inshape.clinic.closecheck?");

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

    public String closeCheck(String wsPart) {
        try {
         //  String bbbb= patient_info.bpid;
            Intent i= getIntent();
            String appCustID=  i.getStringExtra("patientId");

            HttpURLConnection conn = createConnection(wsPart, "POST");
        //    conn.setRequestProperty("appCustID", appCustID);
            conn.connect();

            ArrayList<Object> list = new ArrayList<Object>();

            list.add(0, appCustID);

            JSONArray jsArray = new JSONArray(list);


            OutputStream os = conn.getOutputStream();

            String s = jsArray.toString();


            os.write(s.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    //--------------------------------take photo --------------------------------
    public String takePhoto(String wsPart) {
        try {

            Intent i= getIntent();
            String appCustID=  i.getStringExtra("patientId");

            String boundary = "*****";
            File uploadFile = new File(String.valueOf(output.getAbsoluteFile()));
            HttpURLConnection conn = createConnection(wsPart, "POST");
            conn.setRequestProperty("pathName", uploadFile.getPath());
            conn.setRequestProperty("imgName", uploadFile.getName());
            conn.setRequestProperty("appCustID", appCustID);
            conn.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + boundary);

            String str = new String(uploadFile.getName().getBytes(), "UTF-8");

            System.out.println(str);

            //  compress image
            Bitmap bmp = BitmapFactory.decodeFile(String.valueOf(output.getAbsoluteFile()));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] imageBytes = baos.toByteArray();

            //save compressed  bitmap
            FileInputStream inputStream = new FileInputStream(uploadFile);

            conn.connect();

            OutputStream os = conn.getOutputStream();

            int bytesRead = -1;

            System.out.println("Start writing data...");

            // os.write(s.getBytes());
            os.write(imageBytes);
            os.flush();
            os.close();


            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            conn.disconnect();

        } catch (MalformedURLException e) {


        } catch (IOException e) {

            e.getMessage();

        } catch (Exception e) {

            e.getMessage();
        }

        return null;

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main2, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = null;
            if (position == 0) {
                fragment = new patient_info();
            } else if (position == 1) {
                fragment = new products();

            } else if (position == 2) {
                fragment = new services();

            } else if (position == 3) {

                fragment = new show_history();
            }


            return fragment;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Info";
                case 1:
                    return "Products";
                case 2:
                    return "Services";
                case 3:
                    return "Show History";
            }
            return null;
        }
    }

    private class AsyncCallWS extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            takePhoto("/ws/com.opentus.inshape.clinic.takephoto?");

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


    // ----------------------------------------------------------------------------------------------

    @Override
    public void onBackPressed() {
       // if (id == android.R.id.home) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("Window", "1");
        startActivity(i);
        finish();

    }



    }
