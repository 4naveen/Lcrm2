package com.project.naveen.lcrm.company;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.models.SalesTeam;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment {
    EditText company_name,phone,email,address;
    BetterSpinner main_contact_person,sales_team;
    ArrayList<String> main_contact_personList,salesTeamNameList;
    ArrayList<SalesTeam>salesTeamArrayList;
    Button submit;
    ImageView upload;
    TextInputLayout input_company_name,input_email,input_address,input_phone,input_main_contact_person,input_salesteam;

    public EditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_edit4, container, false);
        company_name=(EditText)v.findViewById(R.id.company_name);

        input_company_name=(TextInputLayout)v.findViewById(R.id.input_layout_company_name);
        input_email=(TextInputLayout)v.findViewById(R.id.input_layout_email);
        input_address=(TextInputLayout)v.findViewById(R.id.input_layout_address);
        input_phone=(TextInputLayout)v.findViewById(R.id.input_layout_phone);
        input_main_contact_person=(TextInputLayout)v.findViewById(R.id.input_layout_main_contact_person);
        input_salesteam=(TextInputLayout)v.findViewById(R.id.input_layout_salesteam);
        email=(EditText)v.findViewById(R.id.email);
        phone=(EditText)v.findViewById(R.id.phone);
        address=(EditText)v.findViewById(R.id.address);

      //  main_contact_person=(BetterSpinner)v.findViewById(R.id.contact_person);
        submit=(Button)v.findViewById(R.id.submit);
        upload=(ImageView)v.findViewById(R.id.upload);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i.createChooser(i,"select pic"),101);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (company_name.getText().toString().isEmpty())
                {
                    input_company_name.setError("Please enter opportunity name");

                    return;
                }

                else if (email.getText().toString().isEmpty()){
                    input_email.setError("Please enter email");
                    return;
                }
                else if (phone.getText().toString().isEmpty()){
                    input_phone.setError("Please enter nextAction date");
                    return;
                }
                else if (address.getText().toString().isEmpty()){
                    input_address.setError("Please enter expectedClosing date");
                    return;
                }
                else if (main_contact_person.getText().toString().isEmpty()){
                    input_main_contact_person.setError("Please select customer name");
                    return;
                }
                else if (sales_team.getText().toString().isEmpty()){
                    input_salesteam.setError("Please select salesTeam name");
                    return;
                }

                else {
                    //do nothing
                }
                Toast.makeText(getActivity(),"this is dummy Gui test",Toast.LENGTH_LONG).show();
            }
        });
        return v;

    }
    @Override
      public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==101&&resultCode== Activity.RESULT_OK)
        {
            Uri uri=data.getData();
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                Log.d("image--", String.valueOf(bitmap));

                upload.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
