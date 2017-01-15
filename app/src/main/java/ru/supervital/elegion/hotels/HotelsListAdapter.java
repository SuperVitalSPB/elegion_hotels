package ru.supervital.elegion.hotels;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class HotelsListAdapter extends BaseAdapter {
    private final static String TAG = "elegion.HotelsListAdapter";
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Hotel> hotels;

    HotelsListAdapter(Context context, ArrayList<Hotel> hotels) {
        ctx = context;
        this.hotels = hotels;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        int aRes= 0;
        if (hotels!=null) aRes = hotels.size();
        return aRes;
    }

    public Hotel getItemById(Long id){
        Hotel res = null;

        for (Hotel hotel :hotels)
            if (hotel.id.equals(id)) {
                res = hotel;
                break;
            }
        return res;
    }

    // загружена ли в отель доп.инфо
    public boolean isHotelEx(Long id){
        Hotel hotel = (Hotel) getItemById(id);
        return hotel != null && hotel.LoadEx;
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return hotels.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return  hotels.get(position).id;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.hotel_item, parent, false);
        }

        Hotel h = getHotel(position);

        // заполняем View в пункте списка данными
        ((TextView) view.findViewById(R.id.tvName)).setText(h.name);
        Integer stars = (int) Math.round(new Double(h.stars));
        int visible = stars > 0 ? View.VISIBLE:View.INVISIBLE;
        TextView tv = ((TextView) view.findViewById(R.id.tvStars));
            tv.setText(stars.toString());
            tv.setVisibility (visible);
        ((ImageView) view.findViewById(R.id.ivStars)).setVisibility (visible);
        ((TextView) view.findViewById(R.id.tvAddress)).setText(h.address);
        ((TextView) view.findViewById(R.id.tvDistance)).setText(h.distance.toString());
        ((TextView) view.findViewById(R.id.tvSuites_availability)).setText(h.suites_availability);
        return view;
    }

    Hotel getHotel(int position) {
        return ((Hotel) getItem(position));
    }


}
