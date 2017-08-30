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
public class FeatureFragment extends Fragment {

    EditText feature;
    BetterSpinner type;
    Button submit, cancel;
    ArrayList<String> featureList;
    FrameLayout layout;
    TextInputLayout input_type, input_feature;

    public static final MediaType FORM_DATA_TYPE
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    //URL derived from form URL
    //input element ids found from the live form page
    public static final String TYPE_KEY = "entry.303257193";
    public static final String FEATURE_KEY = "entry.1954268861";

    public FeatureFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_feature, container, false);
        setHasOptionsMenu(true);

        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);

        }
        type = (BetterSpinner) v.findViewById(R.id.screen);
        feature = (EditText) v.findViewById(R.id.version);
        submit = (Button) v.findViewById(R.id.submit);
        cancel = (Button) v.findViewById(R.id.back);
        input_feature = (TextInputLayout) v.findViewById(R.id.input_layout_version);
        input_type = (TextInputLayout) v.findViewById(R.id.input_layout_screen);
        feature.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_feature.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_type.setError("");
            }
        });
        featureList = new ArrayList<>();
        layout = (FrameLayout) getActivity().findViewById(R.id.frame);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            submit.setBackgroundResource(R.drawable.ripple_btn);
        }
        featureList.add("Job");
        featureList.add("Placement Papers");
        featureList.add("Loans");
        featureList.add("Scholarship");
        featureList.add("Interview Ques");
        featureList.add("Exam");
        featureList.add("Education");
        featureList.add("Other");

        ArrayAdapter<String> salesteamArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, featureList);
        salesteamArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        type.setAdapter(salesteamArrayAdapter);
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
                if (feature.getText().toString().isEmpty() || type.getText().toString().isEmpty()) {
                    if (feature.getText().toString().isEmpty()) {
                        input_feature.setError("Please mention your new feature required");

                    }
                    if (type.getText().toString().isEmpty()) {
                        input_type.setError("Please select screen");

                    }
                    return;
                }

                new PostDataTask().execute(Appconfig.FEATURE_URL, type.getText().toString(), feature.getText().toString());

            }
        });
        return v;
    }

    private class PostDataTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... contactData) {
            Boolean result = true;
            String url = contactData[0];
            String type = contactData[1];
            String feature = contactData[2];
            String postBody = "";

            try {
                //all values must be URL encoded to make sure that special characters like & | ",etc.
                //do not cause problems
                postBody = TYPE_KEY + "=" + URLEncoder.encode(type, "UTF-8") +
                        "&" + FEATURE_KEY + "=" + URLEncoder.encode(feature, "UTF-8");
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
                type.setText("");
                feature.setText("");
                feature.clearFocus();
            }
            Toast.makeText(getActivity(), result ? "Message successfully sent!" : "There was some error in sending message. Please try again after some time.", Toast.LENGTH_LONG).show();
        }

    }

}
