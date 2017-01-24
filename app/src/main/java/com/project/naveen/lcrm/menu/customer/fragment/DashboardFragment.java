package com.project.naveen.lcrm.menu.customer.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.project.naveen.lcrm.R;

import java.util.ArrayList;

import static com.project.naveen.lcrm.R.id.barchart;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {
    LineChart lineChart;
    BarChart barChart;
    BarChart barChart1;

    int[] barcolors={R.color.cust_bar_graph_color};
    int []colors={R.color.white};
    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_dashboard_customer, container, false);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Dashboard");
        }

        lineChart = (LineChart)v.findViewById(R.id.chart);
        lineChart.setVisibleYRangeMaximum(270, YAxis.AxisDependency.LEFT);
        XAxis xAxis2=lineChart.getXAxis();
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis rightAxis2 = lineChart.getAxisRight();
        rightAxis2.setEnabled(false);
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0f, 0));
        entries.add(new Entry(0f, 1));
        entries.add(new Entry(0f, 2));
        entries.add(new Entry(30f, 3));
        entries.add(new Entry(250f, 4));
        entries.add(new Entry(3f, 5));

        LineDataSet dataset = new LineDataSet(entries, "Invoice Due by month");
        dataset.setDrawValues(false);
        ArrayList<String> labels = new ArrayList<String>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");

        LineData data = new LineData(labels, dataset);
        dataset.setColors(ColorTemplate.createColors(colors)); //
        dataset.setDrawCubic(true);
        dataset.setDrawFilled(true);
       lineChart.setNoDataTextDescription("");
        lineChart.setDescription("Y axis in $");

        lineChart.setData(data);
        lineChart.animateY(5000);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
       // end of line chart-----------


        barChart=(BarChart)v.findViewById(barchart);
        XAxis xAxis=barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);
        ArrayList<BarEntry> barentries = new ArrayList<>();
        barentries.add(new BarEntry(5f, 0));
        barentries.add(new BarEntry(8f, 1));
        barentries.add(new BarEntry(6f, 2));
        barentries.add(new BarEntry(12f, 3));
        barentries.add(new BarEntry(18f, 4));
        barentries.add(new BarEntry(9f, 5));


        ArrayList<String> barlabels = new ArrayList<String>();
        barlabels.add("Feb'16");
        barlabels.add("April'16");
        barlabels.add("Jun'16");
        barlabels.add("Aug'16");
        barlabels.add("Oct'16");
        barlabels.add("Dec'16");

        BarDataSet bardataset = new BarDataSet(barentries, "No. of Contracts");
        bardataset.setDrawValues(false);            // to remove point value in graph
        bardataset.setBarSpacePercent(50f);
        bardataset.setColor(Color.rgb(244, 134, 112));

        BarData bardata = new BarData(barlabels, bardataset);
        barChart.setData(bardata);
        barChart.setDescription(" ");
        barChart.animateY(3000);
        bardataset.setLabel("Contracts");

        //bardataset.setColors(ColorTemplate.createColors(barcolors));

        barChart1=(BarChart)v.findViewById(R.id.barchart1);
        XAxis xAxis1=barChart1.getXAxis();
        xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis rightAxis1 = barChart1.getAxisRight();
        rightAxis1.setEnabled(false);
        BarData data1=new BarData(getXAxisValues(),getDataSet());
        barChart1.setData(data1);
        //chart.setData(data);
        barChart1.setDescription("Lead Vs Opportunity");
        barChart1.animateXY(2000, 2000);
        barChart1.invalidate();

        return v;
    }
    private ArrayList<IBarDataSet> getDataSet() {
        ArrayList<IBarDataSet> dataSets ;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        BarEntry v1e1 = new BarEntry(110.000f, 0); // Jan
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(40.000f, 1); // Feb
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(60.000f, 2); // Mar
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(30.000f, 3); // Apr
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(90.000f, 4); // May
        valueSet1.add(v1e5);
   /*     BarEntry v1e6 = new BarEntry(100.000f, 5); // Jun
        valueSet1.add(v1e6);*/

        ArrayList<BarEntry> valueSet2 = new ArrayList<>();
        BarEntry v2e1 = new BarEntry(150.000f, 0); // Jan
        valueSet2.add(v2e1);
        BarEntry v2e2 = new BarEntry(90.000f, 1); // Feb
        valueSet2.add(v2e2);
        BarEntry v2e3 = new BarEntry(120.000f, 2); // Mar
        valueSet2.add(v2e3);
        BarEntry v2e4 = new BarEntry(60.000f, 3); // Apr
        valueSet2.add(v2e4);
        BarEntry v2e5 = new BarEntry(20.000f, 4); // May
        valueSet2.add(v2e5);
       /* BarEntry v2e6 = new BarEntry(80.000f, 5); // Jun
        valueSet2.add(v2e6);*/

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Lead");
        barDataSet1.setColor(Color.rgb(79, 193, 233));
        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Opportunity");
        barDataSet2.setColor(Color.rgb(244, 134, 112));

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("Feb-April 16");
        xAxis.add("April-June 16");
        xAxis.add("Jun-August 16");
        xAxis.add("August-Oct 16");
        xAxis.add("Oct-Dec 16");
        return xAxis;
    }
}
