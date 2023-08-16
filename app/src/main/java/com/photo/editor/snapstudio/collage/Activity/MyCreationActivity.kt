package com.photo.editor.snapstudio.collage.Activity

//import kotlinx.android.synthetic.main.activity_my_creation.*
//import com.photo.editor.snapstudio.Activity.ShowVideoActivity
//import com.photo.editor.snapstudio.videoeditor.activity.AudioPlayer
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photo.editor.snapstudio.Activity.DashboardActivity
import com.photo.editor.snapstudio.Activity.SavedImageActivity
import com.photo.editor.snapstudio.R
import java.io.File
import java.lang.Long.compare
import java.util.*


//import com.photo.editor.snapstudio.MainActivity.Companion.isFromSaved


class MyCreationActivity : AppCompatActivity() {

    //    private lateinit var binding: ActivityMyCreationBinding
    private var listCreation: RecyclerView? = null
    private var backbt: ImageView? = null
    private var rlsph: RelativeLayout? = null

    lateinit var img_path: ArrayList<File_Model>

    private var mLastClickTime: Long = 0
    fun checkClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_creation)

        listCreation = findViewById<View>(R.id.list_creation) as RecyclerView
        backbt = findViewById<View>(R.id.backbt) as ImageView
        rlsph = findViewById<View>(R.id.rlsph) as RelativeLayout
        rlsph!!.visibility = View.VISIBLE

        listCreation!!.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)

        LoadImages().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        Log.e("Page", "My creation")

        backbt!!.setOnClickListener {
            var intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    inner class LoadImages : AsyncTask<Void, Void, Void?>() {


        override fun doInBackground(vararg params: Void?): Void? {
            updateFileList()
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            if (img_path.size == 0) {
//                var builder = AlertDialog.Builder(this@MyCreationActivity)
//                builder.setMessage("No Files Found").setCancelable(false)
//                    .setPositiveButton("Ok", object : DialogInterface.OnClickListener {
//                        override fun onClick(dialog: DialogInterface?, which: Int) {
//                            dialog!!.cancel()
//                            onBackPressed()
//                        }
//                    })
//                var alert = builder.create()
//                alert.show()
//                return
                rlsph!!.visibility = View.VISIBLE
            } else

                if (img_path != null) {
                    rlsph!!.visibility = View.GONE

                    var creationAdapter = CreationAdapter(img_path)
                    listCreation!!.adapter = creationAdapter
                }

        }

    }

    fun updateFileList() {
        var path = Environment.getExternalStorageDirectory()
            .toString() + "/Download/" + resources.getString(R.string.app_name)
        val directory = File(path)
        val files = directory.listFiles()

        img_path = ArrayList()

        var fileDateCmp = Comparator<File> { f1, f2 ->
            compare(f2.lastModified(), f1.lastModified())
        }

        if (files != null) {
            Arrays.sort(files, fileDateCmp)

            for (i in 0 until files.size) {
                var file_model = File_Model()
                file_model.file_path = files[i].absolutePath
                file_model.file_title = files[i].name
                img_path.add(file_model)
            }
        }
    }

    inner class File_Model {
        lateinit var file_path: String
        lateinit var file_title: String
    }

    inner class CreationAdapter(imgPath: ArrayList<File_Model>) :
        RecyclerView.Adapter<CreationAdapter.CreationHolder>() {

        var paths = imgPath

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreationHolder {
            var view = LayoutInflater.from(this@MyCreationActivity)
                .inflate(R.layout.item_creation, parent, false)

//            isFromSaved = false
            return CreationHolder(view)
        }

        override fun getItemCount(): Int {
            return paths.size
        }

        override fun onBindViewHolder(holder: CreationHolder, position: Int) {

            val dm = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(dm)
            val width = dm.widthPixels
            val height = dm.heightPixels

            holder.img_creation.layoutParams = RelativeLayout.LayoutParams(width / 2, width / 2)

//            holder.img_creation.setImageURI(Uri.parse(paths[position].file_path))
            Glide.with(holder.img_creation)
                .load(paths[position].file_path).placeholder(R.drawable.logo)
                .into(holder.img_creation)
            holder.txt_title.setText(paths[position].file_title)
            holder.img_dlt.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {

                    checkClick()

                    val builder = AlertDialog.Builder(this@MyCreationActivity)
                    //set title for alert dialog
                    //set message for alert dialog
                    builder.setMessage("Are you sure you want to delete?")

                    //performing positive action
                    builder.setPositiveButton("Yes") { dialogInterface, which ->
                        var filepath = paths[position].file_path
                        if (File(filepath).delete()) {
                            paths.removeAt(position)
                            notifyDataSetChanged()
                        }
                        dialogInterface!!.dismiss()
                    }
                    //performing cancel action
                    //performing negative action
                    builder.setNegativeButton("No") { dialogInterface, which ->
                        dialogInterface!!.dismiss()
                    }
                    // Create the AlertDialog
                    val alertDialog: AlertDialog = builder.create()
                    // Set other dialog properties
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(getResources().getColor(R.color.black));
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(getResources().getColor(R.color.black));
                }

//                var builder = AlertDialog.Builder(this@MyCreationActivity)
//                builder.setMessage("Are you sure you want to delete?")
//                .setPositiveButton("Yes",
//                object : DialogInterface.OnClickListener {
//                    override fun onClick(dialog: DialogInterface?, which: Int) {
//                        var filepath = paths[position].file_path
//                        if (File(filepath).delete()) {
//                            paths.removeAt(position)
//                            notifyDataSetChanged()
//                        }
//                        dialog!!.dismiss()
//                    }
//                })
//                .setNegativeButton("No",
//                object : DialogInterface.OnClickListener {
//                    override fun onClick(dialog: DialogInterface?, which: Int) {
//                        dialog!!.dismiss()
//                    }
//                }).show()
//            }

            })

            holder.img_creation.setOnClickListener(
                object : View.OnClickListener {
                    override fun onClick(v: View?) {

                        checkClick()
                        var uri: String = paths[position].file_path

//                if (uri!!.contains(".3gp") || uri!!.contains(".3GP") || uri!!.contains(".flv") || uri!!.contains(
//                        ".FLv"
//                    ) || uri!!.contains(".mov") || uri!!.contains(".MOV") || uri!!.contains(".wmv") || uri!!.contains(
//                        ".WMV"
//                    ) || uri!!.contains(".mp4") || uri!!.contains(".Mp4") || uri!!.contains(".MP4")
//                ) {
//                    try {
//                        val intent =
//                            Intent(this@MyCreationActivity, ShowVideoActivity::class.java)
//                        intent.putExtra("video_uri", uri)
//                        startActivity(intent)
//                    } catch (e5: Exception) {
//                        e5.printStackTrace()
//                    }
//                } else if (uri!!.contains(".mp3") || uri!!.contains(".Mp3") || uri!!.contains(".MP3") || uri!!.contains(
//                        ".aac"
//                    ) || uri!!.contains(".AAC")
//                ) {
////                        val myUri: Uri = Uri.parse(uri)
////                        var mediaPlayer = MediaPlayer()
////                        try {
////                            // mediaPlayer.setDataSource(String.valueOf(myUri));
////                            mediaPlayer.setDataSource(this@MyCreationActivity, myUri)
////                        } catch (e: IOException) {
////                            e.printStackTrace()
////                        }
////                        try {
////                            mediaPlayer.prepare()
////                        } catch (e: IOException) {
////                            e.printStackTrace()
////                        }
////                        mediaPlayer.start()
//                    try {
//                        val intent =
//                            Intent(this@MyCreationActivity, AudioPlayer::class.java)
//                        intent.putExtra("song", uri)
//                        startActivity(intent)
//                    } catch (e5: Exception) {
//                        e5.printStackTrace()
//                    }
//
//                } else {
                        try {
                            val intent =
                                Intent(this@MyCreationActivity, SavedImageActivity::class.java)
                            intent.putExtra("image_uri", uri)
                            startActivity(intent)
                        } catch (e6: Exception) {
                            e6.printStackTrace()
                        }
//                }

//                    val intent = Intent(this@MyCreationActivity, ShowImageActivity::class.java)
//                    intent.putExtra("image_uri", paths[position].file_path)
//                    startActivity(intent)
//                    finish()
                    }
                })
        }

        inner class CreationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var img_creation = itemView.findViewById<ImageView>(R.id.img_creation)
            var img_dlt = itemView.findViewById<ImageView>(R.id.img_dlt)
            var txt_title = itemView.findViewById<TextView>(R.id.txt_title)
        }

    }
}
