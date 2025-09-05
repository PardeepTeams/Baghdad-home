package com.baghdadhomes.Utils

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class ZoomOutPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val minScale = 0.85f
        val minAlpha = 0.5f

        when {
            position < -1 -> { // [-Infinity,-1)
                // Page is way off-screen to the left
                page.alpha = 0f
            }
            position <= 1 -> { // [-1,1]
                val scaleFactor = Math.max(minScale, 1 - Math.abs(position))
                val vertMargin = page.height * (1 - scaleFactor) / 2
                val horzMargin = page.width * (1 - scaleFactor) / 2

                if (position < 0) {
                    page.translationX = horzMargin - vertMargin / 2
                } else {
                    page.translationX = -horzMargin + vertMargin / 2
                }

                // Scale the page
                page.scaleX = scaleFactor
                page.scaleY = scaleFactor

                // Fade the page
                page.alpha = (minAlpha +
                        ((scaleFactor - minScale) / (1 - minScale)) * (1 - minAlpha))
            }
            else -> { // (1,+Infinity]
                // Page is way off-screen to the right
                page.alpha = 0f
            }
        }
    }
}
