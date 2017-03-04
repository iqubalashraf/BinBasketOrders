package com.binbasket.binbasketorders;

import android.support.v7.widget.RecyclerView;

/**
 * Created by ashrafiqubal on 04/07/16.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
        private static final String TAG = "CustomAdapter";

        private static String[] mDataSet;
        private static String[] mDataSet2;
        private static String[] maddressset;
        private static int[] mDataSetTypes;

        public static final int ADDRESSVIEW = 2;
        public static final int BUTTONVIEW = 3;
        public static final int ACTIVEORDER = 1;
        public static final int COMPLETEDORDER = 0;
        public static final int CANCELLEDORDER = 4;

        public CustomAdapter(String[] dataSet,String[] dataSet2,String[] addressSet, int[] dataSetTypes){
        mDataSet = dataSet;
        mDataSetTypes = dataSetTypes;
        mDataSet2 = dataSet2;
        maddressset = addressSet;
    }
}
