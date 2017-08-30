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
public class ContentFragment extends Fragment {

    EditText topic,error,anything;
    TextInputLayout input_topic,input_error,input_anything,input_category;
    BetterSpinner category;
    Button submit,cancel;
    ArrayList<String> categoryList;
    FrameLayout layout;
    public static final MediaType FORM_DATA_TYPE
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    public static final String CATEGORY_KEY = "entry.1600802401";
    public static final String TOPIC_KEY = "entry.170810390";
    public static final String ERROR_KEY = "entry.2017144790";
    public static final String ANYTHING_KEY = "entry.780560581";

    public ContentFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_content, container, false);
        setHasOptionsMenu(true);
        final ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);

        }

        category=(BetterSpinner)v.findViewById(R.id.screen);
        topic = (EditText)v.findViewById(R.id.brand);
        error = (EditText)v.findViewById(R.id.version);
        anything = (EditText)v.findViewById(R.id.description);
        submit = (Button) v.findViewById(R.id.submit);
        cancel = (Button) v.findViewById(R.id.back);
        input_topic= (TextInputLayout) v.findViewById(R.id.input_layout_brand);
        input_error = (TextInputLayout) v.findViewById(R.id.input_layout_version);
        input_category = (TextInputLayout) v.findViewById(R.id.input_layout_screen);
        input_anything = (TextInputLayout) v.findViewById(R.id.input_layout_decription);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            submit.setBackgroundResource(R.drawable.ripple_btn);
        }
        categoryList=new ArrayList<>();
        layout=(FrameLayout) getActivity().findViewById(R.id.frame);
        categoryList.add("Dashboard");
        categoryList.add("Jobs");
        categoryList.add("Categories");
        categoryList.add("Placement Papers");
        categoryList.add("Loans");
        categoryList.add("Schlorship");
        categoryList.add("Interview Ques");
        categoryList.add("Exam");
        categoryList.add("Education");
        ArrayAdapter<String> salesteamArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,categoryList);
        salesteamArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        category.setAdapter(salesteamArrayAdapter);


        topic.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_topic.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        error.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_error.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        anything.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_anything.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
/*        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_category.setError("");
            }
        });*/
        category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_category.setError("");
            }
        });
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        actionBar.setDisplayHomeAsUpEnabled(true);
                        getFragmentManager().popBackStackImmediate();
                        return true;
                    }
                }
                return false;
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
                if (topic.getText().toString().isEmpty()||error.getText().toString().isEmpty()||category.getText().toString().isEmpty()||anything.getText().toString().isEmpty()) {
                    if (topic.getText().toString().isEmpty()) {
                        input_topic.setError("Please enter topic");
                    }
                    if (error.getText().toString().isEmpty()){
                        input_error.setError("Please enter");}
                    if (anything.getText().toString().isEmpty()){
                        input_anything.setError("Please enter");}
                    if (category.getText().toString().isEmpty()){
                        input_category.setError("Please select category");}
                    return;
                }


                new PostDataTask().execute(Appconfig.CONTENT_URL, category.getText().toString(),topic.getText().toString(),
                        error.getText().toString(),anything.getText().toString());

            }
        });
        return v;
    }
    private class PostDataTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... contactData) {
            Boolean result = true;
            String url = contactData[0];
            String category = contactData[1];
            String topic= contactData[2];
            String error= contactData[3];
            String anything= contactData[4];

            String postBody = "";

            try {
                //all values must be URL encoded to make sure that special characters like & | ",etc.
                //do not cause problems
                postBody = CATEGORY_KEY + "=" + URLEncoder.encode(category, "UTF-8") +
                        "&" + TOPIC_KEY + "=" + URLEncoder.encode(topic, "UTF-8")+
                        "&" + ERROR_KEY + "=" + URLEncoder.encode(error, "UTF-8")+
                        "&" + ANYTHING_KEY + "=" + URLEncoder.encode(anything, "UTF-8");
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
                topic.setText("");
                error.setText("");
                anything.setText("");
                category.setText("");
                anything.clearFocus();
            }
            Toast.makeText(getActivity(), result ? "Message successfully sent!" : "There was some error in sending message. Please try again after some time.", Toast.LENGTH_LONG).show();
        }

    }

}
