package com.project.lorvent.lcrm.activities.add;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.Connection;
import com.project.lorvent.lcrm.utils.MyVolleySingleton;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.adapters.ProductsAdapter;
import com.project.lorvent.lcrm.models.Category;
import com.project.lorvent.lcrm.models.Products;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class AddProductsActivity extends AppCompatActivity {
    ArrayList<Products> productsArrayList;
    ArrayList<String>categoryNameList,statusList,product_typeList;
    ArrayList<Category>categoryList;
    String token;
    BetterSpinner category,status,product_type;
    Button submit;
    EditText product_name,sales_price,description,qHand,qAvailable;
    LinearLayout linearLayout;
    TextInputLayout input_product_name,input_category,input_status,input_qhand,input_qAvailable,input_product_type,input_sales_price,input_description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Add Product");
        }
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        linearLayout=(LinearLayout)findViewById(R.id.layout);

        input_product_name=(TextInputLayout)findViewById(R.id.input_layout_product_name);
        input_category=(TextInputLayout)findViewById(R.id.input_layout_category);
        input_status=(TextInputLayout)findViewById(R.id.input_layout_status);
        input_qhand=(TextInputLayout)findViewById(R.id.input_layout_qHand);
        input_qAvailable=(TextInputLayout)findViewById(R.id.input_layout_qAvailable);
        input_product_type=(TextInputLayout)findViewById(R.id.input_layout_product_type);
        input_sales_price=(TextInputLayout)findViewById(R.id.input_layout_sales_price);
        input_description=(TextInputLayout)findViewById(R.id.input_layout_description);

        token= Appconfig.TOKEN;
        categoryList=new ArrayList<>();
        categoryNameList=new ArrayList<>();
        product_typeList=new ArrayList<>();
        statusList=new ArrayList<>();
        statusList.add("In Development");
        statusList.add("Normal");
        statusList.add("End of Lifecycle");
        statusList.add("Obsolete");
        product_typeList.add("Stockable Product");
        product_typeList.add("Consumable");
        product_typeList.add("Service");
        getCategoryList(token);
        category=(BetterSpinner)findViewById(R.id.category);
        status=(BetterSpinner)findViewById(R.id.status);
        product_type=(BetterSpinner)findViewById(R.id.product_type);
        sales_price=(EditText)findViewById(R.id.sales_price);
        product_name=(EditText)findViewById(R.id.products_name);
        product_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(product_name.getWindowToken(), 0);
            }
        });

        description=(EditText)findViewById(R.id.description);
        qHand=(EditText)findViewById(R.id.qHand);
        qAvailable=(EditText)findViewById(R.id.qAvailable);
        submit=(Button)findViewById(R.id.submit);

        ArrayAdapter<String> categoryArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,categoryNameList);
        categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        category.setAdapter(categoryArrayAdapter);
        ArrayAdapter<String> statusArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,statusList);
        statusArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        status.setAdapter(statusArrayAdapter);
        ArrayAdapter<String> product_typeArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,product_typeList);
        product_typeArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        product_type.setAdapter(product_typeArrayAdapter);

        product_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_product_name.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        sales_price.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_sales_price.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_description.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        qHand.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_qhand.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        qAvailable.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_qAvailable.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_category.setError("");
            }
        });
        status.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_status.setError("");
            }
        });
        product_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_product_type.setError("");
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     if (product_name.getText().toString().isEmpty())
                     {
                         input_product_name.setError("Please enter product name");

                         return;
                     }
                     else if (category.getText().toString().isEmpty()){
                         input_category.setError("Please select category");
                         return;
                     }
                     else if (status.getText().toString().isEmpty()){
                         input_status.setError("Please select status");
                         return;
                     }
                     else if (qHand.getText().toString().isEmpty()){
                         input_qhand.setError("Please enter quantity In Hand");
                         return;
                     }
                     else if (qAvailable.getText().toString().isEmpty()){
                         input_qAvailable.setError("Please select quantity available");
                         return;
                     }
                     else if (product_type.getText().toString().isEmpty()){
                         input_product_type.setError("Please select product type");
                         return;
                     }
                     else if (sales_price.getText().toString().isEmpty()){
                         input_sales_price.setError("Please enter sales price");
                         return;
                     }
                     else if (description.getText().toString().isEmpty()){
                         input_description.setError("Please enter description");
                         return;
                     }

                     else {
                         //do nothing
                     }
                     int categoryId=categoryList.get(categoryNameList.indexOf(category.getText().toString())).getId();

                     new AddProductTask().execute(token,product_name.getText().toString(),sales_price.getText().toString(),
                             description.getText().toString(),qHand.getText().toString(),qAvailable.getText().toString(),
                             product_type.getText().toString(),String.valueOf(categoryId),status.getText().toString());


                     InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                     imm.hideSoftInputFromWindow(submit.getWindowToken(), 0);
                 }
             });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    public void getCategoryList(String token)
    {
        SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url = text_url + "/user/categories?token=";
        } else {
            get_url = Appconfig.CATEGORY_URL;
        }


        StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+token,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {

                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("categories");
                        for (int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject object=jsonArray.getJSONObject(i);
                            Category category=new Category();
                            category.setId(object.getInt("id"));
                            category.setName(object.getString("name"));
                            categoryNameList.add(object.getString("name"));
                            categoryList.add(category);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);

                    }
                }
            },new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    }){
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();

            return params;
        }

    } ;
        MyVolleySingleton.getInstance(AddProductsActivity.this).getRequestQueue().add(stringRequest);

    }

    class AddProductTask extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AddProductsActivity.this);
            dialog.setMessage("Loading, please wait...");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String response="",jsonresponse="";
            JSONObject json;
            BufferedReader bufferedReader;
            String tok=params[0];
            String product_name=params[1];
            String sales_price=params[2];
            String description=params[3];
            String qHand=params[4];
            String qAvailable=params[5];
            String product_type=params[6];
            String category_id=params[7];
            String status=params[8];
            URL url;
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("product_name",product_name);
                jsonObject.put("sale_price",sales_price);
                jsonObject.put("description" ,description);
                jsonObject.put("quantity_on_hand",qHand);
                jsonObject.put("quantity_available",qAvailable);
                jsonObject.put("product_type",product_type);
                jsonObject.put("status",status);
                jsonObject.put("category_id",category_id);
                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String post_product_url;
                if (text_url != null) {
                    post_product_url = text_url + "/user/post_product?token=";
                } else {
                    post_product_url = Appconfig.PRODUCTS_ADD_URL;
                }

                url = new URL(post_product_url+tok);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();
                int responseCode=conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    json = new JSONObject(response);
                    //Get Values from JSONobject
                    //System.out.println("success=" + json.get("success"));

                    jsonresponse = json.getString("success");

                }
                else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                    }
                    json = new JSONObject(response);
                    jsonresponse=json.getString("error");
                    //System.out.println("error=" + json.get("error"));
                    //succes = json.getString("success");
                }

            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);

            }
            return jsonresponse;
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            if (result.equals("success"))
            {
                new GetAllProductsTask().execute(token);
                Connection.getDashboard(Appconfig.TOKEN,AddProductsActivity.this);

 /*               product_name.setText("");
                sales_price.setText("");
                description.setText("");
                qHand.setText("");
                qAvailable.setText("");*/
                final Snackbar snackbar = Snackbar.make(linearLayout, "Added 1 item Succesfully!", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },3000);
            }
            else {
                final Snackbar snackbar = Snackbar.make(linearLayout, "Item not added! Try Again", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
            }


        }
    }

    class GetAllProductsTask extends AsyncTask<String,Void,String>
    {   String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            URL url;
            HttpURLConnection connection;
            try {
                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String get_url;
                if (text_url != null) {
                    get_url = text_url + "/user/products?token=";
                } else {
                    get_url = Appconfig.PRODUCTS_URL;
                }
                url = new URL(get_url+params[0]);
                connection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String temp;
                while ((temp=br.readLine())!=null)
                {
                    buffer.append(temp);
                }
                response=buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
                Crashlytics.logException(e);

            }
            // connection.disconnect();
            return response;
        }
        @Override
        protected void onPostExecute(String response) {
            try {
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("products");
                AppSession.productsArrayList.clear();
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    Products products =new Products();
                    products.setId(object.getInt("id"));
                    products.setProduct_name(object.getString("product_name"));
                    products.setName(object.getString("name"));
                    products.setProduct_type(object.getString("product_type"));
                    products.setStatus(object.getString("status"));
                    products.setQuantity_on_hand(object.getString("quantity_on_hand"));
                    products.setQuantity_available(object.getString("quantity_available"));
                    AppSession.productsArrayList.add(products);
                }

                ProductsAdapter productAdapter=new ProductsAdapter(AddProductsActivity.this,AppSession.productsArrayList);
                //AppSession.staffArrayListGlobal=staffArrayList;
                AppSession.products_recyclerView.setAdapter(productAdapter);
                AppSession.products_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(AddProductsActivity.this, LinearLayoutManager.VERTICAL,false);
                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);

                AppSession.products_recyclerView.setLayoutManager(lmanager);

            } catch (JSONException e) {
                e.printStackTrace();
                Crashlytics.logException(e);

            }


        }

    }
}
