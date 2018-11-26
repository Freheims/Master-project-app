package no.uib.master_project_app;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.bluetooth.le.ScanFilter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import no.uib.master_project_app.models.Datapoint;
import no.uib.master_project_app.models.Ibeacon;
import no.uib.master_project_app.models.User;
import no.uib.master_project_app.models.Session;
import no.uib.master_project_app.util.AccelerometerListener;
import no.uib.master_project_app.util.AccelerometerManager;
import no.uib.master_project_app.util.Animations;
import no.uib.master_project_app.util.ApiClient;
import no.uib.master_project_app.util.ApiInterface;
import no.uib.master_project_app.util.SessionSaver;
import no.uib.master_project_app.util.UuidConverter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Activity for tracking the user
 * @author Fredrik V. Heimsæter and Edvard P. Bjørgen
 */
public class TrackingActivity extends AppCompatActivity implements AccelerometerListener, SensorEventListener {

    List<ScanFilter> filterList;
    private BluetoothAdapter mBluetoothAdapter;
    ScanSettings scanSettings;
    private boolean mScanning = false;
    private Handler mHandler = new Handler();
    private final int SCAN_PERIOD = 20000;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;
    UuidConverter uuidConv = new UuidConverter();

    @BindView(R.id.rellayout_tracking) RelativeLayout rellayoutTracking;

    @BindView(R.id.textView_trackingStatus) TextView textViewTrackingStatus;
    @BindView(R.id.textView_trackingTime) TextView textViewTrackingTime;
    @BindView(R.id.imageView_bluetoothLogo) ImageView imageViewBluetoothLogo;
    @BindView(R.id.textView_infotext_user) TextView textViewInfoText;
    @BindView(R.id.floatingActionButton_session) FloatingActionButton fabSession;
    @BindView(R.id.relLayout_btIcon_bg) RelativeLayout relativeLayoutBtIconBg;

    boolean inSession;



    private float rotX;
    private float rotY;
    private float rotZ;

    private long steps = 0;

    private SensorManager sManager;
    private Sensor stepSensor;
    private float[] smoothed = new float[3];
    private float[] gravity = new float[3];
    private boolean ignore = true;
    private int countdown = 5;
    private double threshold = 1.5;
    private double prevY;
    int currentSessionId;

    long milliSecondTime, startTime, timeBuff, updateTime = 0L ;
    Handler handler;
    int seconds, minutes, milliSeconds ;

    int screenHeight;
    Animations anim = new Animations();

    Session thisSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        ButterKnife.bind(this);
        BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        scanSettings = new ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        handler = new Handler() ;

        filterList =  new ArrayList<>();

        //keeps the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initGui();
        currentSessionId = getExtras();
        setCurrentSessionFromId();

        askForLocationPermission();
        initStepSensor();
    }

    private void setCurrentSessionFromId() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Session> call = apiService.getSessionFromId(currentSessionId);
        call.enqueue(new Callback<Session>() {
            @Override
            public void onResponse(Call<Session> call, Response<Session> response) {
                if (response.code() == 200) {
                    System.out.println(response.body());
                    //EventBus.getDefault().post(new SessionListEvent(response.body()));
                    thisSession = response.body();
                    fabSession.setEnabled(true);
                    fabSession.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(),R.color.blue)));
                }
            }

            @Override
            public void onFailure(Call<Session> call, Throwable t) {
                System.out.println(t);

            }
        });
    }


    private int getExtras() {
        Bundle extras = getIntent().getExtras();
        return extras.getInt("SessionId");
    }

    private void initStepSensor() {
        sManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (AccelerometerManager.isSupported(this) && inSession) {
            AccelerometerManager.startListening(this);

            sManager.registerListener(this, stepSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);

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

    public boolean checkIfBtIsOn(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return  false;
        } else {
            if (!mBluetoothAdapter.isEnabled()) {

                return false;
            } else {
                return true;
            }
        }

    }

    private ScanCallback mLeScanCallback = new ScanCallback() {


        //Data is stored on every update here
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            try {
                Ibeacon beacon = uuidConv.createIbeaconFromRecord(result.getScanRecord().getBytes());
                if(beacon!=null) {
                    long now = System.currentTimeMillis();
                    thisSession.addDataPoint(new Datapoint(beacon.getUuid(), Integer.toString(beacon.getMajor()), Integer.toString(beacon.getMinor()), now, result.getRssi(), steps, rotX, rotY, rotZ));
                    //Log.d("BEACON ", "RSSI: " + result.getRssi() + " UUID: " + beacon.getUuid() + " Major: " + beacon.getMajor() + " Minor: " + beacon.getMinor() + " Name: " + result.getDevice().getName());
                    System.out.println("BEACON " + "RSSI: " + result.getRssi() + " UUID: " + beacon.getUuid() + " Major: " + beacon.getMajor() + " Minor: " + beacon.getMinor() + " Name: " + result.getDevice().getName()  + " steps: " + steps);
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
            bluetoothLeScanner.startScan(filterList, scanSettings, mLeScanCallback);



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
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenHeight = size.y;

        rellayoutTracking.setTranslationY(screenHeight);

        anim.moveViewToTranslationY(rellayoutTracking, 300 , anim.getShortAnimTime(this), 0, false);

        fabSession.setEnabled(false);
        fabSession.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.red)));
    }

    @OnClick(R.id.floatingActionButton_session)
    public void performAction(){
        if (!inSession) {
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
        final EditText editTextNewSessionName = (EditText) view.findViewById(R.id.editText_newSessionName);
        final EditText editTextNewSessionUser =  (EditText) view.findViewById(R.id.editText_newSessionUser);
        editTextNewSessionName.setText(thisSession.getSessionName());
        editTextNewSessionUser.setText(thisSession.getSessionUser());
        editTextNewSessionName.setEnabled(false);
        editTextNewSessionUser.setEnabled(false);


        Button buttonCancelStartSession = (Button) view.findViewById(R.id.button_dialogCancelStarSession);
        Button buttonStartSession = (Button) view.findViewById(R.id.button_dialogStartSession);

        dialog.setCancelable(true);
        dialog.show();

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

                if(checkIfBtIsOn()){

                    User newUser = new User(sessionUser);
                    //sessionFromDb = new Session(sessionName, newUser.getName());

                    inSession = true;
                    startSession(thisSession);
                    dialog.cancel();

                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth not activated", Toast.LENGTH_SHORT).show();

                    dialog.cancel();
                }

            }
        });




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
                String jsonSession = stopSession();
                dialog.cancel();
                openUploadDialog(jsonSession);


            }
        });



        dialog.setCancelable(true);
        dialog.show();
    }
    public void openUploadDialog(String jsonSession) {
        final AlertDialog dialog = new AlertDialog.Builder(TrackingActivity.this)
                .setView(R.layout.dialog_upload_session)
                .create();

        LayoutInflater li = LayoutInflater.from(TrackingActivity.this.getBaseContext());
        View view = li.inflate(R.layout.dialog_upload_session, null);
        dialog.setView(view);
        //TODO figure out how to use ButterKnife for this
        final Button buttonFinishSession = (Button) view.findViewById(R.id.button_dialogFinishSession);
        final Button buttonRetryUploadSession = (Button) view.findViewById(R.id.button_dialogRetryUploadSession);
        final Button buttonAbortUploadSession = (Button) view.findViewById(R.id.button_dialogAbortUploadSession);
        final TextView textSessionHasEnded = (TextView) view.findViewById(R.id.text_sessionHasEnded);
        final TextView textUploadStatus = (TextView) view.findViewById(R.id.text_uploadStatus);
        final ProgressBar progressUploadSession = (ProgressBar) view.findViewById(R.id.progress_uploadSession);
        final ImageView imageUploadCheck = (ImageView) view.findViewById(R.id.image_uploadCheck);
        final ImageView imageUploadFailed = (ImageView) view.findViewById(R.id.image_uploadFalse);

        textSessionHasEnded.setText(getResources().getString(R.string.session)  + " " + thisSession.getSessionName() + " " + getResources().getString(R.string.has_ended));

        updateSession(dialog, buttonFinishSession, buttonRetryUploadSession, buttonAbortUploadSession, textUploadStatus, progressUploadSession, imageUploadCheck, imageUploadFailed);



        dialog.setCancelable(false);
        dialog.show();
    }


    private void updateSession(final AlertDialog dialog, final Button buttonFinishSession, final Button buttonRetryUploadSession, final Button buttonAbortUploadSession, final TextView textUploadStatus, final ProgressBar progressUploadSession, final ImageView imageUploadCheck, final ImageView imageUploadFailed) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        thisSession.setFinished(true);
        Call<Void> call = apiService.updateSession(thisSession, currentSessionId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200){
                setUiState(true);}
                else {
                    System.out.println(response.code());
                    setUiState(false);
                }
            }

            private void setUiState(boolean uploaded) {
                progressUploadSession.setVisibility(View.GONE);
                if(uploaded) {
                    imageUploadFailed.setVisibility(View.GONE);
                    buttonRetryUploadSession.setEnabled(false);
                    buttonRetryUploadSession.setVisibility(View.GONE);
                    buttonAbortUploadSession.setEnabled(false);
                    buttonAbortUploadSession.setVisibility(View.GONE);
                    imageUploadCheck.setVisibility(View.VISIBLE);

                    textUploadStatus.setText(getResources().getString(R.string.upload_finished));
                    buttonFinishSession.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.blue));
                    buttonFinishSession.setVisibility(View.VISIBLE);
                    buttonFinishSession.setEnabled(true);
                } else{
                    imageUploadFailed.setVisibility(View.VISIBLE);
                    textUploadStatus.setText(R.string.upload_failed);
                    buttonFinishSession.setVisibility(View.GONE);
                    buttonRetryUploadSession.setVisibility(View.VISIBLE);
                    buttonRetryUploadSession.setEnabled(true);
                    buttonAbortUploadSession.setEnabled(true);
                    buttonAbortUploadSession.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println(t);
                System.out.println("call:" +call);
                setUiState(false);
            }
        });

        buttonFinishSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                Intent intent = new Intent(getApplicationContext(), SessionListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        buttonRetryUploadSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                updateSession(dialog, buttonFinishSession, buttonRetryUploadSession, buttonAbortUploadSession, textUploadStatus, progressUploadSession, imageUploadCheck, imageUploadFailed);
                dialog.show();
            }
        });

        buttonAbortUploadSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Implement save functionality
                SessionSaver.saveSessionInSharedPreferences(getApplicationContext(), thisSession);
                dialog.cancel();
                Intent intent = new Intent(getApplicationContext(), SessionListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    private void startSession(Session newSession) {
        inSession = true;

        relativeLayoutBtIconBg.setBackground(getDrawable(R.drawable.shape_circle_blue));
        fabSession.setImageDrawable(getDrawable(R.drawable.ic_stop));
        textViewTrackingStatus.setText(R.string.currently_tracking);
        textViewInfoText.setText(getString(R.string.infotext_session_user, newSession.getSessionUser()));
        //sessionFromDb = newSession;

        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);
        newSession.setSessionStart(System.currentTimeMillis());

        if (AccelerometerManager.isSupported(this) && sManager != null) {
            AccelerometerManager.startListening(this);
            sManager.registerListener(this, stepSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        mHandler.post(scanRunnable);

    }

    private String stopSession() {
        inSession = false;
        relativeLayoutBtIconBg.setBackground(getDrawable(R.drawable.shape_circle_gray));
        fabSession.setImageDrawable(getDrawable(R.drawable.ic_play_arrow));
        textViewTrackingStatus.setText(R.string.not_tracking);
        textViewInfoText.setText(getString(R.string.infotext));
        mHandler.removeCallbacks(scanRunnable);
        scanLeDevice(false);
        handler.removeCallbacks(runnable);


        if (AccelerometerManager.isListening() && sManager != null) {
            AccelerometerManager.stopListening();
            sManager.unregisterListener(this, stepSensor);
        }
        long endTime = System.currentTimeMillis();
        thisSession.setSessionEnd(endTime);
        //Todo: Write sessionFromDb somewhere, for now we only log it
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(thisSession);
        System.out.print(jsonOutput);
        steps = 0;
        return jsonOutput;

    }
    public Runnable runnable = new Runnable() {

        public void run() {

            milliSecondTime = SystemClock.uptimeMillis() - startTime;

            updateTime = timeBuff + milliSecondTime;

            seconds = (int) (updateTime / 1000);

            minutes = seconds / 60;

            seconds = seconds % 60;

            milliSeconds = (int) (updateTime % 1000);

            textViewTrackingTime.setText("" + minutes + ":"
                    + String.format("%02d", seconds));

            handler.postDelayed(this, 0);
        }

    };

    @Override
    public void onAccelerationChanged(float x, float y, float z) {
        rotX = x;
        rotY = y;
        rotZ = z;

       // DecimalFormat df = new DecimalFormat("#.00");
        //textViewTrackingTime.setText("ACCEL CHANGED: X " + df.format(x) + "| Y " + df.format(y) + "| Z " + df.format(z));

    }

    protected float[] lowPassFilter( float[] input, float[] output ) {
        if ( output == null ) return input;
        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + 1.0f * (input[i] - output[i]);
        }
        return output;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {


        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && inSession) {

            // we need to use a low pass filter to make data smoothed
            smoothed = lowPassFilter(event.values, gravity);
            gravity[0] = smoothed[0];
            gravity[1] = smoothed[1];
            gravity[2] = smoothed[2];
            if(ignore) {
                countdown--;
                ignore = (countdown < 0)? false : ignore;

            }
            else
                countdown = 22;
            if((Math.abs(prevY - gravity[1]) > threshold) && !ignore){
                steps++;
                textViewTrackingStatus.setText(getString(R.string.stepcount) + " " + steps);

                ignore = true;
            }
            prevY = gravity[1];
        }
    }


    @Override
    public void onStop() {
        super.onStop();

        //Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isListening() && sManager != null) {

            //Start Accelerometer Listening
            AccelerometerManager.stopListening();
            sManager.unregisterListener(this, stepSensor);

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (AccelerometerManager.isListening() && sManager != null) {
            AccelerometerManager.stopListening();
            sManager.unregisterListener(this, stepSensor);

        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {}

    public void onBackPressed() {
        anim.moveViewToTranslationY(rellayoutTracking, 0 , anim.getShortAnimTime(this), screenHeight, false);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                TrackingActivity.super.onBackPressed();

                overridePendingTransition(0, 0);

            }
        }, anim.getShortAnimTime(this));

    }
}


