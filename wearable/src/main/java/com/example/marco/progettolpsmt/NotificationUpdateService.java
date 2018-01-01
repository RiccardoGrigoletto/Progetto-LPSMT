package com.example.marco.progettolpsmt;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.Date;

/**
 * Created by ricca on 28/12/2017.
 */

public class NotificationUpdateService extends WearableListenerService
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<DataApi.DeleteDataItemsResult> {
    private static final String TAG = "NotificationUpdate";
    private GoogleApiClient mGoogleApiClient;
    private ResultReceiver resultReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        resultReceiver = intent.getParcelableExtra("receiver");


        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();
                String courseName = dataMap.getString("courseName");
                String argumentName = dataMap.getString("argumentName");
                String status = dataMap.getString("status");
                long remainingTime = dataMap.getLong("remainingTime");
                if ("/countdown".equals(dataEvent.getDataItem().getUri().getPath())) {
                    buildWearableNotification(courseName, argumentName, remainingTime,status);
                }
                else if (dataEvent.getType() == DataEvent.TYPE_DELETED) {
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "DataItem deleted: " + dataEvent.getDataItem().getUri().getPath());
                    }
                }
            }
        }
    }
    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {    }

    @Override
    public void onResult(DataApi.DeleteDataItemsResult deleteDataItemsResult) {
        if (!deleteDataItemsResult.getStatus().isSuccess()) {
            Log.e(TAG, "dismissWearableNotification(): failed to delete DataItem");
        }

    }

    private void buildWearableNotification(String courseName, String argumentName, long remainingTime, String status) {

        Intent intent = new Intent(this,WearableActivity.class);
        intent.putExtra("courseName",courseName);
        intent.putExtra("argumentName", argumentName);
        intent.putExtra("remainingTime", remainingTime);
        intent.putExtra("status", status);
        intent.putExtra("now",(new Date()).getTime());
        if (resultReceiver == null) {
            startActivity(intent);
        }
        else {
            resultReceiver.send(100, intent.getExtras());
        }
    }
}
