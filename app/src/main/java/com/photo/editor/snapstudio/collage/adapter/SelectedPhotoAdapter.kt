package com.photo.editor.snapstudio.collage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.photo.editor.snapstudio.collage.AndroidUtils
import com.photo.editor.snapstudio.R

import java.util.ArrayList

class SelectedPhotoAdapter(context: Context,
    arrayList: ArrayList<String>,
    onDeleteButtonClickListener: OnDeleteButtonClickListener
) : RecyclerView.Adapter<SelectedPhotoAdapter.SelectedPhotoViewHolder>() {

    var context = context
    var mImages = arrayList
    var mListener = onDeleteButtonClickListener

    interface OnDeleteButtonClickListener {
        fun onDeleteButtonClick(str: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedPhotoViewHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_selected_photo, parent, false)

        return SelectedPhotoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mImages.size
    }

    override fun onBindViewHolder(holder: SelectedPhotoViewHolder, position: Int) {
        AndroidUtils.loadImageWithGlide(
            holder.selectedImage.context,
            holder.selectedImage,
            mImages[position]
        )
        val isDarkTheme = context.getSharedPreferences(context.resources.getString(R.string.app_name), AppCompatActivity.MODE_PRIVATE).getBoolean("isDarkTheme", false)
        if (isDarkTheme) {
//            mainLayout?.setBackgroundColor(resources.getColor(R.color.blacktwo))
            holder.deleteView.setColorFilter(context.resources.getColor(R.color.white))
//            holder.itemCountView?.setTextColor(context.resources.getColor(R.color.white))
        } else {
//            mainLayout?.setBackgroundColor(resources.getColor(R.color.white))
            holder.deleteView.setColorFilter(context.resources.getColor(R.color.blacktwo))
//            holder.itemCountView?.setTextColor(context.resources.getColor(R.color.blacktwo))

        }
        holder.deleteView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (mListener != null) {
                    mListener.onDeleteButtonClick(mImages[position])
                }
            }
        })
    }


    class SelectedPhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var selectedImage: ImageView = itemView.findViewById(R.id.selectedImage)
        var deleteView: ImageView = itemView.findViewById(R.id.deleteView)
    }
}