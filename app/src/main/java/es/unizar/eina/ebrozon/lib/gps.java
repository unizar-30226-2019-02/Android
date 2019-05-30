package es.unizar.eina.ebrozon.lib;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import static android.content.Context.LOCATION_SERVICE;
import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v4.content.ContextCompat.getSystemService;
import static android.support.v4.content.ContextCompat.startActivity;

public class gps {

    private Context c;
    private Activity a;
    private int INTERVAL = 1000 * 60;
    private double lat = 0;
    private double lon = 0;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private SharedPreferences sharedpreferences;


    public gps(Context ctx, Activity act) {
        a = act;
        c = ctx;
        sharedpreferences = c.getSharedPreferences( Common.MyPreferences, c.MODE_PRIVATE );
    }


    public void init() {

        mLocationManager = (LocationManager) c.getSystemService( Context.LOCATION_SERVICE );
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString( Common.lat, Double.toString( lat ) );
                editor.putString( Common.lon, Double.toString( lon ) );
                editor.commit();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
                c.startActivity( i );
            }
        };
        configurepPermissions();


    }

    void configurepPermissions(){
        if (    ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(a, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION }
                        ,10);
            }
        }
        if (    ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(a, new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION}

                        ,10);
            }
        }
        if (    ActivityCompat.checkSelfPermission(c, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(a, new String[]{
                                Manifest.permission.INTERNET                        }
                        ,10);
            }
        }
        if (    ActivityCompat.checkSelfPermission(c, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED ){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(a, new String[]{
                                Manifest.permission.GET_ACCOUNTS
                        }
                        ,10);
            }
        }
        if (    ActivityCompat.checkSelfPermission(c, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(a, new String[]{
                                Manifest.permission.READ_CONTACTS
                        }
                        ,10);
            }
        }
        if (    ActivityCompat.checkSelfPermission(c, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(a, new String[]{
                                Manifest.permission.CAMERA,
                        }
                        ,10);
            }
        }
        if (    ActivityCompat.checkSelfPermission(c, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(a, new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        }
                        ,10);
            }
        }
        if (    ActivityCompat.checkSelfPermission(c, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(a, new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }
                        ,10);
            }
        }

        mLocationManager.requestLocationUpdates("gps", INTERVAL, 50, mLocationListener);
    }





}