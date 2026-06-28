package com.example.myapplication2.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import superlink.udpbind.client.UDPclient;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        String s= String.valueOf(UDPclient.userlocal.inport);
        mText.setValue(s);
    }

    public LiveData<String> getText() {
        return mText;
    }
}