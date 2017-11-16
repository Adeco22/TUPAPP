package com.happymeal.tupapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.*;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import com.kosalgeek.android.caching.FileCacher;

public class Login extends AppCompatActivity {

    String username, password, endsem;
    ArrayList<String> events = new ArrayList<>();
    Integer schoolyear, stmonth;
    Activity activity = this;
    ProgressDialog pd;
    Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent in = new Intent(getApplicationContext(),Splash.class);
        startActivity(in);
        setContentView(R.layout.log_in_page);

        InputFilter filter = new InputFilter() {
            EditText pass = (EditText)findViewById(R.id.studPass);
            boolean canEnterSpace = false;
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                if(pass.getText().toString().equals(""))
                {
                    canEnterSpace = false;
                }
                StringBuilder builder = new StringBuilder();

                for (int i = start; i < end; i++) {
                    char currentChar = source.charAt(i);

                    if (Character.isLetterOrDigit(currentChar) || currentChar == '_') {
                        builder.append(currentChar);
                        canEnterSpace = true;
                    }

                    if(Character.isWhitespace(currentChar) && canEnterSpace) {
                        builder.append(currentChar);
                    }
                }
                return builder.toString();
            }
        };

        EditText pass = (EditText) findViewById(R.id.studPass);
        pass.setFilters(new InputFilter[]{filter});


        // Else initializes the login screen
        if(ReLogin()) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_CALENDAR,Manifest.permission.READ_CALENDAR}, 1);
            }
            Button Loginbtn = (Button) findViewById(R.id.bLogIn);
            pass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    EditText pass = (EditText) findViewById(R.id.studPass);
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        Login_action(pass);
                    }
                    return false;
                }
            });
            Loginbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText pass = (EditText) findViewById(R.id.studPass);
                    Login_action(pass);
                }
            });
        }
    }

    /** Action where the user logs in */
    private void Login_action(EditText pass){
        final EditText user = (EditText) findViewById(R.id.studID);
        username = user.getText().toString();
        password = pass.getText().toString();
        if(username.equals("") && password.equals("")){
            showToast("Please input username and password",false);
        }
        else if(username.equals("")){
            showToast("Please input username",false);
        }
        else if(password.equals("")){
            showToast("Please input password",false);
        }
        else {
            if (isNetworkAvailable()) {
                new check_credentials().execute("http://tup.edu.ph/android/erslogin/" + username + "/" + password);
            } else {
                showToast("No internet connection!",false);
            }
        }
    }

    private boolean ReLogin(){
        SharedPreferences credentials = getSharedPreferences("credentials",MODE_PRIVATE);
        SharedPreferences profile = getSharedPreferences("profile",MODE_PRIVATE);
        FileCacher<String> profilecache = new FileCacher<>(getApplicationContext(),"profile.txt");
        // Checks if there is a profile logged in
        if (profilecache.hasCache()) {
            String[] b;
            String a="";
            try {
                a = profilecache.readCache();
            } catch (IOException e) {
                e.printStackTrace();
            }
            b = a.split(",");
            String u = b[0];
            String p = b[1];
            String name = b[2] + " " + b[3] + "." + " " + b[4];
            String course = b[5];


            Intent intent = new Intent(getApplicationContext(), Main.class);
            intent.putExtra("username", u);
            intent.putExtra("password", p);
            intent.putExtra("name", name);
            intent.putExtra("course", course);

            startActivity(intent);
            finish();
        }else {
            return true;
        }
        return false;
    }

    /** Checks for internet connection */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /** Validates credentials from TUP server and logs in if successful*/
    private class check_credentials extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(Login.this);
            pd.setMessage("Signing you in");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
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
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                   // Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
                }
                return buffer.toString();

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
        protected void onPostExecute(String result)
        {
            try
            {
                super.onPostExecute(result);
                JSONObject jObject;
                String obj;

                jObject = new JSONObject(result);
                obj = jObject.getString("success");
                if (obj.equals("1"))
                {
                    new profile_retrieve_online().execute("http://tup.edu.ph/android/get_studprofile/" + username + "/" + password);

                    if(ActivityCompat.checkSelfPermission(activity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED)
                    {
                        //new get_schedule().execute("https://tupapp.000webhostapp.com/index.php/Welcome/getschedule");

                        if (ActivityCompat.checkSelfPermission(activity,
                                Manifest.permission.WRITE_CALENDAR)
                                == PackageManager.PERMISSION_GRANTED)
                        {
                            //semcheck();
                            //new schedule_retrieve().execute("https://tupapp.000webhostapp.com/index.php/Welcome/getschedule");
            //new schedule_retrieve().execute("http://tup.edu.ph/android/mygrades/" + username + "/" + password + "/2nd%20Semester:"+ schoolyear +"-"+ (schoolyear+1));
                        }
                    }
                }
                else
                {
                    showToast("Invalid Username or Password",false);
                    if (pd.isShowing())
                    {
                        pd.dismiss();
                    }
                }
            }catch (JSONException e)
            {
                if (pd.isShowing())
                {
                    pd.dismiss();
                }
                e.printStackTrace();
                showToast("Can't access request, try again later",false);
            }
        }
    }

    /** Adds a course event in Google Calendar*/
    private Integer addevent(String code, String title, String room, String day, Integer start, Integer end) {
        try {
            Calendar dstart = Calendar.getInstance();
            dstart.set(schoolyear, stmonth, 15, start, 0);
            int d;

            switch (day){
                case "MO":   d = Calendar.MONDAY; break;
                case "TU":   d = Calendar.TUESDAY; break;
                case "WE":   d = Calendar.WEDNESDAY; break;
                case "TH":  d = Calendar.THURSDAY; break;
                case "FR":   d = Calendar.FRIDAY; break;
                case "SA": d = Calendar.SATURDAY; break;
                case "SU": d = Calendar.SUNDAY; break;
                default: return 0;
            }
            // Algorithm that tells the program i.e. what next Monday is from the 15th
            while (dstart.get(Calendar.DAY_OF_WEEK) != d) {
                dstart.add(Calendar.DATE, 1);
            }

            Uri EVENTS_URI = Uri.parse(getCalendarUriBase(true) + "events");
            ContentResolver cr = getContentResolver();
            TimeZone timeZone = TimeZone.getDefault();

            // Inserting an event in calendar.
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.CALENDAR_ID, 1);
            values.put(CalendarContract.Events.TITLE, code);
            values.put(CalendarContract.Events.DESCRIPTION, title);
            values.put(CalendarContract.Events.EVENT_LOCATION, room);
            values.put(CalendarContract.Events.ALL_DAY, 0);
            values.put(CalendarContract.Events.DTSTART, dstart.getTimeInMillis());
            values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;WKST=SU;BYDAY=" + day + ";UNTIL=" + endsem);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
            values.put(CalendarContract.Events.HAS_ALARM, 1);

            //Algorithm that tells the program if a schedule is in AM or PM
            if (end < start) {
                end += 12;
            } else if (end < 7 && start < 7 && start < end) {
                end += 12;
                start += 12;
            }
            values.put(CalendarContract.Events.DURATION, "+P" + (end - start) + "H");
            Uri event = cr.insert(EVENTS_URI, values);

            // Adds a reminder after event is added
            Uri REMINDERS_URI = Uri.parse(getCalendarUriBase(true) + "reminders");
            values = new ContentValues();
            assert event != null;
            String i = event.getLastPathSegment();
            events.add(i);
            values.put(CalendarContract.Reminders.EVENT_ID,i );
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            values.put(CalendarContract.Reminders.MINUTES, 10);
            cr.insert(REMINDERS_URI, values);
            return 0;
        }catch (SQLiteException e){
            return 1;
        }
    }

    /**Returns Calendar Base URI, supports both new and old OS. */
    private String getCalendarUriBase(boolean eventUri) {
        Uri calendarURI = null;
        try {
            calendarURI = (eventUri) ? Uri.parse("content://com.android.calendar/") : Uri.parse("content://com.android.calendar/calendars");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert calendarURI != null;
        return calendarURI.toString();
    }

    /** Retrieves student's alarm schedule and writes to events.txt(fixed for now) */
    private class schedule_retrieve extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
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
                String line;

                // txtJson.setText();
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                    //Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
                }
                return buffer.toString();

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

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String ccode,ctitle,room,day,time;
            String [] days, timer, timest, rooms;
            Integer start, end,ctr,chk=0;
            JSONArray jArray;
            JSONObject jsonObject,jObject;
            SharedPreferences events = getSharedPreferences("events",MODE_PRIVATE);
            try {
                jObject = new JSONObject(result);
                jArray = jObject.getJSONArray("query");
                try {
                    for (int i = 0; i < jArray.length(); i++) {

                        // Translating Json Object to Schedule
                        jsonObject = jArray.getJSONObject(i);
                        ccode = jsonObject.getString("course_code");
                        ctitle = jsonObject.getString("course_title");
                        room = jsonObject.getString("course_room");
                        day = jsonObject.getString("course_day");
                        time = jsonObject.getString("course_time");
                        rooms = room.split("/");
                        days = day.split("/");
                        timer = time.split("/");
                        for(int a = 0 ; a < days.length ; a++) {
                            ctr = a-1;
                            timest = timer[a].split("-");
                            start = Integer.parseInt(timest[0]);
                            end = Integer.parseInt(timest[1]);
                            try {
                                chk += addevent(ccode, ctitle, rooms[a].toUpperCase(), getday(days[a]), start, end);
                            }
                            catch(ArrayIndexOutOfBoundsException ex){
                                chk += addevent(ccode, ctitle, rooms[ctr].toUpperCase(), getday(days[a]), start, end);
                            }
                        }
                    }
                    if(chk==0)
                    {
                        showToast("Alarm schedule successfully created!",false);
                    }
                    else {
                        showToast("Could not fully create alarm schdule",false);
                    }
                    String in = events.toString().substring(1,(events.toString().length()-1));
                    events.edit().putString("events",in).apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("Could not retrieve data",false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showToast("Could not retrieve data",false);
            }
        }
    }

    /** Checks the current date if it is 1st or 2nd sem then gives feedback to schedule_retrieve() and addevent() */
    private void semcheck() {
        Date current = Calendar.getInstance().getTime();
        DateFormat mo = new SimpleDateFormat("MM", Locale.getDefault());
        DateFormat yr = new SimpleDateFormat("yyyy",Locale.getDefault());
        Integer year = Integer.parseInt(yr.format(current));
        Integer month = Integer.parseInt(mo.format(current));
        // 1st Semester
        if(5<month&&month<11){
            schoolyear = year;
            endsem = (year+1)+"1015";
            stmonth = Calendar.JUNE;
        }
        // 2nd Semester
        else if(10<month||month<4){
            schoolyear =(year-1);
            stmonth = Calendar.NOVEMBER;
            endsem = year+"0315";
        }
        // Summer
        else{
            schoolyear = year;
            endsem = year+"0515";
            stmonth = Calendar.APRIL;
        }
    }

    /** Gets a TUP database day string and converts it to CalendarContract format */
    private String getday(String day){
        switch (day){
            case "M": return "MO";
            case "T": return "TU";
            case "W": return "WE";
            case "TH": return "TH";
            case "F": return "FR";
            case "Sat": return "SA";
            case "Sun": return "SU";

            case "Monday": return "MO";
            case "Tuesday": return "TU";
            case "Wednesday": return "WE";
            case "Thursday": return "TH";
            case "Friday": return "FR";
            case "Saturday": return "SA";
            case "Sunday": return "SU";
            default: return "";
        }
    }

    /** Retrieves student's schedule and writes to file schedule.txt */
    private class get_schedule extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
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
                String line;

                // txtJson.setText();
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
                }
                return buffer.toString();

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

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            SharedPreferences schedule = getSharedPreferences("schedule",MODE_PRIVATE);
            schedule.edit().putString("schedule",result).apply();
        }
    }

    /** Retrieves student's profile and writes to file profile.txt */
    private class profile_retrieve_online extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
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
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
                }
                return buffer.toString();

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
            SharedPreferences profile = getSharedPreferences("profile",MODE_PRIVATE);
            String name,fname, initial, lname,course;
            if (pd.isShowing())
            {
                pd.dismiss();
            }
            try
            {
                jObject = new JSONObject(result);
                JSONArray student = jObject.getJSONArray("student");

                JSONObject st = student.getJSONObject(0);
                profile.edit().putString("profile",st.toString()).apply();
                fname = st.getString("fname");
                initial = st.getString("initial");
                lname = st.getString("lname");
                name = fname +" "+ initial + "." + " " + lname;
                course = st.getString("course");

                if(ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED)
                {
                    profile.edit().putString("fname", fname).apply();
                    profile.edit().putString("initial", initial).apply();
                    profile.edit().putString("lname", lname).apply();
                    profile.edit().putString("course", course).apply();
                }
                Intent intent = new Intent(getApplicationContext(), Main.class);
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                intent.putExtra("name", name);
                intent.putExtra("course", course);
                startActivity(intent);
                finish();
            } catch (JSONException e)
            {
                if (pd.isShowing())
                {
                    pd.dismiss();
                }
                e.printStackTrace();
                showToast("No record found!",false);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showToast("Offline Access Granted!",false);

                } else {
                    showToast("Offline Access Not Granted!",false);
                }
            }
        }
    }

    private void showToast(String message, Boolean isLong)
    {
        try{if(toast.getView().isShown()){toast.cancel();} }catch (NullPointerException e){toast = new Toast(getApplicationContext());}
        if(isLong){ toast = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG);}
        else { toast = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT);}
        toast.show();
    }
}
