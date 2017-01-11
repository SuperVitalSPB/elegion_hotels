package ru.supervital.elegion.hotels;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;


public class MainActivity extends AppCompatActivity {
    private final static String TAG = "elegion.MainActivity";

    public HotelsListFragment fraHotelsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fraHotelsList.RefreshHotelsList();            }
        });

    }

    @Override
    protected Dialog onCreateDialog(int dialogId){
        ProgressDialog progress = null;
        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.sLoadHList));
        return progress;
    }

}
