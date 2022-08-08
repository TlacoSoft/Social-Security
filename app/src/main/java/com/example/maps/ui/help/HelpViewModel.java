package com.example.maps.ui.help;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HelpViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public HelpViewModel() {
        mText = new MutableLiveData<>();

        mText.setValue("Ayuda");

    }

    public LiveData<String> getText() {
        return mText;
    }
}