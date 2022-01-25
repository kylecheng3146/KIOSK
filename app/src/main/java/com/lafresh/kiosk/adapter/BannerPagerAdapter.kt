package com.lafresh.kiosk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.lafresh.kiosk.R
import com.lafresh.kiosk.httprequest.model.Banners
import com.lafresh.kiosk.utils.CommonUtils

/**
 * Created by Kyle on 2021/2/2.
 */

class BannerPagerAdapter(val context: Context, val list: MutableList<Banners.Banner>) : PagerAdapter() {
    override fun getCount(): Int {
        return list.size
    }

    override fun isViewFromObject(v: View, any: Any): Boolean {
        return v == any
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.item_banner, container, false)
        val ivBanner = view.findViewById<ImageView>(R.id.iv_banner)
        CommonUtils.loadImage(context, ivBanner, list[position].image_url)
        container.addView(view) // 將 View 加進 container 中
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }
}
