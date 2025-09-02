package com.baghdadhomes.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.baghdadhomes.Models.ImageData;
import com.baghdadhomes.R;

import java.util.ArrayList;


public class AdapterPropertyImages extends RecyclerView.Adapter<AdapterPropertyImages.ViewHolder> {
    private Context context;
    ArrayList<ImageData> imageList = new ArrayList<ImageData>();
    private InterfaceSelectImage selectImage;

    public AdapterPropertyImages(Context context, ArrayList<ImageData> imageList, InterfaceSelectImage selectImage) {
        this.context = context;
        this.imageList = imageList;
        this.selectImage = selectImage;
    }


    protected class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout el_main;
        ImageView image_property,imv_remove;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            el_main = itemView.findViewById(R.id.el_main);
            image_property = itemView.findViewById(R.id.image_property);
            imv_remove = itemView.findViewById(R.id.imv_remove);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_upload_images,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        int width = holder.image_property.getWidth();
        int height = holder.image_property.getHeight();
        if(imageList.get(position).getUrl()!=null && !imageList.get(position).getUrl().isEmpty()){
            try {
                if(imageList.get(position).getUrl()!=null   ){

                    try{
                        Glide.with(context).
                                load(imageList.get(position).getUrl()).
                                placeholder(R.drawable.img_placeholder).
                                  apply(new RequestOptions().override(width, height)
                                          .priority(Priority.HIGH)
                                          .centerCrop()
                                          .diskCacheStrategy(DiskCacheStrategy.ALL)).
                                thumbnail(Glide.with(context).load(imageList.get(position).getUrl()).override(width, height)).
                                into(holder.image_property);
                    }catch (Exception e){
                        Log.d("exception",e.toString());
                    }
                  //  Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver() , Uri.parse(String.valueOf(Uri.parse(imageList.get(position).getUri()))));
                 //   holder.image_property.setImageBitmap(bitmap);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                //handle exception
            }

        }else {
            holder.image_property.setImageDrawable(context.getDrawable(R.drawable.ic_upload_images));
        }
        holder.imv_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage.onSelectImage(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public interface InterfaceSelectImage{
        public void onSelectImage(int position);
    }
}



/*public class AdapterPropertyImages extends RecyclerView.Adapter<AdapterPropertyImages.ViewHolder>
{
    private Context context;

    public AdapterPropertyImages(Context context) {
        this.context = context;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout el_main;
        ImageView image_property;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            el_main = itemView.findViewById(R.id.el_main);
            image_property = itemView.findViewById(R.id.image_property);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_upload_images,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    interface uploadImage{
        void uploadImage();
    }
}*/
