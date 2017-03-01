package com.dius.guardian;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends WearableActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    private static final String GUARDIAN_CAPABILITY_NAME = "guardian_foo";
    public static final String GUARDIAN_MESSAGE_PATH = "/guardian_foo";

    private GoogleApiClient mGoogleApiClient;
    private String transcriptionNodeId = null;

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();


        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mButton = (Button) findViewById(R.id.okButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "button clicked");
                triggerAlert();
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    private void updateButton() {
        if (mGoogleApiClient.isConnected()) {
            mButton.setBackground(getResources().getDrawable(R.drawable.roundedbutton));
        } else {
            mButton.setBackground(getResources().getDrawable(R.drawable.roundedbuttoninactive));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        Log.i(TAG, "connected: " + String.valueOf(mGoogleApiClient.isConnected()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateButton();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    public void okClicked(View v) {
        Log.i(TAG, "button clicked");
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
//            mTextView.setTextColor(getResources().getColor(android.R.color.white));
//            mClockView.setVisibility(View.VISIBLE);

//            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            mContainerView.setBackground(null);
//            mTextView.setTextColor(getResources().getColor(android.R.color.black));
//            mClockView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "onConnected");
        updateButton();
        setupGuardianHandheldEndpoint();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended");
        updateButton();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed: " + connectionResult);
        updateButton();
    }

    private void setupGuardianHandheldEndpoint() {
        Log.i(TAG, "setupGuardianHandheldEndpoint");
//        final CapabilityApi.GetCapabilityResult result =
//                Wearable.CapabilityApi.getCapability(
//                        mGoogleApiClient, GUARDIAN_CAPABILITY_NAME,
//                        CapabilityApi.FILTER_REACHABLE).await();
//
//        updateTranscriptionCapability(result.getCapability());

        final CapabilityApi.CapabilityListener capabilityListener =
                new CapabilityApi.CapabilityListener() {
                    @Override
                    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
                        Log.i(TAG, "onCapabilityChanged: " +  capabilityInfo);
                        updateTranscriptionCapability(capabilityInfo);
                    }
                };

        Wearable.CapabilityApi.addCapabilityListener(
                mGoogleApiClient,
                capabilityListener,
                GUARDIAN_CAPABILITY_NAME);
    }

    private void updateTranscriptionCapability(CapabilityInfo capabilityInfo) {
        final Set<Node> connectedNodes = capabilityInfo.getNodes();

        transcriptionNodeId = pickBestNodeId(connectedNodes);
    }

    private String pickBestNodeId(Set<Node> nodes) {
        String bestNodeId = null;
        // Find a nearby node or pick one arbitrarily
        for (Node node : nodes) {
            if (node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }
        return bestNodeId;
    }

    private void triggerAlert() {
        requestTranscription("NOT_OK");
        Log.i(TAG, "triggerAlert");
    }

    private void requestTranscription(String data) {
        if (transcriptionNodeId != null) {
            Wearable.MessageApi.sendMessage(mGoogleApiClient, transcriptionNodeId,
                    GUARDIAN_MESSAGE_PATH, data.getBytes()).setResultCallback(
                    new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            if (!sendMessageResult.getStatus().isSuccess()) {
                                Log.e(TAG, "requestTranscription -- failed");
                            }
                            Log.i(TAG, "requestTranscription -- success");
                        }
                    }
            );
        } else {
            Log.w(TAG, "requestTranscription -- no device");
        }
    }
}
