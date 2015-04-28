package hu.uniobuda.nik.felhasznaloi_fiuk;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Barnaby-Laptop on 2015.04.28..
 */
public class ParcelClass implements Parcelable {


    private Communication communicator;

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.write

    }
}
