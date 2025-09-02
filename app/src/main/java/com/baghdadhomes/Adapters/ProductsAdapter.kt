package com.baghdadhomes.Adapters

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Activities.CityDetailActivity
import com.baghdadhomes.Activities.ProjectDetailActivity
import com.baghdadhomes.Models.ProjectData
import com.baghdadhomes.Models.Result
import com.baghdadhomes.PreferencesService.Companion.instance
import com.baghdadhomes.R
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Job

class ProductsAdapter(var context:Context,var projectList:ArrayList<ProjectData>,
    var openPage:openDetailPage):
    RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {
    private val sliderHandler = Handler(Looper.getMainLooper())
    private var sliderRunnable: Runnable? = null
    var totalCount:Int = 0
    var scrollPosition = 0
    var currentIndex = 0
    private var job: Job? = null

    class ProductsViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val title = itemView.findViewById<TextView>(R.id.tvTitle)
        val location = itemView.findViewById<TextView>(R.id.tvLocation)
        val price = itemView.findViewById<TextView>(R.id.tvPrice)
      //  val viewPager = itemView.findViewById<ViewPager2>(R.id.imageViewPager)
        val indicatorLayout = itemView.findViewById<LinearLayout>(R.id.indicatorLayout)
        val img_bookmark = itemView.findViewById<ImageView>(R.id.img_bookmark)
        val city_image = itemView.findViewById<CircleImageView>(R.id.city_image)
        val imageItem = itemView.findViewById<ImageView>(R.id.imageItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val view:View = LayoutInflater.from(context).inflate(R.layout.products_items,parent,false)
        return ProductsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return projectList.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        if(projectList.get(position).thumbnail!=null){
            Glide.with(holder.imageItem.context).load(projectList.get(position).thumbnail)
                .placeholder(R.drawable.img_placeholder).apply(
                    RequestOptions()
                        .override(600, 600) // Resize image (width x height)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)  // Cache the image for future use
                )
                .into(holder.imageItem)
        }else{
            Glide.with(context).load(R.drawable.img_placeholder). apply(
                RequestOptions()
                    .override(600, 600) // Resize image (width x height)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)// Cache the image for future use
            )
                .placeholder(R.drawable.img_placeholder).into(holder.imageItem)
        }


        holder.title.text = projectList.get(position).postTitle

        if(projectList.get(position).propertyAddress!=null){
            if(projectList.get(position).propertyAddress!!.propertyArea!=null && projectList.get(position).propertyAddress!!.propertyCity!=null){
              holder.location.text =   projectList.get(position).propertyAddress!!.propertyArea!! + " , " + projectList.get(position).propertyAddress!!.propertyCity!!
            }else if(projectList.get(position).propertyAddress!!.propertyArea!=null && projectList.get(position).propertyAddress!!.propertyCity==null){
                holder.location.text =   projectList.get(position).propertyAddress!!.propertyArea!!
            }else if(projectList.get(position).propertyAddress!!.propertyArea==null && projectList.get(position).propertyAddress!!.propertyCity!=null){
                holder.location.text =   projectList.get(position).propertyAddress!!.propertyCity!!
            }else{
                holder.location.text = ""
            }
        }else{
            holder.location.text = ""
        }

        if(projectList.get(position).price!=null){
            holder.price.text =    projectList.get(position).price  + context.resources.getString(R.string.currency_code)
        }else{
            holder.price.text = "(0) " + context.resources.getString(R.string.currency_code)
        }

        if (projectList.get(position).isFav != null && projectList.get(position).isFav == true) {
            holder.img_bookmark.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_heart
                )
            )
        } else {
            holder.img_bookmark.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_heart_outline
                )
            )
        }
        holder.city_image.setOnClickListener {
            context.startActivity(Intent(context, CityDetailActivity::class.java))

        }

        holder.itemView.setOnClickListener {
            val intent:Intent = Intent(context, ProjectDetailActivity::class.java)
             intent.putExtra("propertyId",projectList.get(position).id.toString())
            context.startActivity(intent)
        }

        holder.img_bookmark.setOnClickListener {


            //holder.img_bookmark.setColorFilter(holder.img_bookmark.getContext().getResources().getColor(R.color.light_red), PorterDuff.Mode.SRC_ATOP);
            val isLooged = instance.userLoginStatus
            if (isLooged!!) {
                if (projectList.get(position).isFav != null && projectList.get(position).isFav == true) {
                    holder.img_bookmark.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_heart_outline
                        )
                    )
                } else {
                    holder.img_bookmark.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_heart
                        )
                    )
                }
                openPage.addRemoveFav(projectList.get(position), position)
            } else {
                openPage.openLoginActivity()

            }


        }

    }


    interface openDetailPage {
        fun addRemoveFav(model: ProjectData?, position: Int)
        fun openLoginActivity()
    }
}