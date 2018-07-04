package no.uib.master_project_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import no.uib.master_project_app.models.Beacon;
import no.uib.master_project_app.models.Datapoint;
import no.uib.master_project_app.models.Session;
import no.uib.master_project_app.adapters.SessionListViewAdapter;
import no.uib.master_project_app.models.SessionListEvent;
import no.uib.master_project_app.util.Animations;
import no.uib.master_project_app.util.ApiClient;
import no.uib.master_project_app.util.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionListActivity extends AppCompatActivity {


    @BindView(R.id.listview_session) ListView listViewSessions;
    @BindView(R.id.rellayout_select_session) RelativeLayout relLayoutSelectSession;
    @BindView(R.id.swiperefresh) SwipeRefreshLayout swipeRefreshLayout;
    SessionListViewAdapter sessionListViewAdapter;
    Context context;
    Animations anim = new Animations();
    int screenHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_list);
        ButterKnife.bind(this);
        context = getApplicationContext();

        initGui();
        getSessionsByStatus();
        initListeners();

        /*Session session = new Session("testSesh", "testuser");
        List<Session> mockSessions = new ArrayList<>();
        mockSessions.add(session);
        mockSessions.add(session);
        mockSessions.add(session);
        mockSessions.add(session);
        initListViewSessions(mockSessions);*/
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
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenHeight = size.y;

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

        relLayoutSelectSession.setTranslationY(screenHeight);

        anim.moveViewToTranslationY(relLayoutSelectSession, 500 , anim.getShortAnimTime(context), 0, false);


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
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                //TODO: Go to next activity and send the session object with it
                anim.moveViewToTranslationY(relLayoutSelectSession, 0 , anim.getShortAnimTime(context), screenHeight, false);
                final Session session = sessionListViewAdapter.getItem(i);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(context, TrackingActivity.class);
                        intent.putExtra("SessionId", session.getSessionId());
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                }, anim.getShortAnimTime(context));



            }
        });
    }
    private void initListViewSessions(List<Session> sessions) {
        Collections.reverse(sessions);

        sessionListViewAdapter = new SessionListViewAdapter(context, R.layout.list_element_session, sessions);
        listViewSessions.setAdapter(sessionListViewAdapter);

    }

}
