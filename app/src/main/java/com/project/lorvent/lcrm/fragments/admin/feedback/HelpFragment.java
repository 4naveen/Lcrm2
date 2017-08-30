package com.project.lorvent.lcrm.fragments.admin.feedback;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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
public class HelpFragment extends Fragment {
    Button next;
    EditText email;
    RadioGroup topic;
    RadioButton technical, content_suggestion, ui, feature, other;
    int id;
    String text_topic;
    TextInputLayout input_email;
    FrameLayout layout;
    public static final MediaType FORM_DATA_TYPE
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    public static final String EMAIL_KEY = "entry.1168783328";
    public static final String TOPIC_KEY = "entry.2140695439";
    TextView option_error;
    View v;

    public HelpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_help, container, false);
        topic = (RadioGroup) v.findViewById(R.id.topic);
        technical = (RadioButton) v.findViewById(R.id.technical);
        content_suggestion = (RadioButton) v.findViewById(R.id.content);
        ui = (RadioButton) v.findViewById(R.id.ui);
        feature = (RadioButton) v.findViewById(R.id.feature);
        other = (RadioButton) v.findViewById(R.id.other);
        email = (EditText) v.findViewById(R.id.email);
        next = (Button) v.findViewById(R.id.next);
        option_error=(TextView)v.findViewById(R.id.option_error);
        input_email = (TextInputLayout) v.findViewById(R.id.input_layout_email);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            next.setBackgroundResource(R.drawable.ripple_btn);
        }

       // final View view = getActivity().findViewById(R.id.frame);
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_email.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        topic.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                option_error.setVisibility(View.INVISIBLE);
            }
        });
        email.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                email.setFocusable(true);
                email.setFocusableInTouchMode(true);
                return false;
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(next.getWindowToken(), 0);
                id = topic.getCheckedRadioButtonId();

                if (technical.isChecked()) {
                    text_topic = "technical";
                }
                else if(content_suggestion.isChecked()){
                    text_topic = "content_suggestion";

                }
                else if(ui.isChecked()){
                    text_topic = "ui";

                }
                else if(feature.isChecked()){
                    text_topic = "feature";

                }
                else if(other.isChecked()){
                    text_topic = "others";
                }
                else {
                    //do nothing
                    text_topic="";
                }

                if (email.getText().toString().isEmpty()||!isValidEmail(email.getText().toString())||text_topic.isEmpty()) {
                    if (email.getText().toString().isEmpty()) {
                        input_email.setError("Please enter email");
                        return;
                    }
                    if (!isValidEmail(email.getText().toString()))
                    {
                        input_email.setError("Please enter valid  email");
                        return;
                    }
                    if (text_topic.isEmpty()) {
                        option_error.setVisibility(View.VISIBLE);
                        return;

                    }
                }

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment fragment = null;

                switch (id) {

                    case R.id.technical: {
                        fragment = new TechnicalFragment();
                        break;
                    }
                    case R.id.content: {
                        fragment = new ContentFragment();

                        break;
                    }
                    case R.id.ui: {
                        fragment = new UiFragment();
                        break;
                    }
                    case R.id.feature: {
                        fragment = new FeatureFragment();
                        break;
                    }

                    case R.id.other: {
                        fragment = new OtherFragment();
                        break;
                    }
                }
            new PostDataTask().execute(Appconfig.HELP_URL, email.getText().toString(), text_topic);
                transaction.replace(R.id.frame, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        getActivity().finish();
                        return true;
                    }
                }
                return false;
            }
        });
        return v;
    }
    private class PostDataTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... contactData) {
            Boolean result = true;
            String url = contactData[0];
            String email = contactData[1];
            String topic = contactData[2];
            String postBody = "";

            try {
                postBody = EMAIL_KEY + "=" + URLEncoder.encode(email, "UTF-8") +
                        "&" + TOPIC_KEY + "=" + URLEncoder.encode(topic, "UTF-8");
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
                email.setText("");
                email.clearFocus();
            }
        }

    }
    public  boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}

