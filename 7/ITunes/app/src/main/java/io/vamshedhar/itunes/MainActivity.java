package io.vamshedhar.itunes;

import android.content.Context;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements GetDataFromServer.IData, FilteredAppAdapter.IDataAdapter {

    public static final String TAG = "ITunes";
    public static final String BASE_URL = "https://itunes.apple.com/us/rss/toppaidapplications/limit=25/json";

    ArrayList<TopApp> topApps;
    ArrayList<TopApp> filteredTopApps;
    ArrayList<TopApp> favAppsList;

    ImageView refreshButton;
    Switch sortOrder;
    ListView topAppsList;
    ProgressBar pb;
    TextView filterLabel, warningLabel;

    TopAppsAdapter adapter;

    private RecyclerView favApps;
    private RecyclerView.Adapter favAppsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    DatabaseDataManager dbManager;

    public boolean isConnectedOnline(){

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if(info != null && info.isConnected()){
            return true;
        }

        return false;
    }

    public void hideViews(){
        topAppsList.setVisibility(View.INVISIBLE);
        filterLabel.setVisibility(View.INVISIBLE);
        favApps.setVisibility(View.INVISIBLE);
        sortOrder.setVisibility(View.INVISIBLE);
        refreshButton.setVisibility(View.INVISIBLE);
        warningLabel.setVisibility(View.INVISIBLE);
        pb.setVisibility(View.VISIBLE);
    }

    public void showViews(){
        topAppsList.setVisibility(View.VISIBLE);
        filterLabel.setVisibility(View.VISIBLE);
        favApps.setVisibility(View.VISIBLE);
        sortOrder.setVisibility(View.VISIBLE);
        refreshButton.setVisibility(View.VISIBLE);
        pb.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refreshButton = (ImageView) findViewById(R.id.refreshButton);
        sortOrder = (Switch) findViewById(R.id.sortOrder);
        topAppsList = (ListView) findViewById(R.id.topAppsList);
        pb = (ProgressBar) findViewById(R.id.mainProgress);
        filterLabel = (TextView) findViewById(R.id.filterLabel);
        warningLabel = (TextView) findViewById(R.id.warningLabel);
        favApps = (RecyclerView) findViewById(R.id.favApps);
        favApps.setHasFixedSize(true);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideViews();
                HashMap<String, String> urlParams = new HashMap<>();
                if (isConnectedOnline()){
                    new GetDataFromServer(MainActivity.this, MainActivity.BASE_URL, urlParams).execute();
                } else{
                    Toast.makeText(MainActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                    showViews();
                    topApps = new ArrayList<>();
                    getAppsList();
                }

            }
        });

        hideViews();

        dbManager = new DatabaseDataManager(this);

        sortOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    sortOrder.setText("Ascending");
                    Collections.sort(filteredTopApps);
                    adapter.notifyDataSetChanged();
                } else{
                    sortOrder.setText("Descending");
                    Collections.reverse(filteredTopApps);
                    adapter.notifyDataSetChanged();
                }

            }
        });

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        favApps.setLayoutManager(mLayoutManager);

        HashMap<String, String> urlParams = new HashMap<>();

        favAppsList = new ArrayList<>();
        filteredTopApps = new ArrayList<>();
        
        if (isConnectedOnline()){
            new GetDataFromServer(this, MainActivity.BASE_URL, urlParams).execute();
        } else{
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
            showViews();
            topApps = new ArrayList<>();
            getAppsList();
        }

        

    }

    public void getAppsList(){
        favAppsList = (ArrayList<TopApp>) dbManager.getAll();
        favAppsAdapter = new FilteredAppAdapter(favAppsList, this, dbManager, this);
        favApps.setAdapter(favAppsAdapter);

        if (favAppsList.size() == 0){
            warningLabel.setVisibility(View.VISIBLE);
        } else {
            warningLabel.setVisibility(View.INVISIBLE);
        }

        filteredTopApps = new ArrayList<>();

        for (TopApp app : topApps) {
            if (favAppsList.indexOf(app) < 0){
                filteredTopApps.add(app);
            }
        }

        if (sortOrder.isChecked()){
            Log.d(MainActivity.TAG, "ASC");
            Collections.sort(filteredTopApps, new Comparator<TopApp>() {
                @Override
                public int compare(TopApp topApp, TopApp t1) {
                    double currentPrice = Double.parseDouble(topApp.price);
                    double otherPrice = Double.parseDouble(t1.price);
                    if(currentPrice < otherPrice){
                        return -1;
                    } else if (currentPrice > otherPrice){
                        return 1;
                    }
                    return 0;
                }
            });
        } else{
            Collections.sort(filteredTopApps, new Comparator<TopApp>() {
                @Override
                public int compare(TopApp topApp, TopApp t1) {
                    double currentPrice = Double.parseDouble(topApp.price);
                    double otherPrice = Double.parseDouble(t1.price);
                    if(currentPrice < otherPrice){
                        return 1;
                    } else if (currentPrice > otherPrice){
                        return -1;
                    }
                    return 0;
                }
            });
            Log.d(MainActivity.TAG, "DESC");
        }

        adapter = new TopAppsAdapter(this, filteredTopApps);
        topAppsList.setAdapter(adapter);
        adapter.setNotifyOnChange(true);

        topAppsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                TopApp topApp = filteredTopApps.get(i);
                dbManager.saveApp(topApp);
                Log.d(MainActivity.TAG, topApp.toString());
                getAppsList();
                return true;
            }
        });
    }

    @Override
    public void setupData(ArrayList<TopApp> ObjectsList) {
        Log.d(MainActivity.TAG, ObjectsList.size()+"");

        topApps = ObjectsList;

        pb.setVisibility(View.INVISIBLE);

        showViews();

        getAppsList();
    }


    @Override
    public void updateData(String id) {
        dbManager.deleteApp(id);
        getAppsList();
    }
}
