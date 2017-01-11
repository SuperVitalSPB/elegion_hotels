package ru.supervital.elegion.hotels;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

public class HotelActivity extends AppCompatActivity {
    private final static String TAG = "elegion.HotelActivity";
    public static String HOTEL_ID = "hotel_id";

    public final static int LOAD_HOTEL_INFO  = 10;
    public final static int LOAD_HOTEL_IMAGE = 20;

    HotelEx mainHotel = new HotelEx(this);
    Toolbar toolbar;
    ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);
        InitView();

        Long hotel_id = getIntent().getExtras().getLong(HOTEL_ID);

        mainHotel.mActivity = this;
        mainHotel.ivImage = ivImage;
        mainHotel.LoadData(hotel_id);
    }

    @Override
    protected Dialog onCreateDialog(int dialogId){
        String sStr="";
        switch (dialogId) {
            case LOAD_HOTEL_INFO:
                sStr = getString(R.string.sLoadHInfo);
                break;
            case LOAD_HOTEL_IMAGE:
                sStr = getString(R.string.sLoadHImage);
                break;
        }
        ProgressDialog progress = null;
        progress = new ProgressDialog(this);
        progress.setMessage(sStr);
        return progress;
    }

    public void onPostExecute(final Boolean success, Hotel hotel) {
        if (!success)
                    return;
        LoadImage();
        LoadDataInView(hotel);
    }

    public void LoadDataInView(Hotel hotel){
        if (hotel==null)
                        return;
        toolbar.setTitle(hotel.name);
        Integer iStars = (int) Math.round(new Double(hotel.stars));
        int visible = iStars > 0 ? View.VISIBLE:View.INVISIBLE;
        TextView tv = ((TextView) findViewById(R.id.tvStars));
        tv.setText(iStars.toString());
        tv.setVisibility (visible);
        ((ImageView) findViewById(R.id.ivStars)).setVisibility (visible);
        //--
        ((TextView) findViewById(R.id.tvSuites_availability)).setText(hotel.suites_availability);
        ((TextView) findViewById(R.id.tvAddress)).setText(hotel.address);
        ((TextView) findViewById(R.id.tvDistance)).setText(hotel.distance.toString());
        //--
        ((TextView) findViewById(R.id.tvlat)).setText(hotel.lat.toString());
        ((TextView) findViewById(R.id.tvlon)).setText(hotel.lon.toString());

    }

    void InitView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    void LoadImage(){
        ivImage = (ImageView) findViewById(R.id.ivImage);
        String sStr = Hotel.URL_img;
        sStr = sStr.replace("${pict_id}", mainHotel.image);
        Log.d(TAG, sStr);
        new DownloadImageTask(ivImage, this)
                .execute(sStr);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        int PROGRESS_DLG_ID = 20;
        AppCompatActivity mActivity;


        public DownloadImageTask(ImageView bmImage, AppCompatActivity mActivity) {
            this.bmImage = bmImage;
            this.mActivity = mActivity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Bitmap doInBackground(String... urls) {
            publishProgress(new Void[]{});
            String urldisplay = urls[0];
            Bitmap mBmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mBmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
            return mBmp;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            ShowDialog(false);

            if (result==null) {
                ((TextView) findViewById(R.id.tvNoImage)).setVisibility(View.VISIBLE);
                return;
            }
            result.setDensity(Bitmap.DENSITY_NONE);
            result = Bitmap.createBitmap(result, 1, 1, result.getWidth()-2, result.getHeight()-2);
            bmImage.setImageBitmap(result);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            ShowDialog(false);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            ShowDialog(true);
        }

        void ShowDialog(boolean  aShow){
            if (mActivity == null) return; // добавить проверку, есть ли реализация метода onCreateDialog
            if (aShow)
                mActivity.showDialog(PROGRESS_DLG_ID);
            else
                mActivity.dismissDialog(PROGRESS_DLG_ID);
        }

    }

}
