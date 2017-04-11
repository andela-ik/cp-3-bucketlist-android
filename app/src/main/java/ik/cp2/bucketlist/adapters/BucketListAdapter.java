package ik.cp2.bucketlist.adapters;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ik.cp2.bucketlist.MainActivity;
import ik.cp2.bucketlist.R;

/**
 * Created by sycko on 16/03/2017.
 */

public class BucketListAdapter extends RecyclerView.Adapter<BucketListAdapter.ViewHolder> implements View.OnClickListener {

    private JSONObject mDataset;
    private JSONArray bucketlists;
    public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public Date date = new Date();
    ViewHolder vh;



    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public TextView created;
        public TextView bucket_id;
        public CardView bucket;
        public ViewHolder(View v) {
            super(v);

            name =  (TextView) v.findViewById(R.id.bucket_list_name);
            created =  (TextView) v.findViewById(R.id.bucket_created);
            bucket_id =  (TextView) v.findViewById(R.id.bucket_id);
            bucket = (CardView) v.findViewById(R.id.card_view);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public BucketListAdapter(JSONObject myDataset) {
        mDataset = myDataset;
        try {
            bucketlists= mDataset.getJSONArray("bucketlists");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BucketListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bucket_list_card, parent, false);
        // set the view's size, margins, paddings and layout parameters
        vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        try {
            JSONObject bucketlist=  bucketlists.getJSONObject(position);
            String name = bucketlist.getString("name");
            String created = bucketlist.getString("date_created");
            String bucketId = bucketlist.getString("id");
            holder.name.setText(name);
            holder.bucket_id.setText(bucketId);
            holder.bucket.setOnClickListener(this);
            try {
                Long timediff=date.getTime()-dateFormat.parse(created).getTime();

                // Get time difference since date created
                holder.created.setText(String.valueOf(timediff/(24 * 60 * 60 * 1000))+ " day(s) ago");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    // Return the size of the data set
    @Override
    public int getItemCount() {
        return bucketlists.length();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.card_view:
                TextView id = (TextView) v.findViewById(R.id.bucket_id);
                Toast.makeText(v.getContext(),id.getText().toString(),Toast.LENGTH_SHORT).show();
        }

    }
}

