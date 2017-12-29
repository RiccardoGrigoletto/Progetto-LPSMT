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

public class NotificationService extends WearableListenerService
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

                String status = dataMap.getString("status");
                long remainingTime = dataMap.getLong("remainingTime");
                if ("/countdownrev".equals(dataEvent.getDataItem().getUri().getPath())) {
                    buildNotification(status,remainingTime);
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
            Log.e(TAG, "NotificationService(): failed to delete DataItem");
        }

    }

    private void buildNotification(String status, long remainingTime) {

        Intent intent = new Intent(this,TimerActivity.class);
        intent.putExtra("status", status);
        intent.putExtra("remainingTime", remainingTime);
        //startActivity(intent);
        resultReceiver.send(200,intent.getExtras());

    }
}
