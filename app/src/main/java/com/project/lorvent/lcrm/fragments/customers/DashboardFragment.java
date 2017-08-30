package com.project.lorvent.lcrm.fragments.customers;


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
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.models.customer.InvoiceByMonth;
import com.project.lorvent.lcrm.utils.AppSession;

import java.util.ArrayList;

import static com.project.lorvent.lcrm.R.id.barchart;

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

        int count1 = 0;
        for (int i = 0; i < AppSession.byMonthArrayList.size(); i++) {
            InvoiceByMonth invoiceByMonth=AppSession.byMonthArrayList.get(i);
            if (i % 2 != 0) {
                Entry entry1 = new Entry(invoiceByMonth.getInvoices(), count1);
                entries.add(entry1);
                count1++;
            }
        }
        LineDataSet dataset = new LineDataSet(entries, "Invoice Due by month");
        dataset.setDrawValues(false);
        ArrayList<String> labels = new ArrayList<String>();

        for (int i = 0; i < AppSession.byMonthArrayList.size(); i++) {
            InvoiceByMonth invoiceByMonth=AppSession.byMonthArrayList.get(i);
            if (i % 2 == 0) {
                labels.add(invoiceByMonth.getMonth() + "" + invoiceByMonth.getYear().substring(2, 4));
            }
        }
        LineData data = new LineData(labels, dataset);
        dataset.setColors(ColorTemplate.createColors(colors)); //
        dataset.setDrawCubic(true);
        dataset.setDrawFilled(true);
        lineChart.setNoDataTextDescription("");
        lineChart.setDescription("Y axis in $");
        XAxis linexAxis = lineChart.getXAxis();
        linexAxis.setEnabled(true);
        linexAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis ryAxis = lineChart.getAxisRight();
        ryAxis.setEnabled(false);
        ryAxis.setDrawGridLines(false);
        YAxis lineleft = lineChart.getAxisLeft();
        lineleft.setSpaceBottom(0);
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
        YAxis left = barChart.getAxisLeft();
        left.setSpaceBottom(0);
        ArrayList<BarEntry> barentries = new ArrayList<>();

        int count = 0;
        for (int i = 0; i < AppSession.byMonthArrayList.size(); i++) {
            InvoiceByMonth invoiceByMonth=AppSession.byMonthArrayList.get(i);
            if (i % 2!= 0) {
                BarEntry entry1 = new BarEntry(invoiceByMonth.getContracts(), count);
                barentries.add(entry1);
                count++;
            }
        }
        ArrayList<String> barlabels = new ArrayList<String>();
        for (int i = 0; i < AppSession.byMonthArrayList.size(); i++) {
            InvoiceByMonth invoiceByMonth=AppSession.byMonthArrayList.get(i);
            if (i % 2 == 0) {
                barlabels.add(invoiceByMonth.getMonth() + "" + invoiceByMonth.getYear().substring(2, 4));

            }
        }
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
        YAxis leftAxis1 = barChart1.getAxisLeft();
        leftAxis1.setSpaceBottom(0);
        BarData data1=new BarData(getXAxisValues(),getDataSet());
        barChart1.setData(data1);
        //chart.setData(data);
        barChart1.setNoDataTextDescription("");

        barChart1.setDescription("Sales Progress");
        barChart1.animateXY(2000, 2000);
        barChart1.invalidate();

        return v;
    }
    private ArrayList<IBarDataSet> getDataSet() {
        ArrayList<IBarDataSet> dataSets ;
        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        ArrayList<BarEntry> valueSet2 = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < AppSession.byMonthArrayList.size(); i++) {
            InvoiceByMonth invoiceByMonth=AppSession.byMonthArrayList.get(i);
            if (i % 2 != 0) {
                BarEntry entry1 = new BarEntry(invoiceByMonth.getLeads(), count);
                valueSet1.add(entry1);
                BarEntry entry2 = new BarEntry(invoiceByMonth.getOpportunity(), count);
                valueSet2.add(entry2);
                count++;
            }
        }
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
        for (int i = 0; i < AppSession.byMonthArrayList.size(); i++) {
            InvoiceByMonth invoiceByMonth=AppSession.byMonthArrayList.get(i);
            if (i % 2 != 0) {
                xAxis.add(invoiceByMonth.getMonth() + "" + invoiceByMonth.getYear().substring(2, 4));
            }
        }
        return xAxis;
    }
}
