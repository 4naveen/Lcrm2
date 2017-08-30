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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtherFragment extends Fragment {
    FrameLayout layout;
    EditText problem;
    Button submit,cancel;
    public static final MediaType FORM_DATA_TYPE
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    public static final String PROBLEM_KEY = "entry.59704171";
    TextInputLayout input_problem;

    public OtherFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_other, container, false);
        setHasOptionsMenu(true);
        final ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);

        }

        layout=(FrameLayout) getActivity().findViewById(R.id.frame);
        problem = (EditText)v.findViewById(R.id.version);
        submit = (Button) v.findViewById(R.id.submit);
        cancel = (Button) v.findViewById(R.id.back);
        input_problem = (TextInputLayout) v.findViewById(R.id.input_layout_version);

        problem.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_problem.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            submit.setBackgroundResource(R.drawable.ripple_btn);
        }
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
                if (problem.getText().toString().isEmpty()) {
                    input_problem.setError("Please type problem");
                    return;
                }
                new PostDataTask().execute(Appconfig.OTHER_URL, problem.getText().toString());

            }
        });
        return v;
    }
    private class PostDataTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... contactData) {
            Boolean result = true;
            String url = contactData[0];
            String problem = contactData[1];
            String postBody = "";

            try {
                //all values must be URL encoded to make sure that special characters like & | ",etc.
                //do not cause problems
                postBody = PROBLEM_KEY + "=" + URLEncoder.encode(problem, "UTF-8");
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
            //Print Success or failure message accordingly
            if (result)
            {
                problem.setText("");
                problem.clearFocus();
            }
            Toast.makeText(getActivity(), result ? "Message successfully sent!" : "There was some error in sending message. Please try again after some time.", Toast.LENGTH_LONG).show();
        }

    }

}
