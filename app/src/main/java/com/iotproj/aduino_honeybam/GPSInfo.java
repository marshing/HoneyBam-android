package com.iotproj.aduino_honeybam;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by office on 2017-12-07.
 */

public class GPSInfo {
    Context mContext;

    Location returnLocation = null;

    GPSInfo(Context context) {
        mContext = context;
    }

     Location firstSetLocation() {


        // 로케이션 매니저 참조 값 할당
        final LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        // 로케이션 리스너 정의
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                returnLocation = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: 권한체크 후 권한을 요청하는 로직 샘플

//
//            ActivityCompat.requestPermissions(
//                    mContext.g,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION },
//                    1);
//            Log.d(getClass().getName(), "여기까지");
        } else {
            Log.d(getClass().getName(), "durlsms?");
            // 콜백 등록
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            // Location 객체를 반환 받음
            returnLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        // Remove the listener you previously added
        locationManager.removeUpdates(locationListener);
        return returnLocation;
    }



}
