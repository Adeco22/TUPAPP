package com.happymeal.tupapp;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MyDashboardFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    LinearLayout sell;
    LinearLayout browse;
    LinearLayout calendar;
    LinearLayout handbook;
    TextView dashboard_greet,dashboard_name;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View view;

    private OnFragmentInteractionListener mListener;

    public MyDashboardFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MyDashboardFragment newInstance(String param1, String param2) {
        MyDashboardFragment fragment = new MyDashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_dashboard, container, false);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) view.findViewById(R.id.toolbar);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Dashboard");

        }catch (NullPointerException ex){
            ex.printStackTrace();
//            Toast.makeText(getContext(),"PUTANGINA",Toast.LENGTH_LONG).show();
        }


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sell = (LinearLayout) view.findViewById(R.id.sellItem);
        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSellItem();
            }
        });
        browse = (LinearLayout) view.findViewById(R.id.browseItem);
        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBrowseItem();
            }
        });
        calendar= (LinearLayout) view.findViewById(R.id.viewcalendar);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goCalendar();
            }
        });
        handbook= (LinearLayout) view.findViewById(R.id.viewhandbook);
        handbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHandbook();
            }
        });
//
        dashboard_name = (TextView)view.findViewById(R.id.dashboard_name);
        dashboard_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyProfileFragment profileFragment = MyProfileFragment.newInstance(mParam1,mParam2);
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.relativelayout_for_fragment,
                        profileFragment,
                        profileFragment.getTag()
                ).commit();
            }
        });
        //profile_retrieve_offline();
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String data) {
        if (mListener != null) {
            mListener.onFragmentInteraction(data);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

//    public void updateTextView(String greettime,String greetname) {
//        dashboard_greet.setText(greettime);
//        dashboard_name.setText(greetname);
//    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String data);
    }

    public void goSellItem(){
        /*
        AddItemFragment addItemFragment = AddItemFragment.newInstance(mParam1,"My Dashboard");
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.beginTransaction().add(R.id.relativelayout_for_fragment,
                addItemFragment,
                addItemFragment.getTag()
        ).commit();
        */

    }
    public void goBrowseItem(){
        /*if (isNetworkAvailable()) {
            EcsFragment ecsFragment = EcsFragment.newInstance(mParam1);
            FragmentManager manager = getActivity().getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayout_for_fragment,
                    ecsFragment,
                    ecsFragment.getTag()
            ).commit();

        } else {
            Toast.makeText(getContext(), "Lost internet connection!", Toast.LENGTH_LONG).show();
        }
        */
    }
    public void goCalendar(){
        startActivity(new Intent (Intent.ACTION_VIEW, Uri.parse ("content://com.android.calendar/time/")));
    }

    public void goHandbook(){

        DownloadManager manager;
        long fopen;
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        "TUP-Student-Handbook.pdf");
                Uri path = Uri.fromFile(file);
                Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pdfOpenintent.setDataAndType(path, "application/pdf");
                startActivity(pdfOpenintent);
            }
        };
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "TUP-Student-Handbook.pdf");
            if (file.isFile()) {

                Uri path = Uri.fromFile(file);
                Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pdfOpenintent.setDataAndType(path, "application/pdf");
                startActivity(pdfOpenintent);
            } else {
                DownloadManager.Query query = new DownloadManager.Query();
                Cursor l = null;
                try{

                    if(ActivityCompat.checkSelfPermission(getContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(getContext(), "Please allow writing permission in app settings!", Toast.LENGTH_LONG).show();
                    }else {
                        l.getExtras();
                        Toast.makeText(getContext(), "File is already being downloaded, Please wait", Toast.LENGTH_LONG).show();
                    }
                }catch (NullPointerException ex){
                    String url = "http://tup.edu.ph/download/TUP-Student-Handbook.pdf";
                    final DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setTitle("TUP Student Handbook");
                    request.setDescription("File is being downloaded.....");

                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                    String nameOfFile = URLUtil.guessFileName(url, null,
                            MimeTypeMap.getFileExtensionFromUrl(url));

                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nameOfFile);

                    manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                    fopen = manager.enqueue(request);
                    Toast.makeText(getContext(), "File is being downloaded, Please wait", Toast.LENGTH_LONG).show();
                    l = manager.query(query);
                    getActivity().registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    l = null;
                }
            }

        }catch(SecurityException e){
            e.printStackTrace();
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }
    /** Checks for internet connection */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    /** Gets student's profile from file profile.txt */
    private void profile_retrieve_offline(){
        /*FileCacher<String> profilecache = new FileCacher<>(getContext(),"profile.txt");
        String[] f;
        String[] f2;
        JSONObject jObject;
        String fname, lname, initial, course,string="",a="";
        //TextView pname = (TextView) findViewById(R.id.p_name);
        //TextView pcourse = (TextView) findViewById(R.id.p_course);

        try {
            string = profilecache.readCache();
        } catch (IOException e) {
            e.printStackTrace();
        }
        f2 = string.split(",");
        String greetname, greettime;
        String [] arr;
        Date current = Calendar.getInstance().getTime();
        DateFormat hr = new SimpleDateFormat("H", Locale.getDefault());
        Integer hour = Integer.parseInt(hr.format(current));
        a = f2[2];
        arr = a.split(" ");
        greetname = "";
        for (String anArr : arr) {
            greetname += anArr.substring(0, 1).toUpperCase() + anArr.substring(1).toLowerCase()+" ";
        }
        if(-1 < hour && hour < 6){
            greettime = "Good Mornight ";
        }
        else if(5 < hour && hour < 12){
            greettime = "Good Morning ";
        }
        else if(11 < hour && hour < 18){
            greettime = "Good Afternoon ";
        }
        else if(17 < hour){
            greettime = "Good Evening ";
        }
        else {
            greettime = "Hello ";
        }

        dashboard_greet = (TextView)view.findViewById(R.id.dashboard_greet);
        dashboard_name = (TextView)view.findViewById(R.id.dashboard_name);

        dashboard_greet.setText(greettime);
        dashboard_name.setText(greetname);
        //TextView gr = (TextView) findViewById(R.id.greet);
        //gr.setText(greettime + " " + greetname + '!');
        //Toast.makeText(getContext(),greettime + greetname + '!',Toast.LENGTH_SHORT).show();
        */
    }
}
