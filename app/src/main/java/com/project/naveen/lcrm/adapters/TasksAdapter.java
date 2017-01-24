package com.project.naveen.lcrm.adapters;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.project.naveen.lcrm.AppSession;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.models.Tasks;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Guest on 12/8/2016.
 */

public class TasksAdapter extends RecyclerSwipeAdapter<TasksAdapter.TaskViewHolder> {
    private ArrayList<Tasks> tasksArrayList;
    private TaskViewHolder svHolder;
    private String task_id;
    private EditText date,description;
    private Context mContext;
    View v;
    BetterSpinner user;
    public static int  current_year,current_month,current_day;
    SimpleDateFormat simpleDateFormat;
    public static Dialog mdialog;
    public TasksAdapter(Context mContext, ArrayList<Tasks> tasksArrayList) {
        this.mContext = mContext;
        this.tasksArrayList = tasksArrayList;

    }

    @Override
    public TasksAdapter.TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_task, parent, false);

        return new TaskViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final TasksAdapter.TaskViewHolder viewHolder, int position) {
        current_year = Calendar.getInstance().get(Calendar.YEAR);
        current_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        current_month = Calendar.getInstance().get(Calendar.MONTH);
        final Tasks tasks = tasksArrayList.get(position);
        task_id= String.valueOf(tasks.getId());
        svHolder = viewHolder;
        viewHolder.description.setText(tasks.getDescription());
        viewHolder.deadline.setText(tasks.getDeadline());
        viewHolder.name.setText(tasks.getUser());

        viewHolder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//                    viewHolder.tvEdit.setVisibility(View.INVISIBLE);
                    viewHolder.layout_edit.setVisibility(View.INVISIBLE);
                    viewHolder.description.setPaintFlags(viewHolder.description.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
//                    viewHolder.tvEdit.setVisibility(View.VISIBLE);
                    viewHolder.layout_edit.setVisibility(View.VISIBLE);
                    viewHolder.description.setPaintFlags(viewHolder.description.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    viewHolder.description.setTextColor(Color.rgb(47, 47, 47));
                }
            }
        });
        viewHolder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper));
            }
        });
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper));
        viewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {
                viewHolder.arrow.setImageResource(R.mipmap.right);
            }

            @Override
            public void onStartClose(SwipeLayout layout) {
                viewHolder.arrow.setImageResource(R.mipmap.left);
            }

            @Override
            public void onClose(SwipeLayout layout) {
                viewHolder.arrow.setImageResource(R.mipmap.left);
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                // viewHolder.arrow.setImageResource(R.drawable.arrow);
            }
        });

        viewHolder.layout_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Won't be able to recover this file!")
                        .setCancelText("No,cancel plx!")
                        .setConfirmText("Yes,delete it!")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                 new DeleteTask().execute(Appconfig.TOKEN,task_id);
                                sweetAlertDialog.cancel();
                            }
                        })
                        .show();
                mItemManger.closeAllItems();
            }
        });
        viewHolder.layout_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.task_edit_dialog);
                date=(EditText)dialog.findViewById(R.id.deadline);

                user=(BetterSpinner)dialog.findViewById(R.id.user);
                description=(EditText)dialog.findViewById(R.id.description);

/*                date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager)mContext. getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(date.getWindowToken(), 0);
                        // getActivity().showDialog(DATE_PICKER_ID);
                        DatePickerDialog d = new DatePickerDialog(mContext, mDateSetListener, current_year, current_month, current_day);
                        d.show();

                    }
                });*/

                date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(date.getWindowToken(), 0);
                        // getActivity().showDialog(DATE_PICKER_ID);
                        DatePickerDialog d = new DatePickerDialog(mContext, mDateSetListener, current_year, current_month, current_day);
                        d.show();
                    }
                });
                ArrayAdapter<String> companyArrayAdapter=new ArrayAdapter<String>(mContext,android.R.layout.simple_spinner_item,AppSession.sales_personNameList);
                companyArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                user.setAdapter(companyArrayAdapter);
                TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                TextView edit = (TextView) dialog.findViewById(R.id.edit);
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int userId = AppSession.salesPersonList.get(AppSession.sales_personNameList.indexOf(user.getText().toString())).getId();
                        new EditTask().execute(Appconfig.TOKEN,String.valueOf(userId),description.getText().toString(),date.getText().toString(),task_id);
                        dialog.dismiss();                    }
                });
//                 dialog.setCancelable(false);
                 dialog.setTitle("Edit task");
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.75);
                int height = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.50);
                dialog.getWindow().setLayout(width, height);
                setDialog(dialog);
                dialog.show();
            }
        });

        mItemManger.bindView(viewHolder.itemView, position);

    }
    DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            current_year = year;
            current_month = monthOfYear;
            current_day = dayOfMonth;
            simpleDateFormat = new SimpleDateFormat(AppSession.date_format.replace('Y', 'y').replace('m', 'M'), Locale.ENGLISH);
            Date current_date = new Date(year-1900, monthOfYear, dayOfMonth);
            String text_date = simpleDateFormat.format(current_date);
            date.setText(text_date);
            date.clearFocus();
        }
    };
    public void setDialog(Dialog dialog) {
        mdialog = dialog;
    }

    @Override
    public int getItemCount() {
        return tasksArrayList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        TextView description, deadline, name;
        TextView tvDelete;
        TextView tvEdit;
        ImageView arrow;
        CheckBox check;
        LinearLayout layout_edit,layout_delete;
        TaskViewHolder(View itemView) {
            super(itemView);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            arrow = (ImageView) itemView.findViewById(R.id.arrow);
            tvEdit = (TextView) itemView.findViewById(R.id.tvEdit);
            tvDelete = (TextView) itemView.findViewById(R.id.tvDelete);
            description = (TextView) itemView.findViewById(R.id.description);
            deadline = (TextView) itemView.findViewById(R.id.deadline);
            name = (TextView) itemView.findViewById(R.id.name);
            check = (CheckBox) itemView.findViewById(R.id.check);
            layout_edit=(LinearLayout)itemView.findViewById(R.id.layout_edit);
            layout_delete=(LinearLayout)itemView.findViewById(R.id.layout_delete);
        }
    }

    private class EditTask extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Loading, please wait...");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "", jsonresponse = "";
            BufferedReader bufferedReader = null;
            JSONObject json = null;
            JSONObject jsonObject = null;
            String tok = params[0];
            String user_id = params[1];
            String description =params[2] ;
            String deadline =params[3] ;
            String task_id=params[4];

            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("task_id", task_id);
                jsonObject.put("user_id", user_id);
                jsonObject.put("task_description", description);
                jsonObject.put("task_deadline", deadline);
                url = new URL(Appconfig.EDIT_TASK_URL+tok);
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
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    //Log.d("Output",br.toString());
                    while ((line = br.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
                    }
                    json = new JSONObject(response);
                    //Get Values from JSONobject
                    //System.out.println("success=" + json.get("success"));

                    jsonresponse = json.getString("success");

                } else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line ="";
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
                    }
                    Log.i("response", response);
                    json = new JSONObject(response);
                    jsonresponse = json.getString("error");
                    //System.out.println("error=" + json.get("error"));
                    //succes = json.getString("success");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonresponse;
        }
        @Override
        protected void onPostExecute(String result) {

            if (result!=null)
            {
                Toast.makeText(mContext,""+result,Toast.LENGTH_LONG).show();
                //finish();
            }
            dialog.dismiss();

        }
    }

    private class DeleteTask extends AsyncTask<String,Void,String>
    {
        // ProgressDialog dialog;
        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String response="",jsonresponse="";
            JSONObject json;
            BufferedReader bufferedReader;
            String tok=params[0];
            String task_id=params[1];
            URL url;
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("task_id",task_id);

                url = new URL(Appconfig.DELETE_TASK_URL+tok);
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
                //Log.i("res code--",""+conn.getResponseCode());
                int responseCode=conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    //Log.d("Output",br.toString());
                    while ((line = br.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
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
                        Log.d("output lines", line);
                    }
                    Log.i("response",response);
                    json = new JSONObject(response);
                    jsonresponse=json.getString("error");
                    //System.out.println("error=" + json.get("error"));
                    //succes = json.getString("success");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonresponse;
        }
        @Override
        protected void onPostExecute(String result) {
            //  dialog.dismiss();
            //checkResponse=result;
            if (result!=null)
            {
                if (result.equals("success"))
                {
                    AppSession.tasksArrayList.remove(svHolder.getAdapterPosition());
                    notifyItemRemoved(svHolder.getAdapterPosition());
                   /* oppCalls.remove(callIdPosition);
                    notifyItemRemoved(callIdPosition);
                    notifyItemRangeChanged(callIdPosition,oppCalls.size());
*/
                    Log.i("Res--",result);
                }
                if (result.equals("not_valid_data"))
                {
                    Log.i("message--","not deleted");
                }
            }

        }

    }
}
