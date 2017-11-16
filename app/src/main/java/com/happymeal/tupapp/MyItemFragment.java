package com.happymeal.tupapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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

public class MyItemFragment extends Fragment implements SearchView.OnQueryTextListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    ProgressDialog pd;
    private static final String ARG_PARAM1 = "param1";

    RecyclerView recyclerView;
    MyItemAdapter adapter;
    ArrayList<Product> products = new ArrayList<>();
    ProgressDialog pd;
    Toolbar toolbar;
    String[] ts_name, ts_location;
    View view;


    Button bAdditem;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private OnFragmentInteractionListener mListener;

    public MyItemFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MyItemFragment newInstance(String param1) {
        MyItemFragment fragment = new MyItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        new my_product_retrieve().execute("https://tupapp.000webhostapp.com/index.php/Welcome/getListViewMyProfile/"+mParam1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_item, container, false);
        setHasOptionsMenu(true);
        //button add item
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Items");

        }catch (NullPointerException ex){
            ex.printStackTrace();
        }


        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new MyItemFragment.GridSpacingItemDecoration(2, dpToPx(0), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MyItemAdapter(getContext(), products);
        //adapter = new ProductAdapter(tsList);
        recyclerView.setAdapter(adapter);
//        BindDictionary<Product> dictionary = new BindDictionary<>();
//        dictionary.addStringField(R.id.tvName, new StringExtractor<Product>() {
//            @Override
//            public String getStringValue(Product product, int position) {
//                return product.getItem();
//            }
//        });
//
//        dictionary.addStringField(R.id.tvContact, new StringExtractor<Product>() {
//            @Override
//            public String getStringValue(Product product, int position) {
//                return product.getContact();
//            }
//        });
//
//        dictionary.addStringField(R.id.tvPrice, new StringExtractor<Product>() {
//            @Override
//            public String getStringValue(Product product, int position) {
//                return " " + product.getPrice() + ".00";
//            }
//        });
//
//        adapter = new FunDapter<>(getContext(), products, R.layout.product_layout, dictionary);
//        ListView lvItem = (ListView)view.findViewById(R.id.lvItem);
//        lvItem.setAdapter(adapter);
//
//
//
//        //for context menu
//        registerForContextMenu(lvItem);
        bAdditem = (Button)view.findViewById(R.id.bAdditem);
        bAdditem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(isNetworkAvailable()) {
                    AddItemFragment addItemFragment = AddItemFragment.newInstance(mParam1, "My Items");
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    manager.beginTransaction().add(R.id.relativelayout_for_fragment,
                            addItemFragment,
                            addItemFragment.getTag()
                    ).commit();
                }
            }
        });


//        Button bBack1 = (Button) view.findViewById(R.id.bBack1);
//        bBack1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(isNetworkAvailable()) {
//                    EcsFragment ecsFragment = EcsFragment.newInstance(mParam1);
//                    FragmentManager manager = getActivity().getSupportFragmentManager();
//                    manager.beginTransaction().replace(R.id.relativelayout_for_fragment,
//                            ecsFragment,
//                            ecsFragment.getTag()
//                    ).commit();
//                }else{
//                    Toast.makeText(getContext(),"Lost internet connection!",Toast.LENGTH_LONG).show();
//                }
//
//            }
//        });
        return view;
    }


        @Override
        public void onCreateContextMenu (ContextMenu menu, View v, ContextMenuInfo menuInfo) {
            super.onCreateContextMenu(menu, v, menuInfo);
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.main_context_menu,menu);
        }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String id = products.get(info.position).getId();//robert
        switch (item.getItemId())
        {
            case R.id.delete_id:
                if(isNetworkAvailable()) {
                    new delete_item().execute("https://tupapp.000webhostapp.com/index.php/Welcome/DeleteItem/" + id);
                }else {
                    Toast.makeText(getContext(),"Lost internet connection!",Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.edit_id:
            if(isNetworkAvailable()) {
                EditItemFragment editItemFragment = EditItemFragment.newInstance(mParam1, id);
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().add(R.id.relativelayout_for_fragment,
                        editItemFragment,
                        editItemFragment.getTag()
                ).commit();
            }else {
                Toast.makeText(getContext(),"Lost internet connection!",Toast.LENGTH_LONG).show();
            }
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return super.onContextItemSelected(item);
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String data);
    }

    /** Retrieves items the student currently sells*/
    private class my_product_retrieve extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Retrieving your sales");
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
            String id, nm, ds, ct, sct, price, contact, name, description, category, subcateg,seller,sl,status;
            JSONArray jArray;
            JSONObject jsonObject,jObject;
            Product prod;
            if (pd.isShowing()) {
                pd.dismiss();
            }
            try {
                jObject = new JSONObject(result);
                jArray = jObject.getJSONArray("query");
                try {
                    for (int i = 0; i < jArray.length(); i++) {
                        /** Translating Json Object to schedule*/
                        jsonObject = jArray.getJSONObject(i);
                        id = jsonObject.getString("id");
                        nm = jsonObject.getString("name");
                        name = nm.replace("%20"," ");
                        ds = jsonObject.getString("description");
                        description = ds.replace("%20"," ");
                        ct = jsonObject.getString("category");
                        category = ct.replace("%20"," ");
                        sct = jsonObject.getString("s-category");
                        subcateg = sct.replace("%20"," ");
                        price = " "+jsonObject.getString("price");
                        contact = jsonObject.getString("contact");
                        sl = jsonObject.getString("seller");
                        seller = sl.replace("%20"," ");
                        status = jsonObject.getString("status");
                        //String sp[] = jsonObject.getString("sched").split("/"); //reserve for sched
                        prod = new Product(id,name, category, subcateg,contact,price,description,seller,status);
                        products.add(prod);
                    }
                    recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.addItemDecoration(new MyItemFragment.GridSpacingItemDecoration(2, dpToPx(0), true));
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    adapter = new MyItemAdapter(getContext(), products);
                    //adapter = new ProductAdapter(tsList);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Cannot fetch list", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Cannot fetch list", Toast.LENGTH_SHORT).show();
            }catch (NullPointerException ex){
                ex.printStackTrace();
                Toast.makeText(getContext(), "Cannot fetch list", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** Deletes an item owned by the user in the TUPAPP web server*/
    private class delete_item extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
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
            Toast.makeText(getContext(),"Item successfully deleted!",Toast.LENGTH_SHORT).show();
            MyItemFragment myItemFragment = MyItemFragment.newInstance(mParam1);
            FragmentManager manager = getActivity().getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayout_for_fragment,
                    myItemFragment,
                    myItemFragment.getTag()
            ).commit();
        }
    }

    /** Checks for internet connection */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public Bitmap getImagefromId(int id){
        String packageName = getActivity().getPackageName();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),  id);
        int height = (bitmap.getHeight() * 512 / bitmap.getWidth());
        return Bitmap.createScaledBitmap(bitmap, 512, height, true);
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_items, menu);
        super.onCreateOptionsMenu(menu,inflater);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        TextView noresults = (TextView) getActivity().findViewById(R.id.noResultsFoundView);
        newText = newText.toLowerCase();
        ArrayList<Product> newList = new ArrayList<>();
        if(newText.length()>3) {
            for (Product pr : products) {
                String name = pr.getItem().toLowerCase();
                String category = pr.getCategory().toLowerCase();
                if (name.contains(newText)) {
                    noresults.setVisibility(View.GONE);
                    newList.add(pr);
                } else {
                    noresults.setVisibility(View.VISIBLE);
                }
            }
            adapter.setFilter(newList);

        }else {
        adapter.setFilter(products);
        noresults.setVisibility(View.GONE);
    }
        return true;
    }
}


