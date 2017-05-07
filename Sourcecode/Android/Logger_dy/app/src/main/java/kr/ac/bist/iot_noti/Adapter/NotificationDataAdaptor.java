package kr.ac.bist.iot_noti.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import kr.ac.bist.iot_noti.R;

/**
 * Created by Bist on 2017-05-07.
 */

public class NotificationDataAdaptor extends RecyclerView.Adapter<NotificationDataAdaptor.ViewHolder> {

    private ArrayList<NotificationData> myDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView timeTextView;
        public TextView contentTextView;
        public ViewHolder(View view) {
            super(view);
            timeTextView = (TextView) view.findViewById(R.id.textTime);
            contentTextView = (TextView) view.findViewById(R.id.textContent);
        }
    }

    public NotificationDataAdaptor(ArrayList<NotificationData> myDataset) {
        this.myDataset = myDataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

   @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.timeTextView.setText(myDataset.get(position).textTime);
        holder.contentTextView.setText(myDataset.get(position).textContent);
    }

   @Override
    public int getItemCount() {
        return myDataset.size();
    }
}

