package com.photo.editor.snapstudio.collage.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.photo.editor.snapstudio.collage.AndroidUtils
import com.photo.editor.snapstudio.collage.model.GalleryAlbum
import com.photo.editor.snapstudio.R


class GalleryAlbumAdapter(
    context: Context,
    list: List<GalleryAlbum>,
    var mListener: OnGalleryAlbumClickListener?
) : ArrayAdapter<GalleryAlbum>(context, R.layout.item_gallery_album, list) {

    var mContext = context

    interface OnGalleryAlbumClickListener {
        fun onGalleryAlbumClick(galleryAlbum: GalleryAlbum?)
    }

    inner class ViewHolder {
        internal var descriptionView: TextView? = null
        internal var itemCountView: TextView? = null
        internal var thumbnailView: ImageView? = null
        internal var titleView: TextView? = null
    }

    @SuppressLint("ResourceAsColor")
    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        var view = view
        val viewHolder: ViewHolder?
        if (view == null) {
            view =
                LayoutInflater.from(mContext).inflate(R.layout.item_gallery_album, viewGroup, false)
            viewHolder = ViewHolder()
            viewHolder.thumbnailView = view!!.findViewById(R.id.thumbnailView) as ImageView
            viewHolder.titleView = view.findViewById(R.id.titleView) as TextView
            viewHolder.itemCountView = view.findViewById(R.id.itemCountView) as TextView
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        val galleryAlbum = getItem(i)
        if (galleryAlbum != null) {
            if (galleryAlbum.mImageList.size > 0) {
                AndroidUtils.loadImageWithGlide(
                    context,
                    viewHolder.thumbnailView!!,
                    galleryAlbum.mImageList[0] as String
                )
            } else {
                viewHolder.thumbnailView!!.setImageBitmap(null)
            }
            viewHolder.titleView!!.setText(galleryAlbum.mAlbumName)
            val textView = viewHolder.itemCountView
            val sb = StringBuilder()
            sb.append("(")
            sb.append(galleryAlbum.mImageList.size)
            sb.append(")")
            textView!!.text = sb.toString()
            view.setOnClickListener {
                if (this@GalleryAlbumAdapter.mListener != null) {
                    this@GalleryAlbumAdapter.mListener!!.onGalleryAlbumClick(galleryAlbum)
                }
            }

            val isDarkTheme = context.getSharedPreferences(context.resources.getString(R.string.app_name), AppCompatActivity.MODE_PRIVATE).getBoolean("isDarkTheme", false)
            if (isDarkTheme) {
//            mainLayout?.setBackgroundColor(resources.getColor(R.color.blacktwo))
                viewHolder.titleView!!.setTextColor(R.color.white)
                viewHolder.itemCountView?.setTextColor(R.color.white)
            } else {
//            mainLayout?.setBackgroundColor(resources.getColor(R.color.white))
                viewHolder.titleView!!.setTextColor(R.color.blacktwo)
                viewHolder.itemCountView?.setTextColor(R.color.blacktwo)

            }

        }
        return view
    }

}
