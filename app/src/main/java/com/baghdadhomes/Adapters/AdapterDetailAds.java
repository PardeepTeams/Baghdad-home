package com.baghdadhomes.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.baghdadhomes.Models.Result;
import com.baghdadhomes.PreferencesService;
import com.baghdadhomes.R;

import java.util.ArrayList;
import java.util.Objects;

public class AdapterDetailAds extends RecyclerView.Adapter<AdapterDetailAds.ViewHolder> {
    private Context context;
    private openDetailPage detailPage;
    public ArrayList<Result> propertiesList =new ArrayList<>();
   /* private Boolean isMyAds;
    public Boolean isGrid;*/

    public AdapterDetailAds(Context context, openDetailPage detailPage, ArrayList<Result> propertiesList) {
        this.context = context;
        this.detailPage = detailPage;
        this.propertiesList = propertiesList;
       /* this.isMyAds = isMyAds;
        this.isGrid = isGrid;*/
    }


    protected  class ViewHolder extends RecyclerView.ViewHolder{
        ImageView img_bookmark,imv_property,img_premimum;
        TextView tv_details,tv_title,tv_area_property,tv_width,tv_bedroom,tv_batroom,tv_price,tv_number_area,tv_sell;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_bookmark = itemView.findViewById(R.id.img_bookmark);
            tv_details = itemView.findViewById(R.id.tv_details);
            imv_property = itemView.findViewById(R.id.imv_property);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_area_property = itemView.findViewById(R.id.tv_area_property);
            tv_width = itemView.findViewById(R.id.tv_width);
            tv_bedroom = itemView.findViewById(R.id.tv_bedroom);
            tv_batroom = itemView.findViewById(R.id.tv_batroom);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_number_area = itemView.findViewById(R.id.tv_number_area);
            tv_sell = itemView.findViewById(R.id.tv_sell);
            img_premimum = itemView.findViewById(R.id.img_premimum);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate( R.layout.layout_detail_ads, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
       // holder.setIsRecyclable(false);
        String land = "شقة";
        String apartments = "اراضي";
        if(propertiesList.get(position).is_premium()){
            holder.img_premimum.setVisibility(View.VISIBLE);
        }else {
            holder.img_premimum.setVisibility(View.GONE);
        }

        if (propertiesList.get(position).getFave_property_land() != null){
            holder.tv_width.setText(context.getString(R.string.street_width) + " " +  propertiesList.get(position).getFave_property_land() + context.getResources().getString(R.string.m));
        } else {
            holder.tv_width.setText(context.getString(R.string.street_width) + " 0" + context.getResources().getString(R.string.m));
        }

        if (propertiesList.get(position).getProperty_type().contains(land) || propertiesList.get(position).getProperty_type().contains(apartments) ){
            holder.tv_batroom.setVisibility(View.GONE);
            holder.tv_bedroom.setVisibility(View.GONE);
        } else {
            holder.tv_batroom.setVisibility(View.VISIBLE);
            holder.tv_bedroom.setVisibility(View.VISIBLE);
        }

        if(propertiesList.get(position).getProperty_attr()!=null){
            if(propertiesList.get(position).getProperty_attr().getProperty_status()!=null){
                holder.tv_sell.setVisibility(View.VISIBLE);
                holder.tv_sell.setText(propertiesList.get(position).getProperty_attr().getProperty_status());
            }else {
                holder.tv_sell.setVisibility(View.GONE);
            }
        }else {
            holder.tv_sell.setVisibility(View.GONE);
        }

        if(propertiesList.get(position).getThumbnail()!=null){
            String image = propertiesList.get(position).getThumbnail().toString();
            int width = holder.imv_property.getWidth();
            int height = holder.imv_property.getHeight();
            if(image!=null && !image.equals("false")){

                Glide.with(context).load(image).placeholder(R.drawable.img_placeholder)
                        .apply(new RequestOptions().override(300, 300)
                        .priority(Priority.HIGH)
                        .centerCrop().
                        diskCacheStrategy(DiskCacheStrategy.ALL)).
                        thumbnail(Glide.with(context).load(image).override(width, height)).
                        into(holder.imv_property);
            }else {
                Glide.with(context).load(R.drawable.img_placeholder).placeholder(R.drawable.img_placeholder).
                        apply(new RequestOptions().override(width, height).diskCacheStrategy(DiskCacheStrategy.ALL)).into(holder.imv_property);
            }
        }

        if(propertiesList.get(position).getPost_title()!=null){
            holder.tv_title.setText(propertiesList.get(position).getPost_title().trim());
        }

        if(propertiesList.get(position).getFave_property_bedrooms()!=null){
            holder.tv_bedroom.setText(propertiesList.get(position).getFave_property_bedrooms());
        }else {
            holder.tv_bedroom.setText("0") ;
        }
        if(propertiesList.get(position).getFave_property_bathrooms()!=null){
            holder.tv_batroom.setText(propertiesList.get(position).getFave_property_bathrooms());
        }else {
            holder.tv_batroom.setText("0") ;
        }

        if(propertiesList.get(position).getFave_property_size()!=null){
            holder.tv_number_area.setText(propertiesList.get(position).getFave_property_size()+" " +context.getResources().getString(R.string.m));
        }else {
            holder.tv_number_area.setText("00"+context.getResources().getString(R.string.m));
        }

        if(propertiesList.get(position).getPrice()!=null){
            holder.tv_price.setText("(" + propertiesList.get(position).getPrice() + ")");
        }else {
            holder.tv_price.setText("(0) ");
        }

        String currency = "";
        if (propertiesList.get(position).getFave_currency() != null && Objects.equals(propertiesList.get(position).getFave_currency(), "USD")) {
            currency = context.getString(R.string.currency_code_usd);
        } else {
            currency = context.getString(R.string.currency_code);
        }
        holder.tv_price.setText(holder.tv_price.getText() + currency);

        if(propertiesList.get(position).getPost_content()!=null){
            String html = propertiesList.get(position).getPost_content();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.tv_details.setText(Html.fromHtml(html,0));
            }
            else {
                holder.tv_details.setText(Html.fromHtml(html));
            }
        }

        if(propertiesList.get(position).getProperty_address()!=null){
            if(propertiesList.get(position).getProperty_address().getProperty_area()!=null){
                holder.tv_area_property.setVisibility(View.VISIBLE);
                holder.tv_area_property.setText(propertiesList.get(position).getProperty_address().getProperty_area());
            }else {
                holder.tv_area_property.setVisibility(View.GONE);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailPage.openNextActivity(propertiesList.get(position), position);
            }
        });

        holder.img_bookmark.setVisibility(View.VISIBLE);
        if (propertiesList.get(position).is_fav()!=null && propertiesList.get(position).is_fav() == true){
            holder.img_bookmark.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_heart));
        } else {
            holder.img_bookmark.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_heart_outline));
        }


        holder.img_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //holder.img_bookmark.setColorFilter(holder.img_bookmark.getContext().getResources().getColor(R.color.light_red), PorterDuff.Mode.SRC_ATOP);
                Boolean isLooged = PreferencesService.Companion.getInstance().getUserLoginStatus();
                if (isLooged){
                    if(propertiesList.get(position).is_fav()!=null && propertiesList.get(position).is_fav() == true){
                        holder.img_bookmark.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_heart_outline));
                    }else {
                        holder.img_bookmark.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_heart));
                    }
                    notifyItemChanged(position);
                    detailPage.addRemoveFav(propertiesList.get(position),position);
                } else {
                    detailPage.openLoginActivity();
                    //context.startActivity(new Intent(context, LoginActivity.class));
                }

            }
        });
    }

    public void updateList(ArrayList<Result> searchList){
        this.propertiesList = searchList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return propertiesList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface openDetailPage{
        void openNextActivity(Result model,int position);
        void editAd(Result model);
        void addRemoveFav(Result model,int position);
        void deleteAd(Result model,int position);
        void openLoginActivity();
    }
}
