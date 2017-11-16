package com.happymeal.tupapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.chabbal.slidingdotsplash.SlidingSplashView;

public class ProductDetails extends AppCompatActivity {
   // ImageView imageView;
    TextView tv_name, tv_seller, tv_price,tv_categ,tv_subcateg,tv_contact,tv_status,tv_desc;
    SlidingSplashView ts_img;
    Activity activity = this;
    ProgressDialog pd;
    int[] a = {R.drawable.bgpic,R.drawable.buy_sell,R.drawable.tup};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //ts_img = (ImageView) findViewById(R.id.clicked_img);
        //ts_name = (TextView) findViewById(R.id.clicked_tsname);
        this.setTitle(" " +getIntent().getStringExtra("name"));
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
        //tv_status.setText(getIntent().getStringExtra("status"));
        tv_desc.setText(getIntent().getStringExtra("description"));
        ts_img.setImageResources(a);
        //ts_img.setImageResource(getIntent().getIntExtra("ts_img",00));
        //ts_name.setText("Name: "+getIntent().getStringExtra("clicked_name"));




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


    @Override
    public void onBackPressed() {
        finish();
    }

}
