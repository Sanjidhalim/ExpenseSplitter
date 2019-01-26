package com.sanjidhalim.expensesplitter;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.SugarApp;
import com.orm.SugarContext;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView mTextView;
    private ListView mListView;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = (ListView) findViewById(R.id.tripList);
        dbHelper = DbHelper.getInstance(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddTrip.class);
                startActivity(intent);
            }
        });

        showTrips();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showTrips();
    }

    public void showTrips(){
        Cursor allTrips = dbHelper.getAllTrips();
        allTrips.moveToFirst();
        String[] name=new String[allTrips.getCount()];
        int i=0;
        while (!allTrips.isAfterLast()){
            name[i++]= allTrips.getString(allTrips.getColumnIndex("tripName"));
            allTrips.moveToNext();
        }
        mTextView = (TextView) findViewById(R.id.testBox);
        mTextView.setText(Arrays.toString(name));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,
                android.R.id.text1,name);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) mListView.getItemAtPosition(position);
                Intent intent = new Intent(getBaseContext(),TripExpensesActivity.class);
                intent.putExtra("TRIP_NAME",itemValue);
                startActivity(intent);
            }
        });
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

