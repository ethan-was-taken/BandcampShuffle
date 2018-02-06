package com.ford.campos.testdrawer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewArrayAdapter extends RecyclerView.Adapter<RecyclerViewArrayAdapter.MyViewHolder> {

    private String TAG = "Adapter";

    private LayoutInflater inflater;
    private ArrayList<Information> data = new ArrayList<>();
    private Context context;

    private static final int TYPE_INACTIVE = 0;
    private static final int TYPE_ACTIVE = 1;

    //1 b/c thats where the first information object thing is, 0 is where the section is
    private int oldPosition = 0;

    public RecyclerViewArrayAdapter(Context context, ArrayList<Information> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.d(TAG, "onCreateViewHolder, viewType: " + viewType);

        int layout;

        if (viewType == TYPE_INACTIVE)
            layout = R.layout.custom_row;
        else
            layout = R.layout.custom_row_active;


        View view = inflater.inflate(layout, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        Log.d(TAG, "onBindViewHolder, pos: " + position);

        Information curr = data.get(position);

        holder.textView.setText(curr.getTitle());
        holder.imageView.setImageResource(curr.getIconResourceId());

    }

    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "getItemViewType, pos: " + position);
        Information info = data.get(position);

        Log.d(TAG, "getItemViewType, info.isActive(): " + info.isActive());

        if (info.isActive())
            return TYPE_ACTIVE;
        else
            return TYPE_INACTIVE;

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setItemToActive(int position) {

        Log.d(TAG, "setItemToActive, pos: " + position);
        setItemToInactive(oldPosition);

        data.get(position).setActive(true);
        notifyItemChanged(position);
        //notifyDataSetChanged();

        oldPosition = position;

    }

    public void setItemToInactive(int position) {
        Log.d(TAG, "setItemToInactive, pos: " + position);
        data.get(position).setActive(false);
        notifyItemChanged(position);
        //notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.custom_row_text_view);
            imageView = (ImageView) itemView.findViewById(R.id.custom_row_image_view);


        }

    }

}