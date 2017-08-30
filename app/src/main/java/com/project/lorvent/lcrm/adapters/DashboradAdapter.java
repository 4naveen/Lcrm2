package com.project.lorvent.lcrm.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.TypeEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import com.project.lorvent.lcrm.R;

import java.util.ArrayList;

public class DashboradAdapter extends RecyclerView.Adapter<DashboradAdapter.DashViewHolder> {
  private int images[]={R.drawable.dash_cont, R.drawable.dash_pro,R.drawable.dash_opp,R.drawable.dash_cust};
    private String []titles={"Contracts","Products","Opportunity","Customers"};
    private ArrayList <Integer> numbersList;
    private Context context;
    int counter = 0;
    private int[] colors={R.color.colorContracts,R.color.colorProducts,R.color.colorOpp,R.color.colorCustomer};
    public DashboradAdapter(ArrayList<Integer>numbersList, Context context) {
        this.numbersList=numbersList;
        this.context=context;
    }

    @Override
    public DashboradAdapter.DashViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_row1_dash,parent,false);
        return new DashViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final DashboradAdapter.DashViewHolder holder,int position) {

        //for (int i = 0; i <titles.length ; i++) {
           // Log.i("position--", String.valueOf(position));
            holder.titleIcon.setImageResource(images[position]);
            holder.tvTitle.setText(titles[position]);
           // holder.tvNumber.setText(String.valueOf(numbersList.get(position)));
           // Log.i("int arrat size", String.valueOf(numbersList.get(holder.getAdapterPosition())));
             holder.cardView.setBackgroundResource(colors[position]);

        ValueAnimator animator = new ValueAnimator();
        if (numbersList.size()!=0){
            animator.setObjectValues(0, numbersList.get(holder.getAdapterPosition()));
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    holder.tvNumber.setText(String.valueOf(animation.getAnimatedValue()));
                }
            });
            animator.setEvaluator(new TypeEvaluator<Integer>() {
                public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                    return Math.round(startValue + (endValue - startValue) * fraction);
                }
            });
            animator.setDuration(1000);
            animator.start();
        }

    }
    @Override
    public int getItemCount() {
        return titles.length;
    }

    class DashViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTitle;
        TextView tvNumber;
         ImageView titleIcon;

        DashViewHolder(View itemView) {
            super(itemView);
            tvTitle=(TextView)itemView.findViewById(R.id.name);
            tvNumber=(TextView)itemView.findViewById(R.id.number);
            titleIcon=(ImageView)itemView.findViewById(R.id.iv);
            cardView=(CardView)itemView.findViewById(R.id.cv);
        }
    }

}
