package com.photo.editor.snapstudio.collage.Activity

//import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils
//import com.photo.editor.snapstudio.AdsUtils.Utils.Constants
//import com.photo.editor.snapstudio.dataActivityCollageBinding
//import kotlinx.android.synthetic.main.activity_collage.*
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.photo.editor.snapstudio.Activity.DashboardActivity
import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils
import com.photo.editor.snapstudio.R
import com.photo.editor.snapstudio.collage.AndroidUtils
import com.photo.editor.snapstudio.collage.adapter.BackgroundAdapter
import com.photo.editor.snapstudio.collage.adapter.FrameAdapter
import com.photo.editor.snapstudio.collage.frame.FramePhotoLayout
import com.photo.editor.snapstudio.collage.frame.utils.FrameImageUtils
import com.photo.editor.snapstudio.collage.frame.utils.ImageUtils
import com.photo.editor.snapstudio.collage.model.TemplateItem
import com.photo.editor.snapstudio.collage.multitouch.PhotoView
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class CollageActivity : AppCompatActivity(), View.OnClickListener,
    FrameAdapter.OnFrameClickListener, BackgroundAdapter.OnBGClickListener {

    private var tabLayout: LinearLayout? = null
    private var tabBorder: LinearLayout? = null
    private var tabBg: LinearLayout? = null

    private var layTxt: TextView? = null
    private var borTxt: TextView? = null
    private var bgTxt: TextView? = null

    private var llBg: LinearLayout? = null
    private var llBorder: LinearLayout? = null
    private var llFrame: LinearLayout? = null

    private var listBg: RecyclerView? = null
    private var listFrames: RecyclerView? = null

    private var seekbarCorner: SeekBar? = null
    private var seekbarSpace: SeekBar? = null

    private var rlContainer: RelativeLayout? = null
    private var mainLayout: LinearLayout? = null

    private var btnNext: TextView? = null
    private var addbt: ImageView? = null
    private var backbt: ImageView? = null
    var isDarkTheme = false

    private var icon_roundcorner: ImageView? = null
    private var icon_space: ImageView? = null

    private lateinit var backVal: String


    var mFramePhotoLayout: FramePhotoLayout? = null
    var DEFAULT_SPACE: Float = 0.0f
    var MAX_SPACE: Float = 0.0f
    var MAX_CORNER: Float = 0.0f

    protected val RATIO_SQUARE = 0
    protected val RATIO_GOLDEN = 2

    private var mSpace = DEFAULT_SPACE
    private var mCorner = 0f
    val MAX_SPACE_PROGRESS = 300.0f
    val MAX_CORNER_PROGRESS = 200.0f
    private var mblacktwo = Color.WHITE
    private var mBackgroundImage: Bitmap? = null
    private var mBackgroundUri: Uri? = null
    private var mSavedInstanceState: Bundle? = null
    protected var mLayoutRatio = RATIO_SQUARE
    protected lateinit var mPhotoView: PhotoView
    protected var mOutputScale = 1f
    protected var mSelectedTemplateItem: TemplateItem? = null
    private var mImageInTemplateCount = 0
    protected var mTemplateItemList: ArrayList<TemplateItem>? = ArrayList()
    protected var mSelectedPhotoPaths: MutableList<String> = java.util.ArrayList()

    lateinit var frameAdapter: FrameAdapter
    lateinit var img_background: ImageView
//    private lateinit var binding: ActivityCollageBinding


    private var mLastClickTime: Long = 0
    fun checkClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }

    override fun onBGClick(drawable: Drawable) {

        var bmp = mFramePhotoLayout!!.createImage()
        var bitmap = (drawable as BitmapDrawable).bitmap
        mBackgroundImage = AndroidUtils.resizeImageToNewSize(bitmap, bmp.width, bmp.height)

        //  img_background.background = BitmapDrawable(resources, mBackgroundImage)
        img_background.setImageBitmap(mBackgroundImage)

    }

    override fun onFrameClick(templateItem: TemplateItem) {

        mSelectedTemplateItem!!.isSelected = false

        for (idx in 0 until mSelectedTemplateItem!!.photoItemList.size) {
            val photoItem = mSelectedTemplateItem!!.photoItemList[idx]
            if (photoItem.imagePath != null && photoItem.imagePath!!.length > 0) {
                if (idx < mSelectedPhotoPaths.size) {
                    mSelectedPhotoPaths.add(idx, photoItem.imagePath!!)
                } else {
                    mSelectedPhotoPaths.add(photoItem.imagePath!!)
                }
            }
        }

        val size = Math.min(mSelectedPhotoPaths.size, templateItem.photoItemList.size)
        for (idx in 0 until size) {
            val photoItem = templateItem.photoItemList.get(idx)
            if (photoItem.imagePath == null || photoItem.imagePath!!.length < 1) {
                photoItem.imagePath = mSelectedPhotoPaths[idx]
            }
        }

        mSelectedTemplateItem = templateItem
        mSelectedTemplateItem!!.isSelected = true
        frameAdapter.notifyDataSetChanged()
        buildLayout(templateItem)
    }

    inner class space_listener : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            mSpace = MAX_SPACE * seekBar!!.getProgress() / MAX_SPACE_PROGRESS
            if (mFramePhotoLayout != null)
                mFramePhotoLayout!!.setSpace(mSpace, mCorner)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {

        }
    }

    inner class corner_listener : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            mCorner = MAX_CORNER * seekBar!!.getProgress() / MAX_CORNER_PROGRESS
            if (mFramePhotoLayout != null)
                mFramePhotoLayout!!.setSpace(mSpace, mCorner)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {

        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.tab_layout -> {
//                tabLayout!!.setBackgroundColor(resources.getColor(R.color.colorAccent))
//                tabBorder!!.setBackgroundColor(resources.getColor(R.color.windowBackground))
//                tabBg!!.setBackgroundColor(resources.getColor(R.color.windowBackground))


                if (isDarkTheme) {
                    layTxt!!.setTextColor(resources.getColor(R.color.pink))
                    borTxt!!.setTextColor(resources.getColor(R.color.white))
                    bgTxt!!.setTextColor(resources.getColor(R.color.white))
                    layTxt!!.setCompoundDrawableTintList(ColorStateList.valueOf(resources.getColor(R.color.pink)))
                    borTxt!!.setCompoundDrawableTintList(ColorStateList.valueOf(resources.getColor(R.color.white)))
                    bgTxt!!.setCompoundDrawableTintList(ColorStateList.valueOf(resources.getColor(R.color.white)))
                }else{
                    layTxt!!.setTextColor(resources.getColor(R.color.pink))
                    borTxt!!.setTextColor(resources.getColor(R.color.blacktwo))
                    bgTxt!!.setTextColor(resources.getColor(R.color.blacktwo))
                    layTxt!!.setCompoundDrawableTintList(ColorStateList.valueOf(resources.getColor(R.color.pink)))
                    borTxt!!.setCompoundDrawableTintList(ColorStateList.valueOf(resources.getColor(R.color.blacktwo)))
                    bgTxt!!.setCompoundDrawableTintList(ColorStateList.valueOf(resources.getColor(R.color.blacktwo)))
                }

                llFrame!!.visibility = View.VISIBLE
                llBorder!!.visibility = View.GONE
                llBg!!.visibility = View.GONE
            }

            R.id.tab_border -> {
//                tabLayout!!.setBackgroundColor(resources.getColor(R.color.windowBackground))
//                tabBorder!!.setBackgroundColor(resources.getColor(R.color.colorAccent))
//                tabBg!!.setBackgroundColor(resources.getColor(R.color.windowBackground))
                if (isDarkTheme) {
                    layTxt!!.setTextColor(resources.getColor(R.color.white))
                    borTxt!!.setTextColor(resources.getColor(R.color.pink))
                    bgTxt!!.setTextColor(resources.getColor(R.color.white))

                    layTxt!!.setCompoundDrawableTintList(ColorStateList.valueOf(resources.getColor(R.color.white)))
                    borTxt!!.setCompoundDrawableTintList(ColorStateList.valueOf(resources.getColor(R.color.pink)))
                    bgTxt!!.setCompoundDrawableTintList(ColorStateList.valueOf(resources.getColor(R.color.white)))
                } else {
                    layTxt!!.setTextColor(resources.getColor(R.color.blacktwo))
                    borTxt!!.setTextColor(resources.getColor(R.color.pink))
                    bgTxt!!.setTextColor(resources.getColor(R.color.blacktwo))

                    layTxt!!.setCompoundDrawableTintList(ColorStateList.valueOf(resources.getColor(R.color.blacktwo)))
                    borTxt!!.setCompoundDrawableTintList(ColorStateList.valueOf(resources.getColor(R.color.pink)))
                    bgTxt!!.setCompoundDrawableTintList(ColorStateList.valueOf(resources.getColor(R.color.blacktwo)))
                }

                llFrame!!.visibility = View.GONE
                llBorder!!.visibility = View.VISIBLE
                llBg!!.visibility = View.GONE
            }
            R.id.tab_bg -> {
//                tabLayout!!.setBackgroundColor(resources.getColor(R.color.windowBackground))
//                tabBorder!!.setBackgroundColor(resources.getColor(R.color.windowBackground))
//                tabBg!!.setBackgroundColor(resources.getColor(R.color.colorAccent))
                if (isDarkTheme) {
                    layTxt!!.setTextColor(resources.getColor(R.color.white))
                    borTxt!!.setTextColor(resources.getColor(R.color.white))
                    bgTxt!!.setTextColor(resources.getColor(R.color.pink))

                    layTxt!!.setCompoundDrawableTintList(ColorStateList.valueOf(resources.getColor(R.color.white)))
                    borTxt!!.setCompoundDrawableTintList(ColorStateList.valueOf(resources.getColor(R.color.white)))
                    bgTxt!!.setCompoundDrawableTintList(ColorStateList.valueOf(resources.getColor(R.color.pink)))
                } else {
                    layTxt!!.setTextColor(resources.getColor(R.color.blacktwo))
                    borTxt!!.setTextColor(resources.getColor(R.color.blacktwo))
                    bgTxt!!.setTextColor(resources.getColor(R.color.pink))

                    layTxt!!.setCompoundDrawableTintList(ColorStateList.valueOf(resources.getColor(R.color.blacktwo)))
                    borTxt!!.setCompoundDrawableTintList(ColorStateList.valueOf(resources.getColor(R.color.blacktwo)))
                    bgTxt!!.setCompoundDrawableTintList(ColorStateList.valueOf(resources.getColor(R.color.pink)))
                }


                llFrame!!.visibility = View.GONE
                llBorder!!.visibility = View.GONE
                llBg!!.visibility = View.VISIBLE

            }
            R.id.btn_next -> {

                checkClick()
                var path = Environment.getExternalStorageDirectory().absolutePath + "/Download/"
                val mainDir = File(path, "tempBMP")
                if (!mainDir.exists()) {
                    if (mainDir.mkdir())
                        Log.e("Create Directory", "Main Directory Created : $mainDir")
                }
                var outStream: FileOutputStream? = null
                var collageBitmap: Bitmap? = null
                try {
                    val filename = "file.jpg"
                    collageBitmap = createOutputImage()
                    Log.e("collageb", collageBitmap.toString())

                    var path = Environment.getExternalStorageDirectory().absolutePath + "/Download/"
                    outStream = FileOutputStream(
                        Environment.getExternalStorageDirectory()
                            .toString() + "/Download/tempBMP/" + filename
                    )
                    collageBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outStream)
                    outStream.close()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val intent = Intent(this, FilterCollageActivity::class.java)
//                 intent.putExtra("BitmapImage", collageBitmap);
                startActivity(intent)
//                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(1)
        window.setFlags(1024, 1024)
        setContentView(R.layout.activity_collage)
//        AdUtils.showNativeAd(
//            this,
//            Constants.adsJsonPOJO.parameters.native_id.defaultValue.value,
//            findViewById(R.id.native_ads),
//            false
//        )
        layTxt = findViewById<View>(R.id.lay_txt) as TextView
        borTxt = findViewById<View>(R.id.bor_txt) as TextView
        bgTxt = findViewById<View>(R.id.bg_txt) as TextView
        btnNext = findViewById<View>(R.id.btn_next) as TextView
        tabLayout = findViewById<View>(R.id.tab_layout) as LinearLayout
        tabBg = findViewById<View>(R.id.tab_bg) as LinearLayout
        tabBorder = findViewById<View>(R.id.tab_border) as LinearLayout
        llBg = findViewById<View>(R.id.ll_bg) as LinearLayout
        llBorder = findViewById<View>(R.id.ll_border) as LinearLayout
        llFrame = findViewById<View>(R.id.ll_frame) as LinearLayout
        listFrames = findViewById<View>(R.id.list_frames) as RecyclerView
        listBg = findViewById<View>(R.id.list_bg) as RecyclerView
        seekbarSpace = findViewById<View>(R.id.seekbar_space) as SeekBar
        seekbarCorner = findViewById<View>(R.id.seekbar_corner) as SeekBar
        rlContainer = findViewById<View>(R.id.rl_container) as RelativeLayout
        mainLayout = findViewById<View>(R.id.main_layout) as LinearLayout
        addbt = findViewById<View>(R.id.add) as ImageView
        backbt = findViewById<View>(R.id.backbt) as ImageView
        icon_roundcorner = findViewById<View>(R.id.icon_roundcorner) as ImageView
        icon_space = findViewById<View>(R.id.icon_space) as ImageView
        backVal = ""

        changeTheme()
        addbt!!.setOnClickListener {
//            var intent = Intent(this, SelectImageActivity::class.java)
//            intent.putExtra("rvValues", "collage")
//            startActivity(intent)
            backVal = "add"
            onBackPressed()
        }

        backbt!!.setOnClickListener {
            onBackPressed()
        }

        DEFAULT_SPACE = ImageUtils.pxFromDp(this, 2F)
        MAX_SPACE = ImageUtils.pxFromDp(this, 30F)
        MAX_CORNER = ImageUtils.pxFromDp(this, 60F)
        mSpace = DEFAULT_SPACE

        if (savedInstanceState != null) {
            mSpace = savedInstanceState.getFloat("mSpace")
            mCorner = savedInstanceState.getFloat("mCorner")
            mSavedInstanceState = savedInstanceState
        }

        mImageInTemplateCount = intent.getIntExtra("imagesinTemplate", 0)
        val extraImagePaths = intent.getStringArrayListExtra("selectedImages")

        listBg!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        listBg!!.adapter = BackgroundAdapter(this, this)

        tabLayout!!.setOnClickListener(this)
        tabBorder!!.setOnClickListener(this)
        tabBg!!.setOnClickListener(this)

        seekbarSpace!!.setOnSeekBarChangeListener(space_listener())
        seekbarCorner!!.setOnSeekBarChangeListener(corner_listener())

        mPhotoView = PhotoView(this)
        rlContainer!!.getViewTreeObserver()
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mOutputScale = ImageUtils.calculateOutputScaleFactor(
                        rlContainer!!.getWidth(),
                        rlContainer!!.getHeight()
                    )
                    buildLayout(mSelectedTemplateItem!!)
                    // remove listener
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        rlContainer!!.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                    } else {
                        rlContainer!!.getViewTreeObserver().removeGlobalOnLayoutListener(this)
                    }
                }
            })

        img_background = findViewById<ImageView>(R.id.img_background)

        loadFrameImages()
        listFrames!!.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        frameAdapter = FrameAdapter(this, mTemplateItemList!!, this)
        listFrames!!.adapter = frameAdapter


        mSelectedTemplateItem = mTemplateItemList!!.get(0)
        mSelectedTemplateItem!!.isSelected = true

        if (extraImagePaths != null) {
            val size =
                Math.min(extraImagePaths.size, mSelectedTemplateItem!!.photoItemList.size)
            for (i in 0 until size)
                mSelectedTemplateItem!!.photoItemList[i].imagePath = extraImagePaths[i]
        }

        btnNext!!.setOnClickListener(this)
    }

    private fun changeTheme() {
        isDarkTheme = getSharedPreferences(
            resources.getString(R.string.app_name),
            MODE_PRIVATE
        ).getBoolean("isDarkTheme", false)
        // AdUtils.showNativeAd(
//            this@CollageActivity,
//            findViewById<LinearLayout>(R.id.native_ads),
//            false,
//            isDarkTheme
//        )
        if (isDarkTheme) {
            mainLayout!!.setBackgroundResource(R.color.blacktwo)
            layTxt!!.setTextColor(resources.getColor(R.color.pink))
            borTxt!!.setTextColor(resources.getColor(R.color.white))
            bgTxt!!.setTextColor(resources.getColor(R.color.white))
            backbt!!.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.white))
            icon_roundcorner!!.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.white))
            icon_space!!.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.white))

            layTxt!!.setCompoundDrawableTintList(ColorStateList.valueOf(resources.getColor(R.color.pink)))
            borTxt!!.setCompoundDrawableTintList(ColorStateList.valueOf(resources.getColor(R.color.white)))
            bgTxt!!.setCompoundDrawableTintList(ColorStateList.valueOf(resources.getColor(R.color.white)))
        } else {
            mainLayout!!.setBackgroundResource(R.color.white)
            layTxt!!.setTextColor(resources.getColor(R.color.pink))
            borTxt!!.setTextColor(resources.getColor(R.color.blacktwo))
            bgTxt!!.setTextColor(resources.getColor(R.color.blacktwo))
            backbt!!.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.blacktwo))
            icon_roundcorner!!.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.blacktwo))
            icon_space!!.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.blacktwo))
            layTxt!!.setCompoundDrawableTintList(ColorStateList.valueOf(resources.getColor(R.color.pink)))
            borTxt!!.setCompoundDrawableTintList(ColorStateList.valueOf(resources.getColor(R.color.blacktwo)))
            bgTxt!!.setCompoundDrawableTintList(ColorStateList.valueOf(resources.getColor(R.color.blacktwo)))
        }
    }

    private fun loadFrameImages() {
        val mAllTemplateItemList = java.util.ArrayList<TemplateItem>()

        mAllTemplateItemList.addAll(FrameImageUtils.loadFrameImages(this))

        mTemplateItemList = java.util.ArrayList<TemplateItem>()
        if (mImageInTemplateCount > 0) {
            for (item in mAllTemplateItemList)
                if (item.photoItemList.size === mImageInTemplateCount) {
                    mTemplateItemList!!.add(item)
                }
        } else {
            mTemplateItemList!!.addAll(mAllTemplateItemList)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putFloat("mSpace", mSpace)
        outState.putFloat("mCornerBar", mCorner)
        if (mFramePhotoLayout != null) {
            mFramePhotoLayout!!.saveInstanceState(outState)
        }

    }

    fun buildLayout(item: TemplateItem) {
        mFramePhotoLayout = FramePhotoLayout(this, item.photoItemList)

        if (mBackgroundImage != null && !mBackgroundImage!!.isRecycled()) {
            if (Build.VERSION.SDK_INT >= 16)
                rlContainer!!.setBackground(BitmapDrawable(resources, mBackgroundImage))
            else
                rlContainer!!.setBackgroundDrawable(BitmapDrawable(resources, mBackgroundImage))
        } else {
            rlContainer!!.setBackgroundColor(mblacktwo)
        }

        var viewWidth = rlContainer!!.getWidth()
        var viewHeight = rlContainer!!.getHeight()
        if (mLayoutRatio === RATIO_SQUARE) {
            if (viewWidth > viewHeight) {
                viewWidth = viewHeight
            } else {
                viewHeight = viewWidth
            }
        } else if (mLayoutRatio === RATIO_GOLDEN) {
            val goldenRatio = 1.61803398875
            if (viewWidth <= viewHeight) {
                if (viewWidth * goldenRatio >= viewHeight) {
                    viewWidth = (viewHeight / goldenRatio).toInt()
                } else {
                    viewHeight = (viewWidth * goldenRatio).toInt()
                }
            } else if (viewHeight <= viewWidth) {
                if (viewHeight * goldenRatio >= viewWidth) {
                    viewHeight = (viewWidth / goldenRatio).toInt()
                } else {
                    viewWidth = (viewHeight * goldenRatio).toInt()
                }
            }
        }

        mOutputScale = ImageUtils.calculateOutputScaleFactor(viewWidth, viewHeight)
        mFramePhotoLayout!!.build(viewWidth, viewHeight, mOutputScale, mSpace, mCorner)
        if (mSavedInstanceState != null) {
            mFramePhotoLayout!!.restoreInstanceState(mSavedInstanceState!!)
            mSavedInstanceState = null
        }
        val params = RelativeLayout.LayoutParams(viewWidth, viewHeight)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        rlContainer!!.removeAllViews()

        rlContainer!!.removeView(img_background)
        rlContainer!!.addView(img_background, params)

        rlContainer!!.addView(mFramePhotoLayout, params)
        //add sticker view
        rlContainer!!.removeView(mPhotoView)
        rlContainer!!.addView(mPhotoView, params)
        //reset space and corner seek bars

        seekbarSpace!!.setProgress((MAX_SPACE_PROGRESS * mSpace / MAX_SPACE).toInt())
        seekbarCorner!!.setProgress((MAX_CORNER_PROGRESS * mCorner / MAX_CORNER).toInt())
    }

    @Throws(OutOfMemoryError::class)
    fun createOutputImage(): Bitmap {
        try {
            var template = mFramePhotoLayout!!.createImage()
            Log.e("template", template!!.width.toString());
            val result =
                Bitmap.createBitmap(template!!.width, template.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(result)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            if (mBackgroundImage != null && !mBackgroundImage!!.isRecycled()) {
                canvas.drawBitmap(
                    mBackgroundImage!!,
                    Rect(0, 0, mBackgroundImage!!.getWidth(), mBackgroundImage!!.getHeight()),
                    Rect(0, 0, result.width, result.height),
                    paint
                )
            } else {
                canvas.drawColor(mblacktwo)
            }

            canvas.drawBitmap(template, 0f, 0f, paint)
            template.recycle()
            var stickers = mPhotoView.getImage(mOutputScale)
            canvas.drawBitmap(stickers!!, 0f, 0f, paint)
            stickers.recycle()
            stickers = null
            System.gc()
            return result
        } catch (error: OutOfMemoryError) {
            throw error
        }
    }

    override fun onBackPressed() {
        if (backVal.equals("add")) {
            super.onBackPressed()

        } else {
//            startActivity(Intent(this, DashboardActivity::class.java))
            setOnBackPressDialog()
        }

    }

    private fun setOnBackPressDialog() {
        val dialogOnBackPressed = Dialog(this, R.style.Theme_Dialog)
        dialogOnBackPressed.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogOnBackPressed.setCancelable(false)
        dialogOnBackPressed.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val window = dialogOnBackPressed.window
        val wlp = window!!.attributes
        getWindow().setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        wlp.gravity = Gravity.BOTTOM
        window.attributes = wlp
        dialogOnBackPressed.setContentView(R.layout.dialog_exit)
        val textViewCancel: TextView
        val textViewDiscard: TextView
        textViewCancel = dialogOnBackPressed.findViewById(R.id.textViewCancel)
        textViewDiscard = dialogOnBackPressed.findViewById(R.id.textViewDiscard)
        textViewCancel.setOnClickListener { view: View? -> dialogOnBackPressed.dismiss() }
        textViewDiscard.setOnClickListener { view: View? ->
            dialogOnBackPressed.dismiss()
            startActivity(Intent(applicationContext, DashboardActivity::class.java))
            this@CollageActivity.finish()
            finish()
        }
        dialogOnBackPressed.show()
    }

}
