package no.uib.master_project_app;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
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
    @BindView(R.id.swiperefresh) SwipeRefreshLayout swipeRefreshLayout;
    SessionListViewAdapter sessionListViewAdapter;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_list);
        ButterKnife.bind(this);
        context = getApplicationContext();

        initGui();
        getSessionsByStatus();
        initListeners();
    }

    private void getSessionsByStatus() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Session>> call = apiService.getSessionsByStatus(false);
        System.out.println("getAllSessions");
        call.enqueue(new Callback<List<Session>>() {
            @Override
            public void onResponse(Call<List<Session>> call, Response<List<Session>> response) {
                if (response.code() == 200) {
                    EventBus.getDefault().post(new SessionListEvent(response.body()));
                }
            }

            @Override
            public void onFailure(Call<List<Session>> call, Throwable t) {
                System.out.println(t);

            }
        });
    }


    private void initGui() {
    /*
 * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
 * performs a swipe-to-refresh gesture.
 */
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        refreshSessions();
                    }
                }
        );
    }

    private void refreshSessions() {
        getSessionsByStatus();
        swipeRefreshLayout.setRefreshing(false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        getSessionsByStatus();

    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
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
                Session session = sessionListViewAdapter.getItem(i);
                Intent intent = new Intent(context, TrackingActivity.class);
                intent.putExtra("SessionId", session.getSessionId());
                startActivity(intent);

            }
        });
    }
    private void initListViewSessions(List<Session> sessions) {
        Collections.reverse(sessions);
        sessionListViewAdapter = new SessionListViewAdapter(context, R.layout.list_element_session, sessions);
        listViewSessions.setAdapter(sessionListViewAdapter);

    }

}
