package no.uib.master_project_app;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import no.uib.master_project_app.models.Datapoint;
import no.uib.master_project_app.models.Ibeacon;
import no.uib.master_project_app.models.User;
import no.uib.master_project_app.models.Session;
import no.uib.master_project_app.util.AccelerometerListener;
import no.uib.master_project_app.util.AccelerometerManager;
import no.uib.master_project_app.util.UuidConverter;


/**
 * Activity for tracking the user
 * @author Fredrik V. Heimsæter and Edvard P. Bjørgen
 */
public class TrackingActivity extends AppCompatActivity implements AccelerometerListener {

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning = false;
    private Handler mHandler = new Handler();
    private final int SCAN_PERIOD = 20000;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;
    UuidConverter uuidConv = new UuidConverter();


    @BindView(R.id.textView_trackingStatus) TextView textViewTrackingStatus;
    @BindView(R.id.textView_trackingTime) TextView textViewTrackingTime;
    @BindView(R.id.imageView_bluetoothLogo) ImageView imageViewBluetoothLogo;
    @BindView(R.id.textView_infotext_user) TextView textViewInfoText;
    @BindView(R.id.floatingActionButton_session) FloatingActionButton fabSession;
    @BindView(R.id.relLayout_btIcon_bg) RelativeLayout relativeLayoutBtIconBg;

    boolean inSession;

    Session session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        ButterKnife.bind(this);
        BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        askForLocationPermission();

        initGui();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
    }

    private void askForLocationPermission() {

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(TrackingActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(TrackingActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(TrackingActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);


                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
        }

    }


    private ScanCallback mLeScanCallback = new ScanCallback() {



        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            try {
                Ibeacon beacon = uuidConv.createIbeaconFromRecord(result.getScanRecord().getBytes());
                if(beacon!=null) {
                    long now = System.currentTimeMillis();
                    session.addDataPoint(new Datapoint(beacon.getUuid(), beacon.getMajor(), beacon.getMinor(), now, result.getRssi()));
                    Log.d("BEACON ", "RSSI: " + result.getRssi() + " UUID: " + beacon.getUuid() + " Major: " + beacon.getMajor() + " Minor: " + beacon.getMinor() + " Name: " + result.getDevice().getName());
                }

            } catch (NullPointerException e){
                Log.d("BLE Devices", "Name " + result.getDevice().getName() + " RSSI " + result.getRssi() + " Class: " + result.getDevice().getBluetoothClass());

            }

            super.onScanResult(callbackType, result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.d("CallBack", "BatchScanResult");
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.d("CallBack", "ScanFailed");
            super.onScanFailed(errorCode);
        }
    };

    private void scanLeDevice(final boolean enable) {

        final BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            /*mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothLeScanner.stopScan(mLeScanCallback);
                    btnStartLeScan.setText("Scan for BLE devices");
                }
            }, SCAN_PERIOD);*/
            mScanning = true;
            bluetoothLeScanner.startScan(mLeScanCallback);



        } else {
            mScanning = false;
            bluetoothLeScanner.stopScan(mLeScanCallback);
        }
    }

    private Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            scanLeDevice(true);
        }

    };

    /**
     * Initializes the GUI
     */
    public void initGui() {
    }

    @OnClick(R.id.floatingActionButton_session)
    public void performAction(){
        if (!inSession) {
            inSession = true;
            openStartSessionDialog();
        } else if (inSession) {
            openStopSessionDialog();
        }
    }

    public void openStartSessionDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(TrackingActivity.this)
                .setView(R.layout.dialog_new_session)
                .create();

        LayoutInflater li = LayoutInflater.from(TrackingActivity.this.getBaseContext());
        View view = li.inflate(R.layout.dialog_new_session, null);
        dialog.setView(view);


        //TODO figure out how to use ButterKnife for this
        final EditText editTextNewSessionName = (EditText) view.
                findViewById(R.id.editText_newSessionName);
        final EditText editTextNewSessionUser =  (EditText) view.findViewById(R.id.editText_newSessionUser);
        Button buttonCancelStartSession = (Button) view.findViewById(R.id.button_dialogCancelStarSession);
        Button buttonStartSession = (Button) view.findViewById(R.id.button_dialogStartSession);


        //FOR TESTING ONLY
        Random random = new Random();
        editTextNewSessionName.setText("TestSession" + random.nextInt());
        editTextNewSessionUser.setText("TestUser" + random.nextInt());

        buttonCancelStartSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        buttonStartSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sessionName = editTextNewSessionName.getText().toString();
                String sessionUser = editTextNewSessionUser.getText().toString();

                User newUser = new User(sessionUser);
                session = new Session(sessionName, newUser.getName());

                startSession(session);
                dialog.cancel();

            }
        });



        dialog.setCancelable(true);
        dialog.show();
    }
    public void openStopSessionDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(TrackingActivity.this)
                .setView(R.layout.dialog_stop_session)
                .create();

        LayoutInflater li = LayoutInflater.from(TrackingActivity.this.getBaseContext());
        View view = li.inflate(R.layout.dialog_stop_session, null);
        dialog.setView(view);


        //TODO figure out how to use ButterKnife for this
        Button buttonCancelStopSession = (Button) view.findViewById(R.id.button_dialogCancelStopSession);
        Button buttonStopSession = (Button) view.findViewById(R.id.button_dialogStopSession);

        buttonCancelStopSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();

            }
        });

        buttonStopSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSession();
                dialog.cancel();
                openUploadDialog();


            }
        });



        dialog.setCancelable(true);
        dialog.show();
    }
    public void openUploadDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(TrackingActivity.this)
                .setView(R.layout.dialog_upload_session)
                .create();

        LayoutInflater li = LayoutInflater.from(TrackingActivity.this.getBaseContext());
        View view = li.inflate(R.layout.dialog_upload_session, null);
        dialog.setView(view);
        //TODO figure out how to use ButterKnife for this
        final Button buttonFinishSession = (Button) view.findViewById(R.id.button_dialogFinishSession);
        final TextView textSessionHasEnded = (TextView) view.findViewById(R.id.text_sessionHasEnded);
        final TextView textUploadStatus = (TextView) view.findViewById(R.id.text_uploadStatus);
        final ProgressBar progressUploadSession = (ProgressBar) view.findViewById(R.id.progress_uploadSession);
        final ImageView imageUploadCheck = (ImageView) view.findViewById(R.id.image_uploadCheck);

        textSessionHasEnded.setText(getResources().getString(R.string.session)  + " " + session.getSessionName() + " " + getResources().getString(R.string.has_ended));

        //Only for mocking the GUI
        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            //When "upload" finishes, this happens
            public void onFinish() {
                progressUploadSession.setVisibility(View.GONE);
                imageUploadCheck.setVisibility(View.VISIBLE);

                textUploadStatus.setText(getResources().getString(R.string.upload_finished));
                buttonFinishSession.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.blue));
                buttonFinishSession.setEnabled(true);

            }
        }.start();


        buttonFinishSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();


            }
        });



        dialog.setCancelable(true);
        dialog.show();
    }

    private void startSession(Session newSession) {
        inSession = true;

        relativeLayoutBtIconBg.setBackground(getDrawable(R.drawable.shape_circle_blue));
        fabSession.setImageDrawable(getDrawable(R.drawable.ic_stop));
        textViewTrackingStatus.setText(R.string.currently_tracking);
        textViewTrackingTime.setText("00:00");
        textViewInfoText.setText(getString(R.string.infotext_session_user, newSession.getSessionPerson()));
        session = newSession;
        long startTime = System.currentTimeMillis();
        session.setSessionStart(startTime);
        mHandler.post(scanRunnable);

    }

    private void stopSession() {
        inSession = false;
        relativeLayoutBtIconBg.setBackground(getDrawable(R.drawable.shape_circle_gray));
        fabSession.setImageDrawable(getDrawable(R.drawable.ic_play_arrow));
        textViewTrackingStatus.setText(R.string.not_tracking);
        textViewTrackingTime.setText("00:00");
        textViewInfoText.setText(getString(R.string.infotext));
        mHandler.removeCallbacks(scanRunnable);
        scanLeDevice(false);
        long endTime = System.currentTimeMillis();
        session.setSessionEnd(endTime);
        //Todo: Write session somewhere, for now we only log it
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(session);
        System.out.print(jsonOutput);

    }

    @Override
    public void onAccelerationChanged(float x, float y, float z) {
        System.out.println("value x: "+ x + " value y: " + y + " value z: " + z);
    }

    @Override
    public void onShake(float force) {
        Toast.makeText(this, "Motion detected", Toast.LENGTH_SHORT).show();
        System.out.println("value force: " + (int) force);

    }




    @Override
    public void onStop() {
        super.onStop();

        //Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isListening()) {

            //Start Accelerometer Listening
            AccelerometerManager.stopListening();

            Toast.makeText(this, "onStop Accelerometer Stopped", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();

            Toast.makeText(this, "onDestroy Accelerometer Stopped", Toast.LENGTH_SHORT).show();
        }
    }

}


