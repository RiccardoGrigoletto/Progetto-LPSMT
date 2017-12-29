package com.example.marco.progettolpsmt;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

import cn.iwgang.countdownview.CountdownView;

public class WearableActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    CountdownView cdv;
    private String courseName;
    private String argumentName;
    private String status;
    private long remainingTime;
    Button onlyButton;
    private Intent service;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_wearable);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        ((TextView) findViewById(R.id.courseTextView)).setText(courseName);
        ((TextView) findViewById(R.id.argumentTextView)).setText(argumentName);
        cdv = findViewById(R.id.countdownview);
        cdv.updateShow(remainingTime);


        service = new Intent(this, NotificationUpdateService.class);
        MyResultReceiver resultReceiver = new MyResultReceiver(null);
        service.putExtra("receiver", resultReceiver);
        startService(service);

        onlyButton = findViewById(R.id.button2);
        onlyButton.setClickable(true);
        onlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildApplicationNotification(status);
                if (status.equals("start")) {
                    onlyButton.setText("pause");
                    buildApplicationNotification("pause");
                }
                else if (status.equals("pause")){
                    onlyButton.setText("start");
                    buildApplicationNotification("start");
                }

            }
        });



    }

    private void buildApplicationNotification(final String status) {
        new Thread(new Runnable() {

            @Override
            public void run() {

                mGoogleApiClient.connect();
                if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()) {
                    //if you've put data on the remote node
                    String nodeId = getRemoteNodeId();
                    // Or If you already know the node id
                    // String nodeId = "some_node_id";
                    Uri uri = new Uri.Builder().scheme(PutDataRequest.WEAR_URI_SCHEME).authority(nodeId).path("/countdownrev").build();
                    PutDataMapRequest putDataMapRequest = PutDataMapRequest.create (uri.getPath());
                    putDataMapRequest.getDataMap().putString("status", status);
                    putDataMapRequest.getDataMap().putLong("remainingTime",cdv.getRemainTime());
                    PutDataRequest request = putDataMapRequest.asPutDataRequest();
                    request.setUrgent();
                    Wearable.DataApi.putDataItem(mGoogleApiClient,request)
                            .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                                @Override
                                public void onResult(DataApi.DataItemResult dataItemResult) {
                                    if (!dataItemResult.getStatus().isSuccess()) {
                                        Log.e("wearable", "buildWatchOnlyNotification(): Failed to set the data, "
                                                + "status: " + dataItemResult.getStatus().getStatusCode());
                                    }
                                    else {
                                        Log.v("wearable", "buildWatchOnlyNotification(): Success to set the data, "
                                                + "status: " + dataItemResult.getStatus().getStatusCode());

                                    }
                                }
                            });
                } else {
                    Log.e("wearable", "buildWearableOnlyNotification(): no Google API Client connection");
                }
            }
        }).start();
    }
    private String getLocalNodeId() {
        NodeApi.GetLocalNodeResult nodeResult = Wearable.NodeApi.getLocalNode(mGoogleApiClient).await();
        return nodeResult.getNode().getId();
    }

    private String getRemoteNodeId() {
        NodeApi.GetConnectedNodesResult nodesResult =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        List<Node> nodes = nodesResult.getNodes();
        if (nodes.size() > 0) {
            return nodes.get(0).getId();
        }
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class MyResultReceiver extends ResultReceiver
    {
        public MyResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(final int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            courseName = resultData.getString("courseName");
            argumentName = resultData.getString("argumentName");
            status = resultData.getString("status");

            remainingTime = resultData.getLong("remainingTime", 0);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TextView) findViewById(R.id.courseTextView)).setText(courseName);
                    ((TextView) findViewById(R.id.argumentTextView)).setText(argumentName);
                    cdv.updateShow(remainingTime);
                    switch (status) {
                        case "start":cdv.start(remainingTime);onlyButton.setText("pause");break;
                        case "pause":cdv.pause();onlyButton.setText("start");break;
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(service);
    }
}
