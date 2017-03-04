package com.binbasket.binbasketorders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    public static final String SERVERIPADDRESS = "54.169.86.227:8080";
    private ProgressDialog pDialog;
    String serverResponse = null;
    private static RecyclerView mRecyclerView;
    private static CustomAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String[] item_list;
    private static int[] dataType;
    private static String[] data_list;
    private static String[] address_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority(SERVERIPADDRESS)
                .appendPath("binbasket")
                .appendPath("listofallactiveorder.jsp")
                .appendQueryParameter("USERPHONENO", "adminOf@binbasket")
                .appendQueryParameter("PASSWORD", "password@binBasket");
        String myUrl = builder.build().toString();
        myUrl = myUrl.replace("%3A", ":");
        Log.d("URL://", myUrl);
        FetchOrders fetchOrders = new FetchOrders();
        fetchOrders.execute(myUrl);
    }
    public class FetchOrders extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("Starting download");
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            Boolean prepared;
            //progress.show();
            try {
                String str;
                HttpClient myClient = new DefaultHttpClient();
                HttpGet get = new HttpGet(params[0]);
                HttpResponse myResponse = myClient.execute(get);
                BufferedReader br = new BufferedReader(new InputStreamReader(myResponse.getEntity().getContent()));
                while ((str = br.readLine()) != null) {
                    serverResponse = str;
                    Log.d("Address Activity: ", str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            prepared = true;
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            pDialog.dismiss();
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            //progress.cancel();
            Log.d("Address Activity: ", "onPostExecution ");
            Object obj = JSONValue.parse(serverResponse);
            JSONObject jsonObject = (JSONObject)obj;
            String status = (String)jsonObject.get("STATUS");
            if(status.equals("0")){
                Log.d("Address:", "Fetch Successfully");
                //Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
                int maxInt=0;
                try{
                    Long max = (Long)jsonObject.get("MAX");
                    maxInt = ((int)(long)max);
                    Log.d("No error: ", Integer.toString(maxInt));
                    item_list= new String[maxInt];
                    dataType = new int[maxInt];
                    data_list = new String[maxInt];
                    address_list = new String[maxInt];
                    //Integer[] dataType = new Integer[maxInt];
                    int i=maxInt-1;
                    for(int j=0;i>=0;i--,j++){
                        String itemsList = (String)jsonObject.get("ITEMSLIST"+Integer.toString(i));
                        String orderdateTime = (String)jsonObject.get("ORDERTIMEDATE"+Integer.toString(i));
                        String active = (String)jsonObject.get("ACTIVE"+Integer.toString(i));
                        String address = (String)jsonObject.get("ADDRESS"+Integer.toString(i));
                        address = address.replace("; ","\n");
                        itemsList = itemsList.replace("[","");
                        itemsList = itemsList.replace("]","");
                        //String finalData = Html.fromHtml(getString(R.string.listofitems))+"\n"+itemsList+"\n\n"+"Placed On: "+orderdateTime;
                        //Log.d("Final String: ", finalData);
                        item_list[j]=itemsList;
                        data_list[j]=orderdateTime;
                        dataType[j]=Integer.parseInt(active);
                        address_list[j]=address;
                    }
                }catch (Exception e){
                    Log.d("Error: ", e.getMessage());
                }
                if(maxInt==0){
                    Toast.makeText(MainActivity.this, "No order till now", Toast.LENGTH_LONG).show();
                }else {
                    mRecyclerView = (RecyclerView)findViewById(R.id.recyclerViewHome);
                    mLayoutManager = new LinearLayoutManager(MainActivity.this);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mAdapter = new CustomAdapter(item_list,data_list,address_list, dataType);
                    mRecyclerView.setAdapter(mAdapter);
                }
                //Adapter is created in the last step
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
