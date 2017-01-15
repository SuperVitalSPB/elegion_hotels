package ru.supervital.elegion.hotels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

// главный класс приложения
@SuppressLint("DefaultLocale")
public class ApplicationELegionHotels extends Application{
    private final static String TAG = "elegion.ApplicationELegionHotels";
    public static final String URL_main  = "https://dl.dropboxusercontent.com/u/1875854/1/0777.json"; // список отелей
    // “suites_availability” содержит в себе номера доступных комнат отеля разделённых двоеточием.

    public HotelsList mt;
    AppCompatActivity mt_mActivity;
    ListView mt_lvMain;
    View mt_controlSort;


    @Override
    public void onCreate() {
        super.onCreate();
        //LoadAppSettings();
    }

    public Boolean aResult = false;
    public void LoadHotelsList() {
        if (mt==null || mt.hotelArrayList.size()==0) {
            mt = new HotelsList(ApplicationELegionHotels.URL_main, null, aResult);
            mt.mActivity = mt_mActivity;
            mt.lvMain = mt_lvMain;
            mt.controlSort = mt_controlSort;
            mt.execute(mt.mUrl);
        } else {
            mt_lvMain.setAdapter(mt.mAdapter);
            mt.mAdapter.notifyDataSetChanged();
        }
    }


}