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
import androidx.viewpager2.widget.ViewPager2
import com.baghdadhomes.Activities.CityDetailActivity
import com.baghdadhomes.Activities.ProjectDetailActivity
import com.baghdadhomes.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProductsAdapter(var context:Context,var imageList:ArrayList<String>):
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
        return imageList.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
       /* val bannerList:ArrayList<String> = ArrayList()
        bannerList.add(imageList.get(position))*/

        Glide.with(holder.imageItem.context).load(imageList.get(position))
            .placeholder(R.drawable.img_placeholder).apply(
                RequestOptions()
                    .override(600, 600) // Resize image (width x height)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)  // Cache the image for future use
            )
            .into(holder.imageItem)

      /*  val imageAdapter = ImagePagerAdapter(bannerList)
        holder.viewPager.adapter = imageAdapter*/

        holder.city_image.setOnClickListener {
            context.startActivity(Intent(context, CityDetailActivity::class.java))

        }

        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, ProjectDetailActivity::class.java))
        }

        holder.img_bookmark.setOnClickListener {
            val currentDrawable = holder.img_bookmark.drawable
            val heart = ContextCompat.getDrawable(context, R.drawable.ic_heart)?.constantState
            val heartOutline = ContextCompat.getDrawable(context, R.drawable.ic_heart_outline)?.constantState

            if (currentDrawable?.constantState == heart) {
                // It is filled heart, switch to outline
                holder.img_bookmark.setImageResource(R.drawable.ic_heart_outline)
            } else {
                // It is outline or something else, switch to filled
                holder.img_bookmark.setImageResource(R.drawable.ic_heart)
            }



        }

   /*     holder.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentIndex = position;
                val childCount = holder.indicatorLayout.childCount
                for (i in 0 until childCount) {
                    val child = holder.indicatorLayout.getChildAt(i)
                    if (i == position) {
                        child.setBackgroundResource(R.drawable.indicator_active_white)
                    } else {
                        child.setBackgroundResource(R.drawable.indicator_inactive_grey)
                    }
                }
            }
        })*/


      /*  holder.indicatorLayout.removeAllViews()
        for (i in 0 until 3) {
            val indicator = ImageView(context)
            indicator.setLayoutParams(
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
            (indicator.layoutParams as LinearLayout.LayoutParams).setMargins(8, 0, 8, 0)
            indicator.setBackgroundResource(R.drawable.indicator_inactive_grey)
            holder. indicatorLayout.addView(indicator)
        }*/

     /*   currentIndex = position;
        val childCount = holder.indicatorLayout.childCount
        for (i in 0 until childCount) {
            val child = holder.indicatorLayout.getChildAt(i)
            if (i == position) {
                child.setBackgroundResource(R.drawable.indicator_active_white)
            } else {
                child.setBackgroundResource(R.drawable.indicator_inactive_grey)
            }
        }
        job = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(4000)
                if (currentIndex == 2){
                    currentIndex = 0
                } else {
                    currentIndex +=1
                }
               holder. viewPager.setCurrentItem(currentIndex, true)
            }
        }*/

    }

}