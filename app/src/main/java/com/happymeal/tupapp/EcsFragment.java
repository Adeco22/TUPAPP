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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amigold.fundapter.FunDapter;

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

public class EcsFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    Button bAdditem;
    View view;
    RecyclerView recyclerView;
    ProductAdapter adapter;
    ArrayList<Product> products = new ArrayList<>();
    ProgressDialog pd;

    private OnFragmentInteractionListener mListener;

    public EcsFragment() {
        // Required empty public constructor
    }

    public static EcsFragment newInstance(String param1 ) {
        EcsFragment fragment = new EcsFragment();
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
        new product_retrieve().execute("https://tupapp.000webhostapp.com/index.php/Welcome/getListView/");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ecs, container, false);

        //Initializes the Search button in the toolbar
        setHasOptionsMenu(true);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Buy and Sell");
        }catch (NullPointerException ex){
            ex.printStackTrace();
        }

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(0), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ProductAdapter(getContext(), products);
        recyclerView.setAdapter(adapter);

        bAdditem = (Button)view.findViewById(R.id.bAdditem);
        bAdditem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AddItemFragment addItemFragment = AddItemFragment.newInstance(mParam1,"Buy and Sell");
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().add(R.id.relativelayout_for_fragment,
                        addItemFragment,
                        addItemFragment.getTag()
                ).commit();
            }
        });
//
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

    /** Retrieves anyone's unsold products*/
    private class product_retrieve extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Retrieving sales");
            pd.setCancelable(false);
            pd.show();
            products = new ArrayList<>();
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
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... 🙂
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
            String id, nm, ds,ct, price, contact, name, description, category, subcateg,seller,status;
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

                        // Translating Json Object to Product
                        jsonObject = jArray.getJSONObject(i);
                        id = jsonObject.getString("id");
                        nm = jsonObject.getString("name");
                        name = nm.replace("%20"," ");
                        ds = jsonObject.getString("description");
                        description = ds.replace("%20"," ");
                        ct = jsonObject.getString("category");
                        category = ct.replace("%20"," ");
                        subcateg = jsonObject.getString("s-category");
                        price = jsonObject.getString("price");
                        contact = jsonObject.getString("contact");
                        seller = jsonObject.getString("seller");
                        status = jsonObject.getString("status");
                        prod = new Product(id,name, category, subcateg,contact,price,description,seller,status);
                        products.add(prod);
                    }

                    //Adding products to RecyclerView
                    recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(0), true));
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    adapter = new ProductAdapter(getContext(), products);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Cannot translate list, Runtime Error!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(),"Error fetching list, Server Problems!", Toast.LENGTH_SHORT).show();
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

    /**Search Functionalities in RecyclerView*/
    @Override
    public boolean onQueryTextChange(String newText) {
        TextView noresults;
        noresults = (TextView) getActivity().findViewById(R.id.noResultsFoundView);
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

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
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
}