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
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class HotelActivity extends AppCompatActivity {
    private final static String TAG = "elegion.HotelActivity";
    public static String HOTEL_ID = "hotel_id";

    ApplicationELegionHotels myAppl;

    public final static int LOAD_HOTEL_INFO  = 10;
    public final static int LOAD_HOTEL_IMAGE = 20;

    HotelEx mainHotel = new HotelEx(this);
    Toolbar toolbar;
    ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myAppl = (ApplicationELegionHotels) getApplication();

        setContentView(R.layout.activity_hotel);
        InitView();

        Long hotel_id = getIntent().getExtras().getLong(HOTEL_ID);

        if (!myAppl.mt.mAdapter.isHotelEx(hotel_id)) {
            mainHotel.ivImage = ivImage;
            mainHotel.LoadData(hotel_id);
        } else {
            mainHotel.setMainValues(myAppl.mt.mAdapter.getItemById(hotel_id));
            LoadDataInView(mainHotel);
        }
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

    public void onPostExecute(final Boolean success, HotelEx hotel) {
        if (!success)
                    return;
        LoadImage();
        LoadDataInApp(hotel);
        LoadDataInView(hotel);
    }

    public void LoadImageInApp(Hotel hotel) {
        Hotel aHotel = myAppl.mt.mAdapter.getItemById(hotel.id);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        hotel.bitmap_image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        InputStream is = new ByteArrayInputStream(stream.toByteArray());
        aHotel.bitmap_image = BitmapFactory.decodeStream(is);
    }

    public void LoadDataInApp(Hotel hotel){
        Hotel aHotel = myAppl.mt.mAdapter.getItemById(hotel.id);
            aHotel.id = hotel.id;
            aHotel.name = hotel.name;
            aHotel.address = hotel.address;
            aHotel.stars = hotel.stars;
            aHotel.suites_availability = hotel.suites_availability;
            aHotel.image = hotel.image;
            aHotel.distance = hotel.distance;
            aHotel.lat = hotel.lat;
            aHotel.lon = hotel.lon;
            aHotel.LoadEx = true;
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
        //---
        if (hotel.bitmap_image==null)
                ((TextView) findViewById(R.id.tvNoImage)).setVisibility(View.VISIBLE);
            else
                ((ImageView) findViewById(R.id.ivImage)).setImageBitmap(hotel.bitmap_image);

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

            if (result!=null) {
                result.setDensity(Bitmap.DENSITY_NONE);
                result = Bitmap.createBitmap(result, 1, 1, result.getWidth() - 2, result.getHeight() - 2);
                bmImage.setImageBitmap(result);
                mainHotel.bitmap_image = result;
                LoadImageInApp(mainHotel);
            }
            ((TextView) findViewById(R.id.tvNoImage)).setVisibility(result==null ? View.VISIBLE: View.INVISIBLE);
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
