package com.example.watshala.kuclassroom;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.watshala.kuclassroom.JSON.JSONParser;
import com.example.watshala.kuclassroom.JSON.JsonDatabase;
import com.example.watshala.kuclassroom.MenuActivity.*;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class  MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final String KATHMANDU_UNIVERSITY = "Kathmandu_University";


    JsonDatabase jsonData;

    JSONParser parser;
    String Data;

    String[] URL = {
            "https://watshalashrestha.github.io/KUScheduleFiles/IYIS.json",
            "https://watshalashrestha.github.io/KUScheduleFiles/IIYIIS.json",
            "https://watshalashrestha.github.io/KUScheduleFiles/IIIYIS.json",
            "https://watshalashrestha.github.io/KUScheduleFiles/IIIYIIS.json",
            "https://watshalashrestha.github.io/KUScheduleFiles/IVYIS.json",
            "https://watshalashrestha.github.io/KUScheduleFiles/IVYIIS.json"
    };

    String Subject;
    String Lecturer;
    String Day;
    String Start;
    String End;
    String Dept;
    String Year;
    String Sem;

    int version;

    ConnectivityManager connMgr;
    NetworkInfo networkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseMessaging.getInstance().subscribeToTopic(KATHMANDU_UNIVERSITY);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        displaySelectedScreen(R.id.nav_calendar); //Displays Schedule Fragment as default

        jsonData = new JsonDatabase(this); //Calls the constructor of JsonDatabase.java

        connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent aboutus = new Intent(MainActivity.this,About_Us_Activity.class);
        Intent settings = new Intent(MainActivity.this,Settings_Activity.class);
        switch (id){
            case R.id.action_settings:
                new GetSchedule().execute(); //Executes GetSchedule Class (AsyncTask)
                break;
            case R.id.about_us:
                startActivity(aboutus);
                break;
            case R.id.settings:
                startActivity(settings);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        displaySelectedScreen(id);
        return true;
    }

    private void displaySelectedScreen(int id){
        Fragment fragment = null;

        switch(id){
            case R.id.nav_calendar:
                fragment = new Schedule();
                break;
            case R.id.nav_notice:
                fragment = new Notice();
                break;
            case R.id.nav_attendance:
                fragment = new Attendance();
                break;
            case R.id.nav_gpa:
                fragment = new GPA_Calculator();
                break;
            case R.id.nav_deflection:
                fragment = new DeflectionCalculator();
                break;

            case R.id.staff:
                fragment = new StaffProfile();
                break;
        }
        if(fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main,fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }



    /**
     * Async task class to get JSON by making JSONParser call
     */
    public class GetSchedule extends AsyncTask<Boolean, Void, Boolean> {
        ProgressDialog progressDialog;

        JSONParser parser;

        GetSchedule(){

        }


        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Updating Schedule!!");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Boolean... booleans) {
            if (isUpdated()) {
                for (int j = 0; j < URL.length; j++) {
                    parser = new JSONParser();
                    Data = parser.getJson(URL[j]);
                    getDataFromServer();
                }
                return true;
            }else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean s) {
            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            if(s)
                Toast.makeText(MainActivity.this, "New Schedule Updated!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MainActivity.this, "No New Schedule", Toast.LENGTH_SHORT).show();
        }
    }

    public void getDataFromServer() {
        parser = new JSONParser();
        jsonData.ReplaceData();
        for(int j = 0; j<URL.length;j++) {
            Data = parser.getJson(URL[j]);
            if (Data != null) {
                try {
                    JSONObject jsonObject = new JSONObject(Data);
                    JSONArray schedule = jsonObject.getJSONArray("schedule");
                    for (int i = 0; i < schedule.length(); i++) {
                        JSONObject eachObject = schedule.getJSONObject(i);
                        Subject = eachObject.getString("subject");
                        Lecturer = eachObject.getString("lecturer");
                        Day = eachObject.getString("day");
                        Start = eachObject.getString("start");
                        End = eachObject.getString("end");
                        Dept = eachObject.getString("dept");
                        Year = eachObject.getString("year");
                        Sem = eachObject.getString("sem");
                        jsonData.insertJSONData(Subject, Lecturer, Day, Start, End, Dept, Year, Sem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getVersion() {
        parser = new JSONParser();
        for (int j = 0; j < URL.length;j++) {
            Data = parser.getJson(URL[j]);
            if (Data != null) {
                try {
                    JSONObject jsonObject = new JSONObject(Data);
                    version = jsonObject.getInt("version");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return version;
        }
        return 1;
    }

    public boolean isUpdated(){
        //Update The schedule if The version number is different
        SharedPreferences pref = this.getPreferences(Context.MODE_PRIVATE);
        int ver = pref.getInt("Version_Control", 1);
        SharedPreferences.Editor editor = pref.edit();
        Log.d(TAG, "isUpdated: "+getVersion()+" "+ver);
        if (getVersion() != ver && networkInfo != null && networkInfo.isConnected()) {
            getDataFromServer();
            editor.putInt("Version_Control", getVersion());
            editor.commit();
            return true;
        }
        else
            return false;
    }

}
