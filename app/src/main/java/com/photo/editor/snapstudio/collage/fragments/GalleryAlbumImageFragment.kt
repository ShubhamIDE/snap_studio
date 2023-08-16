package com.photo.editor.snapstudio.collage.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.ProgressBar

//import com.photoeditor.photoeffect.R
import com.photo.editor.snapstudio.collage.adapter.GalleryAlbumImageAdapter
import com.photo.editor.snapstudio.R
//import com.photo.editor.snapstudio.databinding.FragmentGalleryAlbumImageBinding

//import kotlinx.android.synthetic.main.fragment_gallery_album_image.view.*

/**
 * A simple [Fragment] subclass.
 */
class GalleryAlbumImageFragment : Fragment() {
//    private var _binding: FragmentGalleryAlbumImageBinding? = null
//    private val binding get() = _binding!!
    companion object {
        val ALBUM_IMAGE_EXTRA = "albumImage"
        val ALBUM_NAME_EXTRA = "albumName"
    }

    var mImages: ArrayList<String> = ArrayList()
    lateinit var names: String
    lateinit var mListener: OnSelectImageListener
    private var gridView: GridView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (activity is OnSelectImageListener) {
            mListener = activity as OnSelectImageListener
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        _binding = FragmentGalleryAlbumImageBinding.inflate(inflater, container, false)
//        val view = binding.root
        var view = inflater.inflate(R.layout.fragment_gallery_album_image, container, false)
        gridView = view.findViewById(R.id.gridView)


        if (arguments != null) {
            mImages = requireArguments().getStringArrayList(ALBUM_IMAGE_EXTRA)!!
            names = requireArguments().getString(ALBUM_NAME_EXTRA)!!

            if (mImages != null) {

                gridView!!.adapter = GalleryAlbumImageAdapter(view.context, mImages)
                gridView!!.setOnItemClickListener(object : AdapterView.OnItemClickListener {
                    override fun onItemClick(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (mListener != null) {
                            mListener.onSelectImage(mImages[position])
                        }
                    }
                })
            }
        }
        return view
    }

    interface OnSelectImageListener {
        fun onSelectImage(str: String)
    }
}
