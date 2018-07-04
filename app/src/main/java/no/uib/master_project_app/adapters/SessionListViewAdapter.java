package no.uib.master_project_app.adapters;

/**
 * Created by tacobabe on 12.03.2018.
 */

import android.content.Context;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import no.uib.master_project_app.R;
import no.uib.master_project_app.models.Session;

/**
 * Custom adapter for populating ListViews with beacons.
 *
 * @author Edvard Pires Bjørgen
 *
 **/

public class SessionListViewAdapter extends ArrayAdapter<Session> {

    private final Context context;
    private final int textViewResourceId;
    private final List<Session> sessions;
    LayoutInflater inflater;

    public SessionListViewAdapter(Context context, int textViewResourceId, List<Session> sessions) {
        super(context, textViewResourceId, sessions);
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.textViewResourceId = textViewResourceId;
        this.sessions = sessions;

    }

    public int getCount() {
        return sessions.size();
    }
    public Session getItem(int position) {
        return sessions.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View row = convertView;
        UserHolder holder = null;

        if (row == null) {
            holder = new UserHolder();
            row = inflater.inflate(textViewResourceId, parent, false);


            holder.imgViewSessionMap = (ImageView) row.findViewById(R.id.imgview_session_map_img);
            holder.textViewSessionName = (TextView) row.findViewById(R.id.textview_session_name);
            holder.textViewSessionUser = (TextView) row.findViewById(R.id.textview_session_user);
            //holder.textViewDateTime = (TextView) row.findViewById(R.id.textview_date_time);

            row.setTag(holder);
        } else {

            holder = (UserHolder) row.getTag();
        }


        holder.textViewSessionName.setText("Session name: " + sessions.get(position).getSessionName());
        holder.textViewSessionUser.setText("Session user: " + sessions.get(position).getSessionUser());
        //holder.textViewDateTime.setText("Time: " + sessions.get(position).getSessionStart());
        final Transformation transformation = new RoundedCornersTransformation(35, 0);
        Picasso.get()
                .load("http://"+sessions.get(position).getMapUrl())
                .error(R.mipmap.ic_firetracker)
                .fit()
                .centerCrop()
                .transform(transformation)
                .into(holder.imgViewSessionMap);
        System.out.println("mapurl:" +sessions.get(position).getMapUrl());

        return row;
    }

    static class UserHolder {
        TextView textViewSessionName;
        TextView textViewSessionUser;
        ImageView imgViewSessionMap;
        //TextView textViewDateTime;
    }

}
