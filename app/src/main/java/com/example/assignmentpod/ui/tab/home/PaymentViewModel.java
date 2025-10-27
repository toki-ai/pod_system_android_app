package com.example.assignmentpod.ui.tab.home;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.assignmentpod.data.repository.PaymentRepository;

public class PaymentViewModel extends AndroidViewModel {
    private static final String TAG = "PaymentViewModel";

    private final PaymentRepository paymentRepository;
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> paymentUrlLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public PaymentViewModel(@NonNull Application application) {
        super(application);
        paymentRepository = new PaymentRepository();
    }

    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    public LiveData<String> getPaymentUrlLiveData() {
        return paymentUrlLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void createMoMoOrder(Context context, int amount) {
        isLoadingLiveData.setValue(true);
        paymentRepository.createMoMoOrder(context, amount, new PaymentRepository.PaymentCallback() {
            @Override
            public void onSuccess(String paymentUrl) {
                isLoadingLiveData.postValue(false);
                paymentUrlLiveData.postValue(paymentUrl);
            }

            @Override
            public void onError(String error) {
                isLoadingLiveData.postValue(false);
                errorLiveData.postValue(error);
            }
        });
    }

    public void createZaloPayOrder(Context context, int amount) {
        isLoadingLiveData.setValue(true);
        paymentRepository.createZaloPayOrder(context, amount, new PaymentRepository.PaymentCallback() {
            @Override
            public void onSuccess(String paymentUrl) {
                isLoadingLiveData.postValue(false);
                paymentUrlLiveData.postValue(paymentUrl);
            }

            @Override
            public void onError(String error) {
                isLoadingLiveData.postValue(false);
                errorLiveData.postValue(error);
            }
        });
    }
}