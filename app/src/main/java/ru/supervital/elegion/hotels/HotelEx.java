package ru.supervital.elegion.hotels;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HotelEx extends Hotel {
    private final static String TAG = "elegion.HotelEx";
    HotelEx me = this;

    public ImageView ivImage;

    public AppCompatActivity mActivity;

    public HotelEx(AppCompatActivity mActivity) {
        super();
        this.mActivity = mActivity;
    }

    public void LoadData(final Long hotel_id){
        Boolean aRes = false;

        Map<String, String> params = new HashMap<String, String>();
            params.put("hotel_id", String.valueOf(hotel_id));

        SendURL mt = new SendURL(URL_hotel, params, aRes){

            @Override
            protected Boolean doInBackground(String... aStrings) {
                Log.d(TAG, "Request: " + this.mUrl);
                PROGRESS_DLG_ID = HotelActivity.LOAD_HOTEL_INFO;
                super.doInBackground(aStrings);
                publishProgress(new Void[]{});
                try {
                    Hotel hotel =  new Gson().fromJson(sMessage, Hotel.class);
                    Log.i(TAG, hotel.toString());
                    setMainValues(hotel);
                    return true;
                }
                catch (Throwable e) {
                    Log.e(TAG, "doInBackground failed: " + this.mUrl);
                    sMessage = e.getLocalizedMessage();
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(final Boolean success) {
                super.onPostExecute(success);
                if (!success)
                            return;
                ((HotelActivity) mActivity).onPostExecute(success, me);
            }
        };
        mt.mActivity = mActivity;
        mt.execute(mt.mUrl);
    }

    void setMainValues(Hotel hotel){
        id = hotel.id;
        name = hotel.name;
        address = hotel.address;
        stars = hotel.stars;
        suites_availability = hotel.suites_availability;
        image = hotel.image;
        distance = hotel.distance;
        lat = hotel.lat;
        lon = hotel.lon;
        bitmap_image = hotel.bitmap_image;
        LoadEx = true;
    }
}
