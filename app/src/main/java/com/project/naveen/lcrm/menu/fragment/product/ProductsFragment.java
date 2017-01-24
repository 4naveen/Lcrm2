package com.project.naveen.lcrm.menu.fragment.product;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.naveen.lcrm.AppSession;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.RecyclerTouchListener;
import com.project.naveen.lcrm.adapters.ProductsAdapter;
import com.project.naveen.lcrm.addactivity.AddProductsActivity;
import com.project.naveen.lcrm.detailsactivity.DetailProductActivity;
import com.project.naveen.lcrm.models.Products;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsFragment extends Fragment {

    String token;
    ProductsAdapter productAdapter;
    MaterialSearchView  searchView;
    View v;
    public ProductsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        token= Appconfig.TOKEN;
        AppSession.productsArrayList=new ArrayList<>();
        setHasOptionsMenu(true);
        searchView=(MaterialSearchView)getActivity().findViewById(R.id.search_view);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Products");
        }

        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600) {
            v=inflater.inflate(R.layout.fragment_products, container, false);

        }
        else
        {
            v=inflater.inflate(R.layout.fragment_products_mob, container, false);

        }
//        View v=inflater.inflate(R.layout.fragment_products, container, false);
        AppSession.products_recyclerView=(RecyclerView)v.findViewById(R.id.rv);
        if (AppSession.products_write==0)
        {
            v.findViewById(R.id.fab).setVisibility(View.GONE);
        }
        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), AddProductsActivity.class);
                getActivity().startActivity(i);
            }
        });

           new GetAllProductsTask().execute(token);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_staff,menu);
        MenuItem menuItem=menu.findItem(R.id.action_search);
        searchView.setMenuItem(menuItem);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Products> subProductsArrayList=new ArrayList<>();
                Log.i("ontypecomplete", newText);
                Log.i("list data--", String.valueOf(  AppSession.productsArrayList.size()));
                for (int i=0;i<  AppSession.productsArrayList.size();i++)
                {
                    if (  AppSession.productsArrayList.get(i).getProduct_name().contains(newText))
                    {
                       Products products=new Products();
                        products.setId(  AppSession.productsArrayList.get(i).getId());
                        products.setProduct_name(AppSession.productsArrayList.get(i).getProduct_name());
                        products.setName(  AppSession.productsArrayList.get(i).getName());
                        products.setProduct_type(  AppSession.productsArrayList.get(i).getProduct_type());
                        products.setStatus(  AppSession.productsArrayList.get(i).getStatus());
                        products.setQuantity_on_hand(  AppSession.productsArrayList.get(i).getQuantity_on_hand());
                        products.setQuantity_available(  AppSession.productsArrayList.get(i).getQuantity_available());
                        subProductsArrayList.add(products);
                    }
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                }
                AppSession.products_recyclerView.setAdapter(new ProductsAdapter(getActivity(),subProductsArrayList));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    class GetAllProductsTask extends AsyncTask<String,Void,String>
    {   String response;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            URL url;
            HttpURLConnection connection;
            try {
                url = new URL(Appconfig.PRODUCTS_URL+params[0]);
                connection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String temp;
                while ((temp=br.readLine())!=null)
                {
                    buffer.append(temp);
                }
                response=buffer.toString();

                //  Log.i("response in d",response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // connection.disconnect();
            return response;
        }
        @Override
        protected void onPostExecute(String response) {
            dialog.dismiss();
            try {
                Log.i("response--",response);
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("products");
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
                    Log.i("productlist--",products.getProduct_name());
                }
                productAdapter=new ProductsAdapter(getActivity(),AppSession.productsArrayList);
                //AppSession.staffArrayListGlobal=staffArrayList;
                AppSession.products_recyclerView.setAdapter(productAdapter);
                AppSession.products_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);

                AppSession.products_recyclerView.setLayoutManager(lmanager);
                AppSession.products_recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),AppSession.products_recyclerView, new RecyclerTouchListener.ClickListener() {
                    @Override

                    public void onClick(View view, int position) {
                        Products products=AppSession.productsArrayList.get(position);
                        final int product_id=products.getId();
                        Intent i=new Intent(getActivity(),DetailProductActivity.class);
                        i.putExtra("product_id",String.valueOf(product_id));
                        i.putExtra("product_id_position",position);
                        Log.i("opp_position", String.valueOf(position));
                        getActivity().startActivity(i);
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }

}
