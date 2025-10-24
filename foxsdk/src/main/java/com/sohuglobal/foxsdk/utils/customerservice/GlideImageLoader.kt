package com.sohuglobal.foxsdk.utils.customerservice

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.qiyukf.unicorn.api.ImageLoaderListener
import com.qiyukf.unicorn.api.UnicornImageLoader
import com.sohuglobal.foxsdk.R

/**
 * 七鱼客服图片加载配置
 */

class GlideImageLoader(private val context: Context) : UnicornImageLoader {

    override fun loadImageSync(uri: String, width: Int, height: Int): Bitmap? {
        return null
    }

    override fun loadImage(uri: String, width: Int, height: Int, listener: ImageLoaderListener?) {
        val targetWidth = if (width <= 0) Integer.MIN_VALUE else width
        val targetHeight = if (height <= 0) Integer.MIN_VALUE else height
        val options = RequestOptions()
                .placeholder(R.mipmap.fs_icon_win_coin)
                .centerCrop()
                .error(R.mipmap.fs_icon_win_coin)
        Glide.with(context)
                .asBitmap()
                .load(uri)
                .apply(options)
                .into(object : CustomTarget<Bitmap>(targetWidth, targetHeight){
                    override fun onResourceReady(resource: Bitmap, p1: Transition<in Bitmap>?) {
                        listener?.onLoadComplete(resource);
                    }

                    override fun onLoadCleared(p0: Drawable?) {
                        val t = Throwable("加载异常")
                        listener!!.onLoadFailed(t)
                    }

                })
    }
}