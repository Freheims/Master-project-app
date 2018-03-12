package no.uib.master_project_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import no.uib.master_project_app.models.Session;
import no.uib.master_project_app.adapters.SessionListViewAdapter;
import no.uib.master_project_app.models.SessionListEvent;
import no.uib.master_project_app.util.ApiClient;
import no.uib.master_project_app.util.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionListActivity extends AppCompatActivity {


    @BindView(R.id.listview_session) ListView listViewSessions;
    SessionListViewAdapter sessionListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_list);
        ButterKnife.bind(this);

        initGui();
        getSessions();
        initListeners();
    }

    private void getSessions() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Session>> call = apiService.getSessions();
        System.out.println("getSessions");
        call.enqueue(new Callback<List<Session>>() {
            @Override
            public void onResponse(Call<List<Session>> call, Response<List<Session>> response) {
                if (response.code() == 200) {
                    System.out.println(response.body());
                    //EventBus.getDefault().post(new SessionListEvent(response.body()));
                    sessionListViewAdapter = new SessionListViewAdapter(getApplicationContext(), R.layout.list_element_session, response.body());
                    listViewSessions.setAdapter(sessionListViewAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Session>> call, Throwable t) {
                System.out.println(t);

            }
        });
    }


    private void initGui() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSessionListEvent(SessionListEvent sessionListEvent){
        initListViewSessions(sessionListEvent.getSessionList());
        System.out.println("TAG: EVENTBUS, onSessionEvent fired");
    }

    private void initListeners() {
        listViewSessions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO: Go to next activity and send the session object with it
            }
        });
    }
    private void initListViewSessions(List<Session> sessions) {
        sessionListViewAdapter = new SessionListViewAdapter(getApplicationContext(), R.layout.list_element_session, sessions);
        listViewSessions.setAdapter(sessionListViewAdapter);

    }

}
