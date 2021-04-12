package com.szu.trashsorting.user.location;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LocationViewModel extends ViewModel {

    private final MutableLiveData<String> mGPSCity = new MutableLiveData<>();
    private final MutableLiveData<String> mGPSAddress = new MutableLiveData<>();

    public void postGPSCity(String city){
        mGPSCity.postValue(city);
    }
    public void postGPSAddress(String address){
        mGPSAddress.postValue(address);
    }

    public MutableLiveData<String> getGPSCity(){
        return mGPSCity;
    }
    public MutableLiveData<String> getGPSAddress(){
        return mGPSAddress;
    }

}