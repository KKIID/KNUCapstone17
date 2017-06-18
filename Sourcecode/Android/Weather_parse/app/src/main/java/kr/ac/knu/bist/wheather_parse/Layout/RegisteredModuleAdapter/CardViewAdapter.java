package kr.ac.knu.bist.wheather_parse.Layout.RegisteredModuleAdapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.ac.knu.bist.wheather_parse.Data.CardViewData;
import kr.ac.knu.bist.wheather_parse.R;

/**
 * Created by BIST120 on 2017-05-26.
 */

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {

    private ArrayList<CardViewData> mDataset;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mDataset.get(position).text);
        holder.mImageView.setImageResource(mDataset.get(position).img);
        holder.itemView.setOnClickListener(mDataset.get(position).listner);
        holder.itemView.setOnLongClickListener(mDataset.get(position).longlistener);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.card_image);
            mTextView = (TextView) view.findViewById(R.id.card_text);
        }
    }

    public CardViewAdapter(ArrayList<CardViewData> myDataset) {

        mDataset = myDataset;
    }
}