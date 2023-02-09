package com.example.gpstracking;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {

    private List<TrackerLocations> mlist;

    private OnItemClickListener listener;


    public LocationsAdapter(List<TrackerLocations> mlist, OnItemClickListener listener) {
        this.mlist = mlist;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View locView = inflater.inflate(R.layout.location, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(locView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        holder.bind( mlist.get(position),listener );

        // Get the data model based on position
        TrackerLocations locations = mlist.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.id;
        textView.setText(locations.getId());

        TextView locView = holder.loc;
        locView.setText( locations.getCoordinates());

    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public interface OnItemClickListener {
        void onItemClick(TrackerLocations trackerLocations);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView id;
        public TextView loc;
        public ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super( itemView );
            id = (TextView) itemView.findViewById( R.id.num );
            loc = (TextView) itemView.findViewById( R.id.loc );
            img = (ImageView)  itemView.findViewById( R.id.img );

        }

        private void bind(final TrackerLocations locations,final OnItemClickListener listener){

            itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick( locations );
                }
            } );

        }
    }
}