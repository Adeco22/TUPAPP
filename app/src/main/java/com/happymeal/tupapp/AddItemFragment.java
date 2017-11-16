package com.happymeal.tupapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.kosalgeek.android.caching.FileCacher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class AddItemFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ProgressDialog pd;
    Fragment fragment = this;
    static final int REQUEST_FROM_CAMERA = 1;
    static final int REQUEST_FROM_GALLERY = 0;
    LinearLayout layout;
    ImageView iv1;
    ImageView iv2;
    Integer ctr=0;
    ImageView x;
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AddItemFragment() {
        // Required empty public constructor
    }

    public static AddItemFragment newInstance(String param1, String param2) {
        AddItemFragment fragment = new AddItemFragment();
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
        final View view =  inflater.inflate(R.layout.fragment_add_item, container, false);

        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Add an Item");

        }catch (NullPointerException ex){
            ex.printStackTrace();
        }
        final Spinner sub_category = (Spinner)view.findViewById(R.id.sSubCateg);

        /** Action listener for dynamic subcategory*/
        Spinner category = (Spinner)view.findViewById(R.id.sCateg);
        iv1 = (ImageView)view.findViewById(R.id.ivCamera);
        iv2 = (ImageView)view.findViewById(R.id.ivGallery);
        layout = (LinearLayout)view.findViewById(R.id.LinearLayout1);

        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFromCamera(view);
            }
        });

        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFromGallery(view);
            }
        });

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String categ = parent.getSelectedItem().toString();
                categ = categ.replace(" ","_");
                String subcateg = getStringResourceByName(categ);
                ArrayAdapter<CharSequence> book_adapter = ArrayAdapter.createFromResource(getContext(),getArrayResourceByInt(subcateg),
                        android.R.layout.simple_spinner_item);
                book_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sub_category.setAdapter(book_adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button bSubmit = (Button)view.findViewById(R.id.bSubmit);
        Button bCancel = (Button)view.findViewById(R.id.bCancel);
        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner category = (Spinner) getActivity().findViewById(R.id.sCateg);
                Spinner subcateg = (Spinner) getActivity().findViewById(R.id.sSubCateg);
                EditText name = (EditText) getActivity().findViewById(R.id.etProdName);
                EditText contact = (EditText) getActivity().findViewById(R.id.etContact);
                EditText price = (EditText) getActivity().findViewById(R.id.etPrice);
                EditText description = (EditText) getActivity().findViewById(R.id.etDesc);
                String c = contact.getText().toString();

                if(name.getText().toString().equals("")|| contact.getText().toString().equals("")||
                        price.getText().toString().equals("")|| description.getText().toString().equals("")){

                    Toast.makeText(getContext(), "Please fill out all forms!", Toast.LENGTH_LONG).show();
                }else if(c.length()!=11 ){
                    Toast.makeText(getContext(), "Enter a valid contact number!", Toast.LENGTH_LONG).show();
                }else if(category.getSelectedItem().toString().equals("Please Select")||subcateg.getSelectedItem().toString().equals("Please Select")){
                    Toast.makeText(getContext(), "Please fill out all forms!", Toast.LENGTH_LONG).show();
                }else{
                    String cn = contact.getText().toString().substring(0,2);
                    if(!(cn.equals("09"))){
                        Toast.makeText(getContext(), "Enter a valid contact number!", Toast.LENGTH_LONG).show();
                    }else {
                        if (isNetworkAvailable()) {
                            FileCacher<String> profilecache = new FileCacher<>(getContext(),"profile.txt");
                            String string="",a,b;
                            String[] f2,arr;

                            try {
                                string = profilecache.readCache();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            f2 = string.split(",");
                            a = f2[2];
                            b = f2[3];
                            arr = a.split(" ");
                            a = "";
                            for (String anArr : arr) {
                                a += anArr.substring(0, 1).toUpperCase() + anArr.substring(1).toLowerCase()+" ";
                            }
                            arr = b.split(" ");
                            b = "";
                            for (String anArr : arr) {
                                b += anArr.substring(0, 1).toUpperCase() + anArr.substring(1).toLowerCase()+" ";
                            }
                            String ct = category.getSelectedItem().toString();
                            String sct = subcateg.getSelectedItem().toString();
                            String nm = name.getText().toString();
                            String co = contact.getText().toString();
                            String pr = price.getText().toString();
                            String dc = description.getText().toString();
                            String sl = a + b;
                            new add_item().execute("https://tupapp.000webhostapp.com/index.php/Welcome/AddItem/" + mParam1 + "/" + ct + "/" + sct + "/" + nm + "/" + pr + "/" + co + "/" + dc+ "/"+sl+"/selling");
                        } else {
                            Toast.makeText(getContext(), "Lost internet connection!", Toast.LENGTH_LONG).show();
                        }
                    }
                }


            }
        });
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(fragment).commit();
                try {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mParam2);

                }catch (NullPointerException ex){
                    ex.printStackTrace();
                }
            }
        });
        return view;
    }

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


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String data);
    }

    /** Adds an item to TUPAPP web server*/
    private class add_item extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Adding Item");
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

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }
            Toast.makeText(getContext(),"Item successfully added!",Toast.LENGTH_SHORT).show();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment).commit();
            try {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mParam2);

            }catch (NullPointerException ex){
                ex.printStackTrace();
            }
        }
    }

    /** Checks for internet connection */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /** Gets a string's Resource Output String in string.xml */
    private String getStringResourceByName(String aString) {
        String packageName = getActivity().getPackageName();
        int resId = getResources().getIdentifier(aString, "string", packageName);
        return getString(resId);
    }

    /** Gets a string's Resource Output Array in string.xml */
    private Integer getArrayResourceByInt(String aString) {
        String packageName = getActivity().getPackageName();
        return getResources().getIdentifier(aString, "array", packageName);
    }

    /** Berto's Image functionalities*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FROM_CAMERA) {
          try {
              Bundle extras = data.getExtras();
              Bitmap imageBitmap = (Bitmap) extras.get("data");
              x.setImageBitmap(imageBitmap);
              ctr++;
          }catch (NullPointerException e){
              e.printStackTrace();
          }
        }else if(requestCode == REQUEST_FROM_GALLERY){
            try {
                Uri selectedImage = data.getData();
                x.setImageURI(selectedImage);
                ctr++;
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    public void addFromCamera(View view){
        if(ctr<3){
            final ImageView image = new ImageView(getContext());
            image.setMaxHeight(70);
            image.setMaxWidth(70);
            image.setId(ctr);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteImage(image);
                }
            });
            layout.addView(image);

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                setX(image);
                startActivityForResult(takePictureIntent, REQUEST_FROM_CAMERA);
            }
        }else
            Toast.makeText(getContext(),"Maximum of 3 images!",Toast.LENGTH_SHORT).show();
    }

    public void addFromGallery(View view){
        if(ctr<3){
            final ImageView image = new ImageView(getContext());
            image.setMaxHeight(70);
            image.setMaxWidth(70);
            image.setId(ctr);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteImage(image);
                }
            });
            layout.addView(image);
            setX(image);
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto , REQUEST_FROM_GALLERY);
        }else
            Toast.makeText(getContext(),"Maximum of 3 images!",Toast.LENGTH_SHORT).show();
    }

    public void deleteImage(View img){
        ((ViewManager)img.getParent()).removeView(img);
//        lala.remove(a);
        ctr--;
    }

    public void setX(View v){
        x =(ImageView) v;
    }

}
