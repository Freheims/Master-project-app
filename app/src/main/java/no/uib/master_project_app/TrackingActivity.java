package no.uib.master_project_app;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Activity for tracking the user
 * @author Fredrik V. Heimsæter and Edvard P. Bjørgen
 */
public class TrackingActivity extends AppCompatActivity {


    @BindView(R.id.textView_trackingStatus) TextView textViewTrackingStatus;
    @BindView(R.id.textView_trackingTime) TextView textViewTrackingTime;
    @BindView(R.id.imageView_bluetoothLogo) ImageView imageViewBluetoothLogo;
    @BindView(R.id.textView_infotext) TextView textViewInfoText;
    @BindView(R.id.floatingActionButton_start) FloatingActionButton buttonStart;
    @BindView(R.id.floatingActionButton_exit) FloatingActionButton buttonExit;


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
        imageViewBluetoothLogo.setBackgroundColor(getResources().getColor(R.color.colorDarkerGray));
        buttonExit.setVisibility(View.INVISIBLE);
    }


     // Add listeners to buttons

    @OnClick(R.id.floatingActionButton_start)
    public void openDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(TrackingActivity.this, R.style.AppTheme)
                .setView(R.layout.dialog_new_session)
                .create();

        LayoutInflater li = LayoutInflater.from(TrackingActivity.this.getBaseContext());
        View view = li.inflate(R.layout.dialog_new_session, null);
        dialog.setView(view);


        //TODO figure out how to use ButterKnife for this
        final EditText editTextNewSessionName = (EditText) view.findViewById(R.id.editText_newSessionName);
        final EditText editTextNewSessionUser = (EditText) view.findViewById(R.id.editText_newSessionUser);
        Button buttonCancel = (Button) view.findViewById(R.id.button_dialogCancel);
        Button buttonStartSession = (Button) view.findViewById(R.id.button_dialogStartSession);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               dialog.hide();
            }
        });

        buttonStartSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sessionName = editTextNewSessionName.getText().toString();
                String sessionUser = editTextNewSessionUser.getText().toString();

                imageViewBluetoothLogo.setBackgroundColor(getResources().getColor(R.color.blue));
                textViewTrackingStatus.setText(R.string.currently_tracking);
                textViewTrackingTime.setText("00:00");
                textViewInfoText.setText(getString(R.string.infotext_session_user, sessionName));
                buttonStart.setVisibility(View.INVISIBLE);
                buttonExit.setVisibility(View.VISIBLE);

                dialog.hide();

            }
        });



        dialog.setCancelable(true);
        dialog.show();
    }
}
