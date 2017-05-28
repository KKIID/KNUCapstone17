package kr.ac.knu.bist.wheather_parse.ModuleRegist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.ac.knu.bist.wheather_parse.CardView.CardViewAdapter;
import kr.ac.knu.bist.wheather_parse.R;

/**
 * Created by BIST120 on 2017-05-28.
 */

public class moduleAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<moduleItem> items;
    private int layout;
    private Context mContext;
    public moduleAdapter(Context context, int layout, ArrayList<moduleItem> items){
        mContext = context;
        this.inflater =LayoutInflater.from(context);
        this.items = items;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public ArrayList<moduleItem> getItem(int i) {
        return items;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();
        if(view==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.module_items, viewGroup, false);
        }
        moduleItem listviewitem = items.get(position);
        ImageView icon=(ImageView)view.findViewById(R.id.moduleIcon);
        icon.setImageResource(listviewitem.getModuleIcon());

        TextView name=(TextView)view.findViewById(R.id.moduleTextView);
        name.setText(listviewitem.getMoudleName());

        return view;
    }


}
