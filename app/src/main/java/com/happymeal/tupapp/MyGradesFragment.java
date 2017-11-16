package com.happymeal.tupapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;
import com.kosalgeek.android.caching.FileCacher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MyGradesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";

    FunDapter adapter;
    ProgressDialog pd;
    ArrayList<Schedule> schedules = new ArrayList<>();
    Fragment fragment = this;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;
    private String mParam4;

    private OnFragmentInteractionListener mListener;

    public MyGradesFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MyGradesFragment newInstance(String param1, String param2, String param3, String param4) {
        MyGradesFragment fragment = new MyGradesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            mParam4 = getArguments().getString(ARG_PARAM4);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_my_grades, container, false);

        /*if(ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            FileCacher<String> grades = new FileCacher<>(getContext(), "grades.txt");
            String a = "";
            try {
                a = grades.readCache();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (a.equals("")) {
                if (isNetworkAvailable()) {
                    new grades_retrieve_online().execute("http://www.tup.edu.ph/android/mygrades/" + mParam1 + "/" + mParam2 + "/+"+mParam3+"+:"+mParam4);
                } else {
                    Toast.makeText(getContext(), "Can't retrieve grades while offline", Toast.LENGTH_LONG).show();
                }
            } else {
                grades_retrieve_offline();
            }
        }else {
            if (isNetworkAvailable()) {
            new grades_retrieve_online().execute("http://www.tup.edu.ph/android/mygrades/" + mParam1 + "/" + mParam2 + "/1st%20Semester:2016-2017");
            } else {
                Toast.makeText(getContext(), "Can't retrieve grades while offline", Toast.LENGTH_LONG).show();
            }
        }*/
        Log.d("mParam1: ",mParam1);
        Log.d("mParam2: ",mParam2);
        Log.d("mParam3: ",mParam3);
        Log.d("mParam4: ",mParam4);
        new grades_retrieve_online().execute("http://www.tup.edu.ph/android/mygrades/" + mParam1 + "/" + mParam2 + "/"+mParam3+":"+mParam4);
        BindDictionary<Schedule> dictionary = new BindDictionary<>();
        dictionary.addStringField(R.id.tvCcode, new StringExtractor<Schedule>() {
            @Override
            public String getStringValue(Schedule schedule, int position) {
                return schedule.getCoursecode();
            }
        });

        dictionary.addStringField(R.id.tvCtitle, new StringExtractor<Schedule>() {
            @Override
            public String getStringValue(Schedule schedule, int position) {
                return schedule.getCoursetitle();
            }
        });

        dictionary.addStringField(R.id.tvDate, new StringExtractor<Schedule>() {
            @Override
            public String getStringValue(Schedule schedule, int position) {
                return schedule.getDate();
            }
        });

        dictionary.addStringField(R.id.tvTime, new StringExtractor<Schedule>() {
            @Override
            public String getStringValue(Schedule schedule, int position) {
                return schedule.getTime();
            }
        });

        dictionary.addStringField(R.id.tvRoom, new StringExtractor<Schedule>() {
            @Override
            public String getStringValue(Schedule schedule, int position) {
                return schedule.getRoom();
            }
        });
        adapter = new FunDapter(MyGradesFragment.this.getActivity(), schedules, R.layout.grade_layout, dictionary);

        Button back = (Button) view.findViewById(R.id.gback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(fragment).commit();
            }
        });
        ListView lvgrades = (ListView)view.findViewById(R.id.lvGrades);
        lvgrades.setAdapter(adapter);
        return view;
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

    /** Gets student's current grades online(fixed for now)*/
    private class grades_retrieve_online extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Getting grades");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                // txtJson.setText();
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
                }
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }
            /*if(ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                FileCacher<String> grades = new FileCacher<>(getContext(), "grades.txt");
                try {
                    grades.writeCache(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/
            String subcode, subdes, grade, units, remark,sd;
            JSONObject jObject, jsonObject;
            Schedule sched;
            Double gr;

            try {

                jObject = new JSONObject(result);
                JSONArray student = jObject.getJSONArray("student");
                try {
                    for (int i = 0; i < student.length(); i++) {
                        /** Translating Json Object to schedule*/
                        jsonObject = student.getJSONObject(i);
                        subcode = jsonObject.getString("subcode");
                        sd = jsonObject.getString("subdes");
                        if (sd.length() > 26) {
                            subdes = sd.substring(0, 26) + "...";
                        } else {
                            subdes = sd;
                        }
                        grade = jsonObject.getString("grade");
                        units = jsonObject.getString("units");
                        if(grade.equals("-")){
                            remark = "-";
                        }else {
                            gr = Double.parseDouble(grade);
                            if(gr <= 3.00){
                                remark = "PASS";
                            }else{
                                remark = "FAIL";
                            }
                        }
                       /* TextView troll = (TextView) getActivity().findViewById(R.id.tvCtitle);
                        troll.setMa*/
                        sched = new Schedule(subcode,subdes,remark , grade,units);
                        schedules.add(sched);
                        adapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error fetching grades", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error fetching grades", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** Gets student's current grades offline from cache(fixed for now)*/
    private void grades_retrieve_offline(){
        String result="";
        FileCacher<String> grades = new FileCacher<>(getContext(),"grades.txt");
        try {
            result= grades.readCache();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String subcode, subdes, grade, units, remark,sd,f, b;
        JSONObject jObject, jsonObject;
        Schedule sched;
        Double gr;
        try {

            jObject = new JSONObject(result);
            JSONArray student = jObject.getJSONArray("student");
            try {
                for (int i = 0; i < student.length(); i++) {
                    /** Translating Json Object to schedule*/
                    jsonObject = student.getJSONObject(i);
                    subcode = jsonObject.getString("subcode");
                    sd = jsonObject.getString("subdes");
                    if (sd.length() > 26) {
                        subdes = sd.substring(0, 26) + "...";
                    } else {
                        subdes = sd;
                    }
                    grade = jsonObject.getString("grade");
                    units = jsonObject.getString("units");
                    if(grade.equals("-")){
                        remark = "-";
                    }else {
                        gr = Double.parseDouble(grade);
                        if(gr<=3.00){
                            remark = "PS";
                        }else{
                            remark = "FL";
                        }
                    }
                    grade = "\u0020"+grade;
                    sched = new Schedule(subcode,subdes,remark , grade,units);
                    schedules.add(sched);
                    //adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error fetching grades", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error fetching grades", Toast.LENGTH_SHORT).show();
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String data);
    }

    /** Checks for internet connection */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
