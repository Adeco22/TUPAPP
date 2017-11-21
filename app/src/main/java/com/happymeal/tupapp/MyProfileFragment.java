package com.happymeal.tupapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ProgressDialog pd;

    private OnFragmentInteractionListener mListener;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MyProfileFragment newInstance(String param1, String param2) {
        MyProfileFragment fragment = new MyProfileFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Profile");

        }catch (NullPointerException ex){
            ex.printStackTrace();
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        retrieve_profile_offline();
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


    public void retrieve_profile_offline(){
        String fname,lname,bplace,cstatus,religion,bmonth, query="";
        String[] array, b;
        ContentValues student = new ContentValues();
        TextView pname = (TextView) getActivity().findViewById(R.id.tvpName);
        TextView pbdate = (TextView) getActivity().findViewById(R.id.tvpBdate);
        TextView pbplace = (TextView) getActivity().findViewById(R.id.tvpBPlace);
        TextView paddress = (TextView) getActivity().findViewById(R.id.tvpAddress);
        TextView ptelephone = (TextView) getActivity().findViewById(R.id.tvpTelephone);
        TextView preligion = (TextView) getActivity().findViewById(R.id.tvpReligion);
        TextView pcivil = (TextView) getActivity().findViewById(R.id.tvpCStatus);
        TextView pusername = (TextView) getActivity().findViewById(R.id.tvpUsername);
        TextView pcourse = (TextView) getActivity().findViewById(R.id.tvpCourse);
        TextView pcollege = (TextView) getActivity().findViewById(R.id.tvpCollege);
        TextView phasgrad = (TextView) getActivity().findViewById(R.id.tvpHGrad);
        TextView pemail = (TextView) getActivity().findViewById(R.id.tvpEmail);
        TextView ppass = (TextView) getActivity().findViewById(R.id.tvpPassword);
        /*FileCacher<String> profilecache = new FileCacher<>(getContext(), "profile.txt");
        try {
            query = profilecache.readCache();
        } catch (IOException e) {
            e.printStackTrace();
        }

        array = query.split(",");

        try {
            fname = array[2];
            b = fname.split(" ");
            fname = "";
            for (String aB : b) {
                fname += aB.substring(0, 1).toUpperCase() + aB.substring(1).toLowerCase();
                fname += " ";
            }

            lname = array[3];
            b = lname.split(" ");
            lname = "";
            for (String aB : b) {
                lname += aB.substring(0, 1).toUpperCase() + aB.substring(1).toLowerCase() + " ";
            }

            bplace = array[10];
            b = bplace.split(" ");
            bplace = "";
            for (String aB : b) {
                bplace += aB.substring(0, 1).toUpperCase() + aB.substring(1).toLowerCase() + " ";
            }

            cstatus = array[16];
            b = cstatus.split(" ");
            cstatus = "";
            for (String aB : b) {
                cstatus += aB.substring(0, 1).toUpperCase() + aB.substring(1).toLowerCase() + " ";
            }

            religion = array[17];
            b = religion.split(" ");
            religion = "";
            for (String aB : b) {
                religion += aB.substring(0, 1).toUpperCase() + aB.substring(1).toLowerCase() + " ";
            }

            bmonth = array[7].substring(0, 1).toUpperCase() + array[7].substring(1).toLowerCase() + " ";

            if (array[6].equals("XXX")) {
                student.put("hasgrad", "No");
            } else {
                student.put("hasgrad", "Yes");
            }

            student.put("name", fname + array[4] + ". " + lname);
            student.put("bdate", bmonth + " " + array[8] + ", " + array[9]);
            student.put("bplace", bplace);
            student.put("address", array[11] + ", " + array[12] + " " + array[13]);
            student.put("telephone", array[14]);
            student.put("religion", religion);
            student.put("cstatus", cstatus);
            student.put("username", array[0]);
            student.put("course", array[5]);
            student.put("college", array[18]);
            student.put("email", array[15]);
            student.put("password", array[1]);

            pname.setText(student.getAsString("name"));
            pbdate.setText(student.getAsString("bdate"));
            pbplace.setText(student.getAsString("bplace"));
            paddress.setText(student.getAsString("address"));
            ptelephone.setText(student.getAsString("telephone"));
            preligion.setText(student.getAsString("religion"));
            pcivil.setText(student.getAsString("cstatus"));
            pusername.setText(student.getAsString("username"));
            pcourse.setText(student.getAsString("course"));
            pcollege.setText(student.getAsString("college"));
            phasgrad.setText(student.getAsString("hasgrad"));
            pemail.setText(student.getAsString("email"));
            ppass.setText("***" + student.getAsString("password") + "***");
        }catch (NullPointerException e){
            if(isNetworkAvailable()) {
                new profile_retrieve_online().execute("http://tup.edu.ph/android/get_studprofile/" + mParam1 + "/" + mParam2);
            }else {
                Toast.makeText(getContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
            }
        }catch (ArrayIndexOutOfBoundsException e){
            if(isNetworkAvailable()) {
                new profile_retrieve_online().execute("http://tup.edu.ph/android/get_studprofile/" + mParam1 + "/" + mParam2);
            }else {
                Toast.makeText(getContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
            }
        }*/
    }

    /** Retrieves student's profile and writes to file profile.txt if possible */
    private class profile_retrieve_online extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Getting profile");
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
            JSONObject jObject;
            ContentValues data = new ContentValues();
            String fname,lname,bplace,cstatus,religion,bmonth, query="";
            String[] array, b;

            try {
                jObject = new JSONObject(result);
                JSONArray student = jObject.getJSONArray("student");
                JSONObject st = student.getJSONObject(0);
                //student name
                data.put("fname",st.getString("fname"));
                data.put("lname",st.getString("lname"));
                data.put("initial",st.getString("initial"));

                data.put("course",st.getString("course"));
                //student birth
                data.put("bmonth",st.getString("bmonth"));
                data.put("bday",st.getString("bday"));
                data.put("byear",st.getString("byear"));
                data.put("bplace",st.getString("bplace"));
                //student address
                data.put("street",st.getString("street"));
                data.put("municipality",st.getString("municipality"));
                data.put("province",st.getString("province"));
                //student contact
                data.put("telephone",st.getString("telephone"));
                data.put("email",st.getString("email"));
                //other student info
                data.put("civilstatus",st.getString("civilstatus"));
                data.put("religion",st.getString("religion"));
                data.put("college",st.getString("college"));
                data.put("graduated",st.getString("graduated"));

                if(ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    /*FileCacher<String> profile = new FileCacher<>(getContext(), "profile.txt");
                    try {
                        profile.writeCache(mParam1 +','+ mParam2+','+ data.getAsString("fname") +','+
                                data.getAsString("lname")    +','+ data.getAsString("initial") +','+
                                data.getAsString("course")   +','+ data.getAsString("graduated")+','+
                                data.getAsString("bmonth")   +','+ data.getAsString("bday")+','+
                                data.getAsString("byear")    +','+ data.getAsString("bplace")+','+
                                data.getAsString("street")   +','+ data.getAsString("municipality")+','+
                                data.getAsString("province") +','+ data.getAsString("telephone")+','+
                                data.getAsString("email")    +','+ data.getAsString("civilstatus")+','+
                                data.getAsString("religion") +','+ data.getAsString("college"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                }
                TextView pname = (TextView) getActivity().findViewById(R.id.tvpName);
                TextView pbdate = (TextView) getActivity().findViewById(R.id.tvpBdate);
                TextView pbplace = (TextView) getActivity().findViewById(R.id.tvpBPlace);
                TextView paddress = (TextView) getActivity().findViewById(R.id.tvpAddress);
                TextView ptelephone = (TextView) getActivity().findViewById(R.id.tvpTelephone);
                TextView preligion = (TextView) getActivity().findViewById(R.id.tvpReligion);
                TextView pcivil = (TextView) getActivity().findViewById(R.id.tvpCStatus);
                TextView pusername = (TextView) getActivity().findViewById(R.id.tvpUsername);
                TextView pcourse = (TextView) getActivity().findViewById(R.id.tvpCourse);
                TextView pcollege = (TextView) getActivity().findViewById(R.id.tvpCollege);
                TextView phasgrad = (TextView) getActivity().findViewById(R.id.tvpHGrad);
                TextView pemail = (TextView) getActivity().findViewById(R.id.tvpEmail);
                TextView ppass = (TextView) getActivity().findViewById(R.id.tvpPassword);


                fname =data.getAsString("fname");
                b = fname.split(" ");
                fname = "";
                for (String aB : b) {
                    fname += aB.substring(0, 1).toUpperCase() + aB.substring(1).toLowerCase();
                    fname += " ";
                }

                lname = data.getAsString("lname");
                b = lname.split(" ");
                lname = "";
                for (String aB : b) {
                    lname += aB.substring(0, 1).toUpperCase() + aB.substring(1).toLowerCase()+" ";
                }

                bplace = data.getAsString("bplace");
                b = bplace.split(" ");
                bplace = "";
                for (String aB : b) {
                    bplace += aB.substring(0, 1).toUpperCase() + aB.substring(1).toLowerCase()+" ";
                }

                cstatus = data.getAsString("civilstatus");
                b = cstatus.split(" ");
                cstatus = "";
                for (String aB : b) {
                    cstatus += aB.substring(0, 1).toUpperCase() + aB.substring(1).toLowerCase()+" ";
                }

                religion = data.getAsString("religion");
                b = religion.split(" ");
                religion = "";
                for (String aB : b) {
                    religion += aB.substring(0, 1).toUpperCase() + aB.substring(1).toLowerCase()+" ";
                }

                bmonth = data.getAsString("bmonth");
                data.put("bmonth",bmonth.substring(0, 1).toUpperCase() + bmonth.substring(1).toLowerCase()+" ");

                if(data.getAsString("graduated").equals("XXX")){
                    data.put("hasgrad","No");
                }else {
                    data.put("hasgrad","Yes");
                }

                data.put("name",fname +" "+ data.getAsString("initial") +" "+ lname);
                data.put("bdate",bmonth+" "+data.getAsString("bday")+", "+data.getAsString("byear"));
                data.put("bplace",bplace);
                data.put("address",data.getAsString("street")+", "+data.getAsString("municipality")+" "
                        +data.getAsString("province"));
                data.put("religion",religion);
                data.put("cstatus",cstatus);

                pname.setText(data.getAsString("name"));
                pbdate.setText(data.getAsString("bdate"));
                pbplace.setText(data.getAsString("bplace"));
                paddress.setText(data.getAsString("address"));

                ptelephone.setText(data.getAsString("telephone"));
                preligion.setText(data.getAsString("religion"));
                pcivil.setText(data.getAsString("cstatus"));
                pusername.setText(mParam1);
                pcourse.setText(data.getAsString("course"));
                pcollege.setText(data.getAsString("college"));
                phasgrad.setText(data.getAsString("hasgrad"));
                pemail.setText(data.getAsString("email"));
                ppass.setText("***"+mParam2+"***");
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "No record found!", Toast.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String data);
    }
}
