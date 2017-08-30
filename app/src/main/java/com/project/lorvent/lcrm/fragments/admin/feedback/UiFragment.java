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
import android.widget.RatingBar;
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
public class UiFragment extends Fragment {
    EditText feedback;
    RatingBar ratingbar;
    Button submit,cancel;
    FrameLayout layout;
    TextInputLayout input_feedback;

    public static final MediaType FORM_DATA_TYPE
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    //URL derived from form URL
    //input element ids found from the live form page
    public static final String RATING_KEY = "entry.1834610698";
    public static final String FEEDBACK_KEY = "entry.202694970";
    public UiFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_ui, container, false);
        setHasOptionsMenu(true);
        final ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);

        }
        layout=(FrameLayout) getActivity().findViewById(R.id.frame);
        feedback = (EditText)v.findViewById(R.id.feedback);
        input_feedback = (TextInputLayout) v.findViewById(R.id.input_layout_feedback);

        ratingbar=(RatingBar)v.findViewById(R.id.rating);
        submit = (Button)v.findViewById(R.id.submit);
        cancel = (Button) v.findViewById(R.id.back);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            submit.setBackgroundResource(R.drawable.ripple_btn);
        }
        feedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_feedback.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
                if (feedback.getText().toString().isEmpty()) {
                    input_feedback.setError("This field is required");
                    return;
                }
                //Log.i("rating-", String.valueOf(ratingbar.getRating()));
                new PostDataTask().execute(Appconfig.UI_URL, String.valueOf(ratingbar.getRating()),feedback.getText().toString());
            }
        });
        return v;
    }
    private class PostDataTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... contactData) {
            Boolean result = true;
            String url = contactData[0];
            String rating = contactData[1];
            String feedback= contactData[2];


            String postBody = "";

            try {
                //all values must be URL encoded to make sure that special characters like & | ",etc.
                //do not cause problems
                postBody = RATING_KEY + "=" + URLEncoder.encode(rating, "UTF-8") +
                        "&" + FEEDBACK_KEY + "=" + URLEncoder.encode(feedback, "UTF-8");
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
                feedback.setText("");
                ratingbar.setRating(0F);
                feedback.clearFocus();
            }
            Toast.makeText(getActivity(), result ? "Message successfully sent!" : "There was some error in sending message. Please try again after some time.", Toast.LENGTH_LONG).show();
        }
    }
}
