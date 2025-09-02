package com.baghdadhomes.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.baghdadhomes.Models.Result;
import com.baghdadhomes.R;

import java.util.ArrayList;

public class AdapterFeatureAds extends RecyclerView.Adapter<AdapterFeatureAds.ViewHolder> {
    private Context context;
    private ArrayList<Result> feautredList = new ArrayList<Result>();
    private openFeatureDetailPage detailPage;

    public AdapterFeatureAds(Context context, ArrayList<Result> feautredList, openFeatureDetailPage detailPage) {
        this.context = context;
        this.feautredList = feautredList;
        this.detailPage = detailPage;
    }




    protected class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imv_feautred_property;
        TextView tv_featured_address;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imv_feautred_property = itemView.findViewById(R.id.imv_feautred_property);
            tv_featured_address = itemView.findViewById(R.id.tv_featured_address);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_ads_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(feautredList.get(position).getThumbnail()!=null){
            String image = feautredList.get(position).getThumbnail().toString();
            int width = holder.imv_feautred_property.getWidth();
            int height = holder.imv_feautred_property.getHeight();
            if(image!=null && !image.equals("false")){
                Glide.with(context).load(image).placeholder(R.drawable.img_placeholder).
                        apply(new RequestOptions().override(width, height)
                                .priority(Priority.HIGH)
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)).
                        thumbnail(Glide.with(context).load(image).override(width, height)).
                        into(holder.imv_feautred_property);

            }else {
                Glide.with(context).load(R.drawable.img_placeholder).
                        placeholder(R.drawable.img_placeholder).
                        apply(new RequestOptions().override(width, height).diskCacheStrategy(DiskCacheStrategy.ALL)).
                        into(holder.imv_feautred_property);

            }
        }

        if(feautredList.get(position).getProperty_address()!=null){
            if(feautredList.get(position).getProperty_address().getProperty_area()!=null){
                holder.tv_featured_address.setVisibility(View.VISIBLE);
                String feature = feautredList.get(position).getProperty_address().getProperty_area();
                holder.tv_featured_address.setText(feature.trim());
            }else {
                holder.tv_featured_address.setVisibility(View.GONE);
            }

        }else {
            holder.tv_featured_address.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailPage.openFeatureDetail(feautredList.get(position));
            }
        });

    }

    public interface openFeatureDetailPage{
        void openFeatureDetail(Result model);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return feautredList.size();
    }

}
