package ru.supervital.elegion.hotels;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;


public class HotelsListFragment extends Fragment {
    private final static String TAG = "elegion.HotelsListFragm";
    final String URL_main  = "https://dl.dropboxusercontent.com/u/1875854/1/0777.json"; // список отелей
    // “suites_availability” содержит в себе номера доступных комнат отеля разделённых двоеточием.

    TextView lblSort,
             lblSortDistance,
             lblSortFreeNum;
    ImageView imgSortDistance,
              imgSortFreeNum;

    public HotelsList mt;
    ListView lvMain;
    MainActivity mActivity;

    public HotelsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hotels_list_layout, container, false);
        InitView(rootView);
        return rootView;
    }

    void ResetLabelSort(){
        lblSort.setTypeface(null, Typeface.NORMAL);
        lblSortDistance.setTypeface(null, Typeface.NORMAL);
        lblSortFreeNum.setTypeface(null, Typeface.NORMAL);
        imgSortDistance.setVisibility(View.INVISIBLE);
        imgSortFreeNum.setVisibility(View.INVISIBLE);
    }

    void InitView(View rootView){
        lvMain = (ListView)rootView.findViewById(R.id.lvMain);
        lvMain.setOnItemClickListener(mlvMain_OnItemClickListener);

        lblSort = (TextView) rootView.findViewById(R.id.lblSort);
        lblSortDistance = (TextView) rootView.findViewById(R.id.lblSortDistance);
        lblSortDistance.setGravity(Gravity.RIGHT);
        lblSortDistance.setClickable(true);
        lblSortDistance.setOnTouchListener(mValOnTouch);
        lblSortDistance.setOnClickListener(mSortValOnClick);

        lblSortFreeNum = (TextView) rootView.findViewById(R.id.lblSortFreeNum);
        lblSortFreeNum.setGravity(Gravity.RIGHT);
        lblSortFreeNum.setClickable(true);
        lblSortFreeNum.setOnTouchListener(mValOnTouch);
        lblSortFreeNum.setOnClickListener(mSortValOnClick);

        imgSortDistance = (ImageView) rootView.findViewById(R.id.imgSortDistance);
        imgSortDistance.setOnTouchListener(mValOnTouch);
        imgSortDistance.setOnClickListener(mSortValOnClick);

        imgSortFreeNum = (ImageView) rootView.findViewById(R.id.imgSortFreeNum);
        imgSortFreeNum.setOnTouchListener(mValOnTouch);
        imgSortFreeNum.setOnClickListener(mSortValOnClick);
    }

    View.OnTouchListener mValOnTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setTop(v.getTop() + 10);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.setTop(v.getTop() - 10);
                v.performClick(); // то, по чему помазали по тому и  сортируем
            }
            return true;
        }
    };

    int mTypeSortDistance = 0,
        mTypeSortFreeNum = 0;
    boolean mOldSort=false;
    View controlSort = null; //контрол по которому кликаем

    View.OnClickListener mSortValOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!mOldSort) { // если false, оставляем как есть, тип сортировки на противоположный
                // если нажали на label или на картинку, то показать соответствующую кртинку
                imgSortDistance.setVisibility((v.getId() == R.id.lblSortDistance || v.getId() == R.id.imgSortDistance)? View.VISIBLE : View.INVISIBLE);
                imgSortFreeNum.setVisibility((v.getId() == R.id.lblSortFreeNum || v.getId() == R.id.imgSortFreeNum) ? View.VISIBLE : View.INVISIBLE);
                lblSort.setTypeface(null, Typeface.BOLD);
                // рисуем картинку типа сортировки
                // меняем значение типа сортирвки на противоположное
                if (imgSortDistance.getVisibility() == View.VISIBLE) {
                    lblSortDistance.setTypeface(null, Typeface.BOLD);
                    lblSortFreeNum.setTypeface(null, Typeface.NORMAL);
                    if (mTypeSortDistance == 1) {
                        imgSortDistance.setImageResource(R.drawable.sort_down);
                        mTypeSortDistance = 0;
                    } else {
                        imgSortDistance.setImageResource(R.drawable.sort_up);
                        mTypeSortDistance = 1;
                    }
                } else if (imgSortFreeNum.getVisibility() == View.VISIBLE) {
                    lblSortDistance.setTypeface(null, Typeface.NORMAL);
                    lblSortFreeNum.setTypeface(null, Typeface.BOLD);
                    if (mTypeSortFreeNum == 1) {
                        imgSortFreeNum.setImageResource(R.drawable.sort_down);
                        mTypeSortFreeNum = 0;
                    } else {
                        imgSortFreeNum.setImageResource(R.drawable.sort_up);
                        mTypeSortFreeNum = 1;
                    }
                }
            }

            controlSort = v; // контрол по которому кликнули

            Collections.sort(mt.hotelArrayList, new Comparator<Hotel>() {
                @Override
                public int compare(Hotel o1, Hotel o2) {

                    int res = 0;
                    if (controlSort.getId() == R.id.lblSortDistance || controlSort.getId() == R.id.imgSortDistance) {
                        res = Double.valueOf(o1.distance).compareTo(o2.distance) * (mTypeSortDistance == 1 ? 1 : -1);
                    } else if (controlSort.getId() == R.id.lblSortFreeNum || controlSort.getId() == R.id.imgSortFreeNum) {
                        res = Double.valueOf(Double.valueOf(o1.getSuites_availability())).
                                compareTo(Double.valueOf(o2.getSuites_availability())) * (mTypeSortFreeNum == 1 ? 1 : -1);
                    }
                    mOldSort = false;
                    return res;
                }
            });
            mt.mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mActivity.fraHotelsList = this;
        LoadHotelsList();
    }

    public void RefreshHotelsList(){
        if (mt!=null)
            mt.hotelArrayList.clear();
        mOldSort = true;
        LoadHotelsList();
    }

    public Boolean aResult = false;
    void LoadHotelsList() {
        if (mt==null || mt.hotelArrayList.size()==0) {
            //ResetLabelSort();
            mOldSort = true;
            mt = new HotelsList(URL_main, null, aResult);
            mt.mActivity = mActivity;
            mt.lvMain = lvMain;
            mt.controlSort = controlSort;
            mt.execute(mt.mUrl);
        }
    }

    AdapterView.OnItemClickListener mlvMain_OnItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view,
        int position, long id) {
            Log.d(TAG, "itemClick: position = " + position + ", id = " + id);
            Intent intent = new Intent(mActivity, HotelActivity.class)
                .putExtra(HotelActivity.HOTEL_ID, id);
            startActivity(intent);
        }
    };

    void onPostExecute(){
        mOldSort = false;
    }
}
