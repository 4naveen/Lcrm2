package com.project.lorvent.lcrm.fragments.admin.feedback;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TechnicalFragment extends Fragment {
    EditText brand,version,description;
    TextInputLayout input_brand,input_version,input_screen,input_desc;
    BetterSpinner screen;
    Button submit,cancel;
    ArrayList<String> screenList;
        FrameLayout layout;
    public static final MediaType FORM_DATA_TYPE
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    public static final String BRAND_KEY = "entry.379383765";
    public static final String VERSION_KEY = "entry.364639317";
    public static final String SCREEN_KEY = "entry.1219751948";
    public static final String DETAILS_KEY = "entry.754221024";

    public TechnicalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_technical, container, false);
               setHasOptionsMenu(true);
        final ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);

        }

        screen=(BetterSpinner)v.findViewById(R.id.screen);
        version=(EditText)v.findViewById(R.id.version);
        description=(EditText)v.findViewById(R.id.description);
        brand = (EditText)v.findViewById(R.id.brand);
        input_brand = (TextInputLayout) v.findViewById(R.id.input_layout_brand);
        input_version = (TextInputLayout) v.findViewById(R.id.input_layout_version);
        input_screen = (TextInputLayout) v.findViewById(R.id.input_layout_screen);
        input_desc = (TextInputLayout) v.findViewById(R.id.input_layout_decription);

        submit = (Button) v.findViewById(R.id.submit);
        cancel = (Button) v.findViewById(R.id.back);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            submit.setBackgroundResource(R.drawable.ripple_btn);
        }
        screenList=new ArrayList<>();
        layout=(FrameLayout) getActivity().findViewById(R.id.frame);
        screenList.add("Dashboard");
        screenList.add("Opportunity");
        screenList.add("Leads");
        screenList.add("Quotation");
        screenList.add("Invoices");
        screenList.add("SalesTeam");
        screenList.add("");
        screenList.add("Exam");
        screenList.add("Education");
        ArrayAdapter<String> salesteamArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,screenList);
        salesteamArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        screen.setAdapter(salesteamArrayAdapter);

        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                       // Log.i("on back--","key pressesd");
                        actionBar.setDisplayHomeAsUpEnabled(true);
                        getFragmentManager().popBackStackImmediate();

                        return true;
                    }
                }
                return false;
            }
        });
        brand.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_brand.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        version.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_version.setError("");

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
                input_desc.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        screen.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_screen.setError("");
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           actionBar.setDisplayHomeAsUpEnabled(true);
           getFragmentManager().popBackStackImmediate();

       }
   });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(submit.getWindowToken(), 0);
                if (brand.getText().toString().isEmpty()||version.getText().toString().isEmpty()||description.getText().toString().isEmpty()||screen.getText().toString().isEmpty()) {
                   if (brand.getText().toString().isEmpty()){
                       input_brand.setError("Please enter your phone brand");

                   }
                    if (version.getText().toString().isEmpty()){
                        input_version.setError("Please enter version");


                    }
                    if (description.getText().toString().isEmpty()){
                        input_desc.setError("Please enter description");

                    }
                    if (screen.getText().toString().isEmpty())
                    {
                        input_screen.setError("Please select screen");

                    }
                    return;
                }

                new PostDataTask().execute(Appconfig.TECHNICAL_URL, brand.getText().toString(),version.getText().toString(),
                        screen.getText().toString(),description.getText().toString());

            }
        });
        return v;
    }
    private class PostDataTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... contactData) {
            Boolean result = true;
            String url = contactData[0];
            String brand = contactData[1];
            String version= contactData[2];
            String details= contactData[3];
            String screen= contactData[4];

            String postBody = "";

            try {
                //all values must be URL encoded to make sure that special characters like & | ",etc.
                //do not cause problems
                postBody = BRAND_KEY + "=" + URLEncoder.encode(brand, "UTF-8") +
                        "&" + VERSION_KEY + "=" + URLEncoder.encode(version, "UTF-8")+
                        "&" + DETAILS_KEY + "=" + URLEncoder.encode(details, "UTF-8")+
                        "&" + SCREEN_KEY + "=" + URLEncoder.encode(screen, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                result = false;
            }

            try {
                //Create OkHttpClient for sending request
                OkHttpClient client = new OkHttpClient();
                //Create the request body with the help of Media Type
                RequestBody body = RequestBody.create(FORM_DATA_TYPE, postBody);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                //Send the request
                Response response = client.newCall(request).execute();
            } catch (IOException exception) {
                result = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result)
            {
                brand.setText("");
                version.setText("");
                description.setText("");
                screen.setText("");
                description.clearFocus();
            }
            Toast.makeText(getActivity(), result ? "Message successfully sent!" : "There was some error in sending message. Please try again after some time.", Toast.LENGTH_LONG).show();

        }

    }

}
