package com.photo.editor.snapstudio.collage.Activity

//import com.photo.editor.snapstudio.dataActivityMyCreationBinding
//import com.photo.editor.snapstudio.dataActivitySelectImageBinding
//import kotlinx.android.synthetic.main.activity_select_image.*
//import com.photo.editor.snapstudio.Activity.EditorActivity
//import com.photo.editor.snapstudio.picker.PhotoPicker
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.photo.editor.snapstudio.Activity.DashboardActivity
import com.photo.editor.snapstudio.PhEditor.Activity.EditorActivity
import com.photo.editor.snapstudio.PhEditor.picker.PhotoPicker
import com.photo.editor.snapstudio.R
import com.photo.editor.snapstudio.collage.adapter.SelectedPhotoAdapter
import com.photo.editor.snapstudio.collage.fragments.GalleryAlbumFragment
import com.photo.editor.snapstudio.collage.fragments.GalleryAlbumImageFragment
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

class SelectImageActivity : AppCompatActivity(), GalleryAlbumImageFragment.OnSelectImageListener,
    SelectedPhotoAdapter.OnDeleteButtonClickListener {

    private var listImages: RecyclerView? = null
    private var btnNext: TextView? = null
    private var textImgcount: TextView? = null
    private var mainLayout: RelativeLayout? = null

    override fun onDeleteButtonClick(str: String) {

        mSelectedImages.remove(str)
        mSelectedPhotoAdapter.notifyDataSetChanged()
        val textView = textImgcount
        val str2 = "Select upto 9 photo(s)"
        val sb = StringBuilder()
        sb.append("(")
        sb.append(this.mSelectedImages.size)
        sb.append(")")
        textView?.text = str2 + sb.toString()
    }

    private val mSelectedImages = ArrayList<String>()

    //    private var maxIamgeCount = 10
    private lateinit var rvvalue: String
    private lateinit var imgUrl: String
    private lateinit var bgUrl: String

    private lateinit var str2: String

    private lateinit var mSelectedPhotoAdapter: SelectedPhotoAdapter
    private var mLastClickTime: Long = 0
    fun checkClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }


    override fun onSelectImage(str: String) {
        if (str != null) {
            val maxIamgeCount: Int
            if (rvvalue == "collage") {
                maxIamgeCount = 9
            } else {
                maxIamgeCount = 1
            }

            if (this.mSelectedImages.size == maxIamgeCount) {
                Toast.makeText(
                    this,
                    String.format("You only need %d photo(s)", maxIamgeCount),
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                var uri = Uri.fromFile(File(str))

                this.mSelectedImages.add(str)
                this.mSelectedPhotoAdapter.notifyDataSetChanged()
                val textView = textImgcount
                if (rvvalue == "collage") {
                    str2 = "Select upto 9 photo(s)"
                } else {
                    str2 = "Select upto photo"
                }
                val sb = StringBuilder()
                sb.append("(")
                sb.append(this.mSelectedImages.size)
                sb.append(")")
                textView?.text = str2 + sb.toString()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(1)
        window.setFlags(1024, 1024)
        setContentView(R.layout.activity_select_image)
        rvvalue = intent.getStringExtra("rvValues").toString()
        imgUrl = intent.getStringExtra("imgUrl").toString()
        bgUrl = intent.getStringExtra("bgUrl").toString()

        mainLayout = findViewById<View>(R.id.main_layout) as RelativeLayout
        btnNext = findViewById<View>(R.id.btn_next) as TextView
        listImages = findViewById<View>(R.id.list_images) as RecyclerView
        textImgcount = findViewById<View>(R.id.text_imgcount) as TextView
        changeTheme()

        mSelectedPhotoAdapter = SelectedPhotoAdapter(this,mSelectedImages, this)
        val textView = textImgcount
        str2 = if (rvvalue == "collage") {
            "Select upto 10 photo(s)"
        } else {
            "Select upto photo"
        }
        textView?.text = str2
        listImages!!.hasFixedSize()
        listImages!!.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        listImages!!.adapter = mSelectedPhotoAdapter

        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, GalleryAlbumFragment(this)).commit()

        btnNext!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                checkClick()
                createCollage()
            }
        })
    }

    private fun changeTheme() {
        val isDarkTheme = getSharedPreferences(
            resources.getString(R.string.app_name),
            MODE_PRIVATE
        ).getBoolean("isDarkTheme", false)
        if (isDarkTheme) {
            mainLayout?.setBackgroundColor(resources.getColor(R.color.blacktwo))
            btnNext?.setTextColor(resources.getColor(R.color.white))
            textImgcount?.setTextColor(resources.getColor(R.color.white))
        } else {
            mainLayout?.setBackgroundColor(resources.getColor(R.color.white))
            btnNext?.setTextColor(resources.getColor(R.color.blacktwo))
            textImgcount?.setTextColor(resources.getColor(R.color.blacktwo))

        }
    }

    fun createCollage() {
        if (mSelectedImages.size == 0) {

            Toast.makeText(this, "Please select photo(s)", Toast.LENGTH_SHORT).show()
            return
        } else if (mSelectedImages.size == 1) {
            if (rvvalue == "collage") {
//                if (imgUrl == null) {
                try {
                    var intent = Intent(this, CollageActivity::class.java)
                    intent.putExtra("imageCount", mSelectedImages.size)
                    intent.putExtra("selectedImages", mSelectedImages)
                    intent.putExtra("imagesinTemplate", mSelectedImages.size)

                    startActivityForResult(intent, 111)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
//                }else{
//                    var intent = Intent(this, CollageActivity::class.java)
//                    intent.putExtra("imageCount", mSelectedImages.size)
//                    intent.putExtra("selectedImages", mSelectedImages)
//                    intent.putExtra("imagesinTemplate", mSelectedImages.size)
//                    intent.putExtra("imageUrl", imgUrl)
//                    startActivityForResult(intent, 111)
//
//                }
            } /*else if (rvvalue == "video") {

                var intent = Intent(this, VideoEditorActivity::class.java)
                startActivity(intent)

            }*/ else {
                val imageUri = Uri.parse(mSelectedImages.get(0))
                var imageStream: InputStream? = null
                try {
                    imageStream = contentResolver.openInputStream(imageUri)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
                val selectedImage = BitmapFactory.decodeStream(imageStream)

                var i = Intent(this, EditorActivity::class.java)
                // on below line we are passing the image path to our new activity.
                i.putExtra(PhotoPicker.KEY_SELECTED_PHOTOS, getRealPathFromURI(imageUri))
//                i.putExtra("imgPath", mSelectedImages.get(position))
                i.putExtra("rvValue", rvvalue)
                i.putExtra("bgUrl", bgUrl)
                Log.e("value", rvvalue)
                startActivity(i)
            }
            return
        } else {

            try {
                var intent = Intent(this, CollageActivity::class.java)
                intent.putExtra("imageCount", mSelectedImages.size)
                intent.putExtra("selectedImages", mSelectedImages)
                intent.putExtra("imagesinTemplate", mSelectedImages.size)

                startActivityForResult(intent, 111)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        val cursor: Cursor? = contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == 111) {
            var intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
