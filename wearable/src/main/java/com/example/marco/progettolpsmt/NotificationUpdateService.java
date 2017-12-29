package com.example.marco.progettolpsmt;

import android.content.Intent;
import android.os.Bundle;
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

/**
 * Created by ricca on 28/12/2017.
 */

public class NotificationUpdateService extends WearableListenerService
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<DataApi.DeleteDataItemsResult> {
    private static final String TAG = "NotificationUpdate";
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("DIOAND","onCreate()");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("DIOAND","onCreate()");

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        Log.d("DIOAND","onDataChanged()");
        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();
                String content = dataMap.getString("content");
                if ("/start".equals(dataEvent.getDataItem().getUri().getPath())) {
                    buildWearableNotification(content);
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
    public void onConnected(Bundle bundle) {        Log.d("DIOAND","onCreate()");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("DIOAND","onCreate()");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("DIOAND","onCreate()");
    }

    @Override
    public void onResult(DataApi.DeleteDataItemsResult deleteDataItemsResult) {
        if (!deleteDataItemsResult.getStatus().isSuccess()) {
            Log.e(TAG, "dismissWearableNotification(): failed to delete DataItem");
        }
        Log.d("DIOAND","onCreate()");
        buildWearableNotification("CIAONE");

    }

    private void buildWearableNotification(String content) {
        Log.d("DIOAND","onCreate()");

        Intent intent = new Intent(this,WearableActivity.class);
        intent.putExtra("courseName",content);
        startActivity(intent);
    }
}
