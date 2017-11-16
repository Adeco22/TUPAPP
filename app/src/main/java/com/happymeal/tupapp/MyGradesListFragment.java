package com.happymeal.tupapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;
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

public class MyGradesListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FunDapter adapter;
    ProgressDialog pd;
    ArrayList<Semester> semesters = new ArrayList<>();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MyGradesListFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MyGradesListFragment newInstance(String param1, String param2) {
        MyGradesListFragment fragment = new MyGradesListFragment();
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
        new grades_retrieve_online().execute("http://tup.edu.ph/android/my_sems/13-027-047/12345");
        View view = inflater.inflate(R.layout.fragment_my_grades_list, container, false);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) view.findViewById(R.id.toolbar);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Grades");

        }catch (NullPointerException ex){
            ex.printStackTrace();
        }
        BindDictionary<Semester> dictionary = new BindDictionary<>();
        dictionary.addStringField(R.id.tvsem, new StringExtractor<Semester>() {
            @Override
            public String getStringValue(Semester semester, int position) {
                return semester.getSemester();
            }
        });

        dictionary.addStringField(R.id.tvsy, new StringExtractor<Semester>() {
            @Override
            public String getStringValue(Semester semester, int position) {
                return semester.getSchoolyear();
            }
        });
        adapter = new FunDapter(MyGradesListFragment.this.getActivity(), semesters, R.layout.gradelist_layout, dictionary);

        ListView lvgrlist = (ListView)view.findViewById(R.id.lvGradesList);
        lvgrlist.setAdapter(adapter);

        lvgrlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isNetworkAvailable()) {
                    Semester v = semesters.get(position);
                    String sm = v.getSemester().replace(" ","%20");
                    String sy = v.getSchoolyear();

                    MyGradesFragment myGradesFragment = MyGradesFragment.newInstance(mParam1, mParam2, sm, sy);
                    FragmentManager manager = getActivity().getSupportFragmentManager();

                    manager.beginTransaction().add(R.id.relativelayout_for_fragment,
                            myGradesFragment,
                            myGradesFragment.getTag()
                    ).commit();

                }
                else {
                    Toast.makeText(getContext(),"Lost internet connection!",Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    private class grades_retrieve_online extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Getting grades list");
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
            String semester, schyr;
            JSONObject jObject, jsonObject;
            Semester sem;
            try {

                jObject = new JSONObject(result);
                JSONArray student = jObject.getJSONArray("student");
                try {
                    for (int i = 0; i < student.length(); i++) {
                        /** Translating Json Object to schedule*/
                        jsonObject = student.getJSONObject(i);
                        semester = jsonObject.getString("sem");
                        schyr    = jsonObject.getString("schoolyr");
                       /* TextView troll = (TextView) getActivity().findViewById(R.id.tvCtitle);
                        troll.setMa*/
                        sem = new Semester(semester,schyr);
                        semesters.add(sem);
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /** Checks for internet connection */
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
