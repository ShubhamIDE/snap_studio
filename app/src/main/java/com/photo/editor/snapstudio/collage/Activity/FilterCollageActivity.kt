package com.photo.editor.snapstudio.collage.Activity

//import com.photo.editor.snapstudio.MainActivity.Companion.isFromSaved

//import com.photo.editor.snapstudio.dataActivityFilterCollageBinding
//import kotlinx.android.synthetic.main.activity_filter_collage.*
import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.*
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.photo.editor.snapstudio.Activity.DashboardActivity
import com.photo.editor.snapstudio.Activity.SavedImageActivity
import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils
import com.photo.editor.snapstudio.AdsUtils.Interfaces.AppInterfaces
import com.photo.editor.snapstudio.R
import com.photo.editor.snapstudio.collage.AndroidUtils
import com.photo.editor.snapstudio.collage.adapter.FilterNameAdapter
import com.photo.editor.snapstudio.collage.model.FilterData
import java.io.File
import java.io.FileOutputStream
import java.util.*


class FilterCollageActivity : AppCompatActivity(), View.OnClickListener {
//    private lateinit var binding: ActivityFilterCollageBinding

    private var imgCollage: ImageView? = null
    private var backbt: ImageView? = null
    private var mainLayout: LinearLayout? = null


    private var mLastClickTime: Long = 0
    var isDarkTheme = false
    fun checkClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }

    private fun successDialog(string: String) {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val reg_layout = inflater.inflate(R.layout.success_dialog, null)
        val tv = reg_layout.findViewById<TextView>(R.id.tv)
        tv.text = string
        dialog.setView(reg_layout)
        val handler = Handler()
        handler.postDelayed({
            val intent =
                Intent(this@FilterCollageActivity, SavedImageActivity::class.java)
            intent.putExtra("image_uri", savedImageUri!!.toString())
            startActivityForResult(intent, 2)
            finish()
        }, 2000)
        dialog.show()
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.img_save -> {
                AdUtils.showInterstitialAd(
                    this@FilterCollageActivity,
                    object : AppInterfaces.InterstitialADInterface {
                        override fun adLoadState(isLoaded: Boolean) {
                            checkClick()
//                isFromSaved = true
                            try {
                                saveBitmap(screenShot)
                            } catch (th: Throwable) {
                                th.printStackTrace()
                            }

                            val string = "Your photo has been saved !"
                            successDialog(string)

                        }
                    })
            }
        }
    }

    val screenShot: Bitmap
        get() {
            val findViewById = findViewById<View>(R.id.img_collage)
            findViewById.background = null
            findViewById.destroyDrawingCache()
            findViewById.isDrawingCacheEnabled = true
            val createBitmap = Bitmap.createBitmap(findViewById.drawingCache)
            findViewById.isDrawingCacheEnabled = false
            val createBitmap2 = Bitmap.createBitmap(
                createBitmap.width,
                createBitmap.height,
                Bitmap.Config.ARGB_8888
            )
            findViewById.draw(Canvas(createBitmap2))
            return createBitmap2
        }

    private var savedImageUri: Uri? = null

    fun saveBitmap(bitmap: Bitmap) {
        var path = Environment.getExternalStorageDirectory().absolutePath + "/Download/"
        val mainDir = File(path, resources.getString(R.string.app_name))
        if (!mainDir.exists()) {
            if (mainDir.mkdir())
                Log.e("Create Directory", "Main Directory Created : $mainDir")
        }
        val now = Date()
        val fileName = (now.time / 1000).toString() + ".png"

        val file = File(mainDir.absolutePath, fileName)
        try {
            val fOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()

            savedImageUri = Uri.parse(file.path)

            MediaScannerConnection.scanFile(this, arrayOf(file.absolutePath), null) { path, uri ->
                Log.i("ExternalStorage", "Scanned $path:")
                Log.i("ExternalStorage", "-> uri=$uri")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    companion object {
        var red: Float = 0F
        var green: Float = 0F
        var blue: Float = 0F
        var saturation: Float = 0F
    }

    lateinit var bmp: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(1)
        window.setFlags(1024, 1024)
//        binding = ActivityFilterCollageinflate(layoutInflater)
//        val view = root
        setContentView(R.layout.activity_filter_collage)
        imgCollage = findViewById<View>(R.id.img_collage) as ImageView
        val imgSave = findViewById<TextView>(R.id.img_save)
        val listFilterstype = findViewById<RecyclerView>(R.id.list_filterstype)
        val filterNames = findViewById<RecyclerView>(R.id.filter_names)
        backbt = findViewById<View>(R.id.backbt) as ImageView
        mainLayout = findViewById<View>(R.id.main_layout) as LinearLayout
        changeTheme()
        val filename = "file.jpg"
        val intent = intent
        //    val bitmap = intent.getParcelableExtra<Parcelable>("BitmapImage") as Bitmap?
        var path =
            Environment.getExternalStorageDirectory().toString() + "/Download/tempBMP/" + filename
        val bitmapPath = path
        val options = BitmapFactory.Options()
        Log.e("bitmappath", bitmapPath)
        try {

            if (bitmapPath != null) {
                val bmOptions = BitmapFactory.Options()
                bmp = BitmapFactory.decodeFile(bitmapPath, bmOptions)

                imgCollage!!.setImageBitmap(bmp)
            }
            //  bmp = BitmapFactory.decodeStream(path ,null, options)!!;
            // img_collage.setImageBitmap(bmp);
        } catch (E: Exception) {

        }

        backbt!!.setOnClickListener {
            onBackPressed()
        }

        imgSave.setOnClickListener(this)
        listFilterstype.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        var filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_clr1)
        listFilterstype.adapter = filter_typeAdapter

        filterNames.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        var filter_nameAdapter = FilterNameAdapter(this, resources.getStringArray(R.array.filters))

        filter_nameAdapter.setOnFilterNameClick(object : FilterNameAdapter.FilterNameClickListener {
            override fun onItemClick(view: View, position: Int) {

                if (position == 0) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_clr1)
                    listFilterstype.adapter = filter_typeAdapter
                } else if (position == 1) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_clr2)
                    listFilterstype.adapter = filter_typeAdapter
                } else if (position == 2) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_duo)
                    listFilterstype.adapter = filter_typeAdapter
                } else if (position == 3) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_pink)
                    listFilterstype.adapter = filter_typeAdapter
                } else if (position == 4) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_fresh)
                    listFilterstype.adapter = filter_typeAdapter
                } else if (position == 5) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_euro)
                    listFilterstype.adapter = filter_typeAdapter
                } else if (position == 6) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_dark)
                    listFilterstype.adapter = filter_typeAdapter
                } else if (position == 7) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_ins)
                    listFilterstype.adapter = filter_typeAdapter
                } else if (position == 8) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_elegant)
                    listFilterstype.adapter = filter_typeAdapter
                } else if (position == 9) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_golden)
                    listFilterstype.adapter = filter_typeAdapter
                } else if (position == 10) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_tint)
                    listFilterstype.adapter = filter_typeAdapter
                } else if (position == 11) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_film)
                    listFilterstype.adapter = filter_typeAdapter
                } else if (position == 12) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_lomo)
                    listFilterstype.adapter = filter_typeAdapter
                } else if (position == 13) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_movie)
                    listFilterstype.adapter = filter_typeAdapter
                } else if (position == 14) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_retro)
                    listFilterstype.adapter = filter_typeAdapter
                } else if (position == 15) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_bw)
                    listFilterstype.adapter = filter_typeAdapter
                } else {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_clr1)
                    listFilterstype.adapter = filter_typeAdapter
                }
                filter_nameAdapter.notifyDataSetChanged()
                filter_typeAdapter.notifyDataSetChanged()
            }
        })

        filterNames.adapter = filter_nameAdapter

    }

    private fun changeTheme() {
        isDarkTheme = getSharedPreferences(
            resources.getString(R.string.app_name),
            MODE_PRIVATE
        ).getBoolean("isDarkTheme", false)
        // AdUtils.showNativeAd(
//            this@FilterCollageActivity,
//            findViewById<LinearLayout>(R.id.native_ads),
//            false,
//            isDarkTheme
//        )
        if (isDarkTheme) {
            mainLayout!!.setBackgroundResource(R.color.blacktwo)
            backbt!!.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.white))
        } else {
            mainLayout!!.setBackgroundResource(R.color.white)
            backbt!!.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.blacktwo))

        }
    }


    inner class FilterDetailAdapter(filters: Array<FilterData>) :
        RecyclerView.Adapter<FilterDetailAdapter.FilterDetailHolder>() {
        var filterType = filters
        var selectedindex = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterDetailHolder {
            var view = LayoutInflater.from(this@FilterCollageActivity)
                .inflate(R.layout.itemfilter, parent, false)
            return FilterDetailHolder(view)
        }

        override fun getItemCount(): Int {
            return filterType.size
        }

        override fun onBindViewHolder(holder: FilterDetailHolder, position: Int) {

            if (selectedindex == position) {
                holder.rl_filteritem.setBackgroundColor(resources.getColor(R.color.pink))
            } else {
                holder.rl_filteritem.setBackgroundColor(resources.getColor(R.color.transparent))
            }

            holder.thumbnail_filter.setImageResource(R.drawable.thumb_filter)

            red = filterType[position].red
            green = filterType[position].green
            blue = filterType[position].blue
            saturation = filterType[position].saturation

            var bitmap = Bitmap.createBitmap(
                bmp.getWidth(),
                bmp.getHeight(),
                Bitmap.Config.ARGB_8888
            )
            var canvas = Canvas(bitmap)

            var paint = Paint()
            var colorMatrix = ColorMatrix()
            colorMatrix.setSaturation(saturation)

            var colorScale = ColorMatrix()
            colorScale.setScale(
                red,
                green,
                blue, 1F
            )
            colorMatrix.postConcat(colorScale)

            paint.setColorFilter(ColorMatrixColorFilter(colorMatrix))
            canvas.drawBitmap(bmp, 0F, 0F, paint)

            holder.thumbnail_filter.setImageBitmap(bitmap)

            holder.filterName.setText(filterType[position].text)

            holder.rl_filteritem.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {

                    selectedindex = position

                    red = filterType[position].red
                    green = filterType[position].green
                    blue = filterType[position].blue
                    saturation = filterType[position].saturation

                    Async_Filter(
                        bmp,
                        imgCollage!!
                    ).executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR,
                        red,
                        green,
                        blue
                    )
                    notifyDataSetChanged()
                }
            })
        }

        inner class FilterDetailHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var thumbnail_filter: ImageView
            var filterName: TextView
            var rl_filteritem: RelativeLayout

            init {
                thumbnail_filter = itemView.findViewById(R.id.thumbnail_filter)
                filterName = itemView.findViewById(R.id.filterName)
                rl_filteritem = itemView.findViewById(R.id.rl_filteritem)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    class Async_Filter() : AsyncTask<Float, Void, Bitmap>() {

        lateinit var originalBitmap: Bitmap
        lateinit var imgMain: ImageView

        constructor(originalBitmap: Bitmap, imgMain: ImageView) : this() {
            this.originalBitmap = originalBitmap
            this.imgMain = imgMain
        }

        override fun doInBackground(vararg params: Float?): Bitmap {
            var r = params[0]
            var g = params[1]
            var b = params[2]

            var bitmap = Bitmap.createBitmap(
                this.originalBitmap.getWidth(),
                this.originalBitmap.getHeight(),
                Bitmap.Config.ARGB_8888
            )
            var canvas = Canvas(bitmap)

            var paint = Paint()
            var colorMatrix = ColorMatrix()
            colorMatrix.setSaturation(saturation)

            var colorScale = ColorMatrix()
            colorScale.setScale(r!!, g!!, b!!, 1F)
            colorMatrix.postConcat(colorScale)

            paint.setColorFilter(ColorMatrixColorFilter(colorMatrix))
            canvas.drawBitmap(this.originalBitmap, 0F, 0F, paint)

            return bitmap
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)

            this.imgMain.setImageBitmap(result)

        }
    }

}
