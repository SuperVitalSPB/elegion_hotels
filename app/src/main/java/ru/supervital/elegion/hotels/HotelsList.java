package ru.supervital.elegion.hotels;


import android.util.Log;
import android.view.View;
import android.widget.ListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HotelsList extends SendURL {
    private final static String TAG = "elegion.HotelsList";

    ArrayList<Hotel> hotelArrayList = new ArrayList<Hotel>();
    HotelsListAdapter mAdapter;
    ListView lvMain;
    View controlSort; // контрол для сортировки
    Type itemsListType = new TypeToken<List<Hotel>>(){}.getType();

    public HotelsList(String Url, HashMap<String, String> Params, Boolean Result) {
        super(Url, Params, Result);
    }

    @Override
    protected Boolean doInBackground(String... aStrings) {
        super.doInBackground(aStrings);
        Log.d(TAG, "Message: " + sMessage);
        publishProgress(new Void[]{});
        try {
            hotelArrayList = new Gson().fromJson(sMessage, itemsListType);
            return true;
        }
        catch (Throwable e) {
            Log.e(TAG, "Request failed: " + this.mUrl);
            sMessage = e.getLocalizedMessage();
            e.printStackTrace();
            return false;
        }
    }

    /**
     * после выполнения
     */
    @Override
    protected void onPostExecute(final Boolean success) {
        super.onPostExecute(success);
        if (!success) return;
        Log.i(TAG, hotelArrayList.toString());
        mAdapter = new HotelsListAdapter(mActivity, hotelArrayList);
        lvMain.setAdapter(mAdapter);
        if (controlSort!=null)
            controlSort.performClick();// если была сортировка ранее, то повторяем её.
        else
            mAdapter.notifyDataSetChanged();
        ((MainActivity) mActivity).fraHotelsList.onPostExecute();
    }

} // main class
