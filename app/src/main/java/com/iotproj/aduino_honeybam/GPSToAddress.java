package com.iotproj.aduino_honeybam;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by marsh on 2017-12-16.
 */

public class GPSToAddress {

    public static String getAddress(Context mContext, double lat, double lng) {

        String nowAddress = "현재 위치를 확인할 수 없습니다.";
        Geocoder geocoder = new Geocoder(mContext, Locale.KOREA);
        List<Address> address;
        try {
            if (geocoder != null) {
                address = geocoder.getFromLocation(lat, lng, 1);

                if (address != null && address.size() > 0) {
                    String cut[] = address.get(0).toString().split(" ");
                    nowAddress = cut[1] + " " + cut[2];
                }
            }
        } catch (IOException e) {
            Log.e("address", "주소를 가져올 수 없습니다,", e);
            e.printStackTrace();
        }
        return nowAddress;
    }
}


