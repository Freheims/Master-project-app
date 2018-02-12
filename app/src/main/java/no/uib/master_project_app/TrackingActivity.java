package no.uib.master_project_app;

import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import no.uib.master_project_app.models.User;
import no.uib.master_project_app.models.Session;


/**
 * Activity for tracking the user
 * @author Fredrik V. Heimsæter and Edvard P. Bjørgen
 */
public class TrackingActivity extends AppCompatActivity {


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
        initGui();
    }

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
        final EditText editTextNewSessionName = (EditText) view.findViewById(R.id.editText_newSessionName);
        final EditText editTextNewSessionUser =  (EditText) view.findViewById(R.id.editText_newSessionUser);
        Button buttonCancelStartSession = (Button) view.findViewById(R.id.button_dialogCancelStarSession);
        Button buttonStartSession = (Button) view.findViewById(R.id.button_dialogStartSession);

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
                session = new Session(sessionName, newUser);

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
        textViewInfoText.setText(getString(R.string.infotext_session_user, newSession.getSessionPerson().getName()));

    }

    private void stopSession() {
        inSession = false;
        relativeLayoutBtIconBg.setBackground(getDrawable(R.drawable.shape_circle_gray));
        fabSession.setImageDrawable(getDrawable(R.drawable.ic_play_arrow));
        textViewTrackingStatus.setText(R.string.not_tracking);
        textViewTrackingTime.setText("00:00");
        textViewInfoText.setText(getString(R.string.infotext));
        //Todo: Write session somewhere

    }

    // Add listeners to buttons




}

