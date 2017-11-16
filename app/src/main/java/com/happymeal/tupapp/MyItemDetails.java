package com.happymeal.tupapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chabbal.slidingdotsplash.SlidingSplashView;
import com.kosalgeek.android.caching.FileCacher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MyItemDetails extends AppCompatActivity implements EditItemFragment.OnFragmentInteractionListener,MyItemFragment.OnFragmentInteractionListener{
   SlidingSplashView imageView;
    TextView tv_name, tv_seller, tv_price,tv_categ,tv_subcateg,tv_contact,tv_status,tv_desc;
    SlidingSplashView ts_img;
    Activity activity = this;
    ProgressDialog pd;
    String username;
    int[] a = {R.drawable.bgpic,R.drawable.buy_sell,R.drawable.tup};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_item_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.setTitle(getIntent().getStringExtra("name"));
        tv_seller  = (TextView) findViewById(R.id.seller);
        tv_price   = (TextView) findViewById(R.id.price);
        tv_categ   = (TextView) findViewById(R.id.categ);
        tv_subcateg= (TextView) findViewById(R.id.scateg);
        tv_contact = (TextView) findViewById(R.id.contact);
        tv_status  = (TextView) findViewById(R.id.stat);
        tv_desc    = (TextView) findViewById(R.id.desc);
        ts_img = (SlidingSplashView) findViewById(R.id.clicked_img);

        tv_seller.setText(getIntent().getStringExtra("seller"));
        tv_price.setText(getIntent().getStringExtra("price"));
        tv_categ.setText(getIntent().getStringExtra("category"));
        tv_subcateg.setText(getIntent().getStringExtra("subcategory"));
        tv_contact.setText(getIntent().getStringExtra("contact"));
        tv_status.setText(getIntent().getStringExtra("status"));
        tv_desc.setText(getIntent().getStringExtra("description"));
        ts_img.setImageResources(a);
        username = getIntent().getStringExtra("username");
        Button edit = (Button)findViewById(R.id.bEditItem);
        edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(isNetworkAvailable()) {
                    setContentView(R.layout.activity_main);
                    EditItemFragment editItemFragment = EditItemFragment.newInstance(username, getIntent().getStringExtra("id"));
                    FragmentManager manager = getSupportFragmentManager();
                    manager.beginTransaction().add(R.id.relativelayout_for_fragment,
                            editItemFragment,
                            editItemFragment.getTag()
                    ).commit();
                }else {
                    Toast.makeText(getApplicationContext(),"Lost internet connection!",Toast.LENGTH_LONG).show();
                }
            }
        });
        Button delete = (Button)findViewById(R.id.bDeleteItem);
        delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(isNetworkAvailable()) {
                    new delete_item().execute("https://tupapp.000webhostapp.com/index.php/Welcome/DeleteItem/" + getIntent().getStringExtra("id"));
                }else {
                    Toast.makeText(getApplicationContext(),"Lost internet connection!",Toast.LENGTH_LONG).show();
                }
            }
        });
        Button change = (Button)findViewById(R.id.bChangeItem);
        change.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(isNetworkAvailable()) {
                    new change_item().execute("https://tupapp.000webhostapp.com/index.php/Welcome/ChangeStatus/" + getIntent().getStringExtra("id"));
                }else {
                    Toast.makeText(getApplicationContext(),"Lost internet connection!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onFragmentInteraction(String data) {
        Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return  super.onOptionsItemSelected(item);
    }

    private class delete_item extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(activity);
            pd.setMessage("Deleting Item");
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
                    buffer.append(line).append("\n");
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

        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (pd.isShowing()) {
                pd.dismiss();
            }
//            FileCacher<String> profilecache = new FileCacher<>(getApplicationContext(),"profile.txt");
//            String string="";
//            String[] f2;
//
//            try {
//                string = profilecache.readCache();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            f2 = string.split(",");
            Toast.makeText(getApplicationContext(),"Item successfully deleted!",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /** Deletes an item owned by the user in the TUPAPP web server*/
    private class change_item extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(activity);
            pd.setMessage("Changing Item Status");
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
                    buffer.append(line).append("\n");
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

        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (pd.isShowing()) {
                pd.dismiss();
            }
            Toast.makeText(getApplicationContext(),"Item successfully sold!",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /** Checks for internet connection */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
