package com.baghdadhomes.Utils

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import androidx.core.content.ContextCompat
import com.baghdadhomes.R
import pl.droidsonroids.gif.GifImageView

class ProgressHud : Dialog {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, theme: Int) : super(context, theme) {}

    companion object {
        fun show(
            context: Context, indeterminate: Boolean, cancelable: Boolean
        ): ProgressHud {
            val dialog = ProgressHud(context, R.style.ProgressHUD)
            dialog.setTitle("")
            dialog.setContentView(R.layout.progress_hud)
            var imageView_progress: GifImageView = dialog.findViewById(R.id.imageView_progress)
            imageView_progress.setColorFilter(ContextCompat.getColor(context, R.color.blue), android.graphics.PorterDuff.Mode.SRC_ATOP)

            // Glide.with(context).load(R.drawable.splash_screen_drawable).gif;
            dialog.setCancelable(cancelable)
            dialog.setCanceledOnTouchOutside(cancelable)
            //		dialog.setOnCancelListener(cancelListener);
            dialog.window!!.attributes.gravity = Gravity.CENTER
            val lp = dialog.window!!.attributes
            lp.dimAmount = 0.2f
            dialog.window!!.attributes = lp
            //dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            dialog.show()
            return dialog
        }
    }
}