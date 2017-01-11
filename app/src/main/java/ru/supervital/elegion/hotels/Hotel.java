package ru.supervital.elegion.hotels;

import android.support.v7.app.AppCompatActivity;

import java.util.*;


public class Hotel {
    private final static String TAG = "elegion.Hotel";
    final static String URL_hotel = "https://dl.dropboxusercontent.com/u/1875854/1/${hotel_id}.json",    // ID отеля.
                        URL_img  = "https://dl.dropboxusercontent.com/u/1875854/1/${pict_id}";           // ID картинки.

    int id;
    String name,
           address,
           stars,
           suites_availability,
           image;

    Double distance,
           lat,
           lon;

    ArrayList<String> arrSuites_availability = null;

    public Hotel() {
        super();
    }

    public Hotel(Integer id, String name, String address, String stars, String suites_availability, String image,
                 double distance, double lat, double lon) {
        super();
        this.id = id;
        this.name = name;
        this.address = address;
        this.stars = stars;
        this.suites_availability = suites_availability;
        arrSuites_availability = new ArrayList<String>(Arrays.asList(suites_availability.split(":")));
        this.image = image;
        this.distance = distance;
        this.lat = lat;
        this.lon = lon;
    }

    public int getSuites_availability() {
        int res;
        if (arrSuites_availability==null)
            arrSuites_availability = new ArrayList<String>(Arrays.asList(suites_availability.split(":")));
        res = arrSuites_availability.size();
        return res;
    }

    public String toString(){
        return id + ": " + name + ": " + stars;
    }
}
