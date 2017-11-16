package com.happymeal.tupapp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

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

public class MyScheduleFragment extends Fragment {
    ProgressDialog pd;
    FunDapter adapter;
    ArrayList<Schedule> schedules = new ArrayList<>();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String username;
    private String password;

    private OnFragmentInteractionListener mListener;

    public MyScheduleFragment() {

    }

    public static MyScheduleFragment newInstance(String param1, String param2) {
        MyScheduleFragment fragment = new MyScheduleFragment();
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
            username = getArguments().getString(ARG_PARAM1);
            password = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_schedule, container, false);

        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Schedule");

        }catch (NullPointerException ex){
            ex.printStackTrace();
        }
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                FileCacher<String> schedule = new FileCacher<>(getContext(), "schedule.txt");
                String a = "";
                try {
                    a = schedule.readCache();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (a.equals("")) {
                    if (isNetworkAvailable()) {
                        new schedule_retrieve_online().execute("https://tupapp.000webhostapp.com/index.php/Welcome/getschedule");
                    } else {
                        Toast.makeText(getContext(), "Can't retrieve schedule while offline", Toast.LENGTH_LONG).show();
                    }
                } else {
                    schedule_retrieve_offline();
                }

            } else {
                if (isNetworkAvailable()) {
                    new schedule_retrieve_online().execute("https://tupapp.000webhostapp.com/index.php/Welcome/getschedule");
                } else {
                    Toast.makeText(getContext(), "Can't retrieve schedule while offline", Toast.LENGTH_LONG).show();
                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
            if (isNetworkAvailable()) {
                new schedule_retrieve_online().execute("https://tupapp.000webhostapp.com/index.php/Welcome/getschedule");
            } else {
                Toast.makeText(getContext(), "Can't retrieve schedule while offline", Toast.LENGTH_LONG).show();
            }
        }
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

        adapter = new FunDapter(MyScheduleFragment.this.getActivity(), schedules, R.layout.schedule_layout, dictionary);
//
        ListView lvSchedules = (ListView)view.findViewById(R.id.lvSchedules);
        lvSchedules.setAdapter(adapter);


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String data) {
        if (mListener != null) {
            mListener.onFragmentInteraction(data);
        }
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

    /** Gets student's current schedule online(fixed for now)*/
    private class schedule_retrieve_online extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Getting schedule");
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

                StringBuilder buffer = new StringBuilder();
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
            if(ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                FileCacher<String> schedule = new FileCacher<>(getContext(), "schedule.txt");
                try {
                    schedule.writeCache(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String ccode,ct,ctitle,room,day,time;
            JSONArray jArray;
            JSONObject jsonObject,jObject;
            Schedule sched;
            try {
                jObject = new JSONObject(result);
                jArray = jObject.getJSONArray("query");
                try {
                    for (int i = 0; i < jArray.length(); i++) {

                        // Translating Json Object to schedule
                        jsonObject = jArray.getJSONObject(i);
                        ccode = jsonObject.getString("course_code");
                        ct = jsonObject.getString("course_title");
                        if(ct.length()>20){
                            ctitle = ct.substring(0,20) + "...";
                        }
                        else {
                            ctitle = ct;
                        }
                        room = jsonObject.getString("course_room");
                        day = jsonObject.getString("course_day");
                        time = jsonObject.getString("course_time");
                        sched = new Schedule(ccode,ctitle, day, time,room.toUpperCase());
                        schedules.add(sched);
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error fetching schedule", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error fetching schedule", Toast.LENGTH_SHORT).show();
            }catch(NullPointerException ex){
                ex.printStackTrace();
                Toast.makeText(getContext(), "Error fetching schedule", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** Gets student's current schedule offline from cache(fixed for now)*/
    private void schedule_retrieve_offline(){
        String ccode,ct,ctitle,room,day,time,result="";
        JSONArray jArray;
        JSONObject jsonObject,jObject;
        Schedule sched;
        FileCacher<String> schedule = new FileCacher<>(getContext(),"schedule.txt");
        try {
            try {
                result = schedule.readCache();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(result.equals("")){
                Toast.makeText(getContext(),"Cant get cache",Toast.LENGTH_LONG);
            }else {
                jObject = new JSONObject(result);
                jArray = jObject.getJSONArray("query");
                try {
                    for (int i = 0; i < jArray.length(); i++) {
                        /** Translating Json Object to schedule*/
                        jsonObject = jArray.getJSONObject(i);
                        ccode = jsonObject.getString("course_code");
                        ct = jsonObject.getString("course_title");
                        if (ct.length() > 20) {
                            ctitle = ct.substring(0, 20) + "...";
                        } else {
                            ctitle = ct;
                        }
                        room = jsonObject.getString("course_room");
                        day = jsonObject.getString("course_day");
                        time = jsonObject.getString("course_time");
                        //String sp[] = jsonObject.getString("sched").split("/"); //reserve for sched
                        sched = new Schedule(ccode, ctitle, day, time, room.toUpperCase());
                        schedules.add(sched);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Can't fetch data, try again later", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Can't fetch data, try again later", Toast.LENGTH_SHORT).show();
        }
    }

    /** Checks for internet connection */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void clearschedule(){
        FileCacher<String> eventcache = new FileCacher<>(getContext(), "events.txt");
        String a = "";
        String[] b;
        try {
            a = eventcache.readCache();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_CALENDAR)
                == PackageManager.PERMISSION_GRANTED) {
            if (!(a.equals(""))) {
                b = a.split(",");
                for (String aB : b) {
                    aB = aB.replace(" ", "");
                    DeleteEvent(Integer.parseInt(aB));
                }
            }
        }
    }

    public void DeleteEvent(int your_event_id){
        Uri deleteUri;
        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, your_event_id);
        getActivity().getContentResolver().delete(deleteUri, null, null);
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String data);
    }
}
