package com.happymeal.tupapp;


import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.database.Cursor;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import android.provider.CalendarContract;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;

import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MyItemFragment.OnFragmentInteractionListener,
        AddItemFragment.OnFragmentInteractionListener,
        EditItemFragment.OnFragmentInteractionListener,
        EcsFragment.OnFragmentInteractionListener,
        MyScheduleFragment.OnFragmentInteractionListener,
        MyGradesFragment.OnFragmentInteractionListener,
        AboutUsFragment.OnFragmentInteractionListener,
        MyGradesListFragment.OnFragmentInteractionListener,
        MyProfileFragment.OnFragmentInteractionListener,
        MyDashboardFragment.OnFragmentInteractionListener
        {
            String username, password, name, course;
            private Toast toast;
            public Activity activity = this;
            DownloadManager manager;
            long fopen;
            private boolean doubleBackToExitPressedOnce;
            private Handler mHandler = new Handler();


            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);

                Intent intent = getIntent();
                username = intent.getExtras().getString("username");
                password = intent.getExtras().getString("password");
                name = intent.getExtras().getString("name");
                course = intent.getExtras().getString("course");

                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);
                        setprofile(name, course);
                    }};
                drawer.addDrawerListener(toggle);
                toggle.syncState();
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(this);

                MyDashboardFragment myDashboardFragment = MyDashboardFragment.newInstance(username,password);
                FragmentManager manager = getSupportFragmentManager();

                manager.beginTransaction().replace(R.id.relativelayout_for_fragment,
                        myDashboardFragment,
                        myDashboardFragment.getTag()
                ).commit();

    }
            private final Runnable mRunnable = new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            };

            @Override
            protected void onDestroy()
            {
                super.onDestroy();

                if (mHandler != null) { mHandler.removeCallbacks(mRunnable); }
            }

            @Override
            public void onBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                showToast("Press once again to exit",false);
                mHandler.postDelayed(mRunnable, 2000);
            }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        BroadcastReceiver receiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        "TUP-Student-Handbook.pdf");
                Uri path = Uri.fromFile(file);
                Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pdfOpenintent.setDataAndType(path, "application/pdf");
                startActivity(pdfOpenintent);
            }
        };
        if (id == R.id.nav_my_dashboard)
        {
            MyDashboardFragment myDashboardFragment = MyDashboardFragment.newInstance(username,password);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayout_for_fragment,
                    myDashboardFragment,
                    myDashboardFragment.getTag()
            ).commit();
        }
        else if (id == R.id.nav_my_schedule)
        {
            MyScheduleFragment myScheduleFragment = MyScheduleFragment.newInstance(username, password);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayout_for_fragment,
                    myScheduleFragment,
                    myScheduleFragment.getTag()
            ).commit();
        }
        else if (id == R.id.nav_my_grades)
        {
            if(isNetworkAvailable())
            {
                MyGradesListFragment myGradesListFragment = MyGradesListFragment.newInstance(username, password);
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().add(R.id.relativelayout_for_fragment,
                        myGradesListFragment,
                        myGradesListFragment.getTag()
                ).commit();
            }else
            {
                showToast("No internet connection!",false);
            }
        }
        else if (id == R.id.nav_my_account)
        {
            MyProfileFragment profileFragment = MyProfileFragment.newInstance(username, password);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayout_for_fragment,
                    profileFragment,
                    profileFragment.getTag()
            ).commit();
        }
        else if (id == R.id.nav_ecs)
        {/*
            if (isNetworkAvailable())
            {
                EcsFragment ecsFragment = EcsFragment.newInstance(username);
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.relativelayout_for_fragment,
                        ecsFragment,
                        ecsFragment.getTag()
                ).commit();
            }else
            {
                showToast("No internet connection!",false);
            }
           */
            showToast("Feature disabled",false);
        }
        else if (id == R.id.nav_myi)
        {
            /*
            if(isNetworkAvailable())
            {
                MyItemFragment myItemFragment = MyItemFragment.newInstance(username);
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.relativelayout_for_fragment,
                        myItemFragment,
                        myItemFragment.getTag()
                ).commit();
            }else
            {
                showToast("No internet connection!",false);
            }
            */
            showToast("Feature disabled",false);
        }
        else if (id == R.id.nav_student_handbook)
        {
                try
                {
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "TUP-Student-Handbook.pdf");
                    if (file.isFile())
                    {
                        Uri path = Uri.fromFile(file);
                        Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                        pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pdfOpenintent.setDataAndType(path, "application/pdf");
                        startActivity(pdfOpenintent);
                    } else
                    {
                        DownloadManager.Query query = new DownloadManager.Query();
                        Cursor l = null;
                        try
                        {
                            if(ActivityCompat.checkSelfPermission(this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED)
                            {
                                showToast("Please allow writing permission in app settings!",false);

                            }else
                            {
                                l.getExtras();
                                showToast("File is already being downloaded, Please wait",false);
                            }
                        }catch (NullPointerException ex)
                        {
                            String url = "http://tup.edu.ph/download/TUP-Student-Handbook.pdf";
                            final DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                            request.setTitle("TUP Student Handbook");
                            request.setDescription("File is being downloaded.....");
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            String nameOfFile = URLUtil.guessFileName(url, null,
                                    MimeTypeMap.getFileExtensionFromUrl(url));
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nameOfFile);
                            manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                            fopen = manager.enqueue(request);
                            showToast("File is already being downloaded, Please wait",false);
                            l = manager.query(query);
                            registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                            l = null;
                        }
                    }
                }catch(SecurityException e)
                {
                    e.printStackTrace();
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
        }
        else if (id == R.id.nav_about_us)
        {
            AboutUsFragment aboutUsFragment = AboutUsFragment.newInstance("Welcome", "HappyMeal");
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayout_for_fragment,
                    aboutUsFragment,
                    aboutUsFragment.getTag()
            ).commit();
        }
        else if (id == R.id.nav_logout)
        {
            SharedPreferences credentials = getSharedPreferences("credentials",MODE_PRIVATE);
            SharedPreferences profile = getSharedPreferences("profile",MODE_PRIVATE);
            credentials.edit().clear().apply();
            profile.edit().clear().apply();

            Intent logout = new Intent(getApplicationContext(), Login.class);
            username = "";
            password = "";
            startActivity(logout);
            showToast("Successfully logged out",false);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(String data)
    {
        Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
    }

    /** Checks for internet connection */
    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
                {
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    {
                        Toast.makeText(getApplicationContext(), "Permission Granted! YEY :D", Toast.LENGTH_LONG).show();
                    } else
                    {
                        Toast.makeText(getApplicationContext(), "The app needs all permissions", Toast.LENGTH_LONG).show();
                    }
                }
        }
    }

    public void DeleteEvent(int your_event_id)
    {
        Uri deleteUri;
        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, your_event_id);
        getContentResolver().delete(deleteUri, null, null);
    }

    public void setprofile(String name, String course)
    {
        StringBuilder full= new StringBuilder();
        String[] a;
        TextView pname = (TextView) findViewById(R.id.p_name);
        TextView pcourse = (TextView) findViewById(R.id.p_course);
        TextView pidno = (TextView) findViewById(R.id.p_idno);

        a = name.split(" ");
        for (String aB : a)
        {
            full.append(aB.substring(0, 1).toUpperCase()).append(aB.substring(1).toLowerCase()).append(" ");
        }
        pidno.setText(username);
        pname.setText(full.toString());
        pcourse.setText(course);
    }

    private void showToast(String message, Boolean isLong)
    {
        try{if(toast.getView().isShown()){toast.cancel();} }catch (NullPointerException e){toast = new Toast(getApplicationContext());}
        if(isLong){ toast = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG);}
        else { toast = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT);}
        toast.show();
    }
}
