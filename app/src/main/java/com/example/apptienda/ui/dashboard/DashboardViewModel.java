package com.example.apptienda.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * DashboardViewModel
 * ViewModel sencillo que expone un texto observable usado por `DashboardFragment`.
 * Mantiene LiveData para que la UI pueda reaccionar a cambios.
 */
public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}