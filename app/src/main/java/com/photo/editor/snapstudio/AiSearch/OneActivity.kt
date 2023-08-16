package com.photo.editor.snapstudio.AiSearch


import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.photo.editor.snapstudio.Activity.AiSearchResultActivity
import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils
import com.photo.editor.snapstudio.AdsUtils.Interfaces.AppInterfaces
import com.photo.editor.snapstudio.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OneActivity : AppCompatActivity() {
    companion object {
        var TOKEN = ""
    }

    var imageDataList: java.util.ArrayList<String>? = null
    var promptEditText: EditText? = null
    var generateButton: TextView? = null
    var txt: TextView? = null
    var title: TextView? = null
    var isDarkTheme = false
    var mainLayout: RelativeLayout? = null
    var backbt: ImageView? = null

    var size256: RadioButton? = null
    var size512: RadioButton? = null
    var size1024: RadioButton? = null
    var progressDialog: ProgressDialog? = null

    var db = FirebaseFirestore.getInstance()
    var query: Task<DocumentSnapshot>? = null

    private val viewModel: GenerateImageViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(1)
        window.setFlags(1024, 1024)
        setContentView(R.layout.activity_one)
        promptEditText = findViewById(R.id.promptEditText)
        generateButton = findViewById(R.id.generateButton)
        size256 = findViewById(R.id.size256)
        size512 = findViewById(R.id.size512)
        size1024 = findViewById(R.id.size1024)
        mainLayout = findViewById(R.id.main_layout)
        backbt = findViewById(R.id.backbt)
        txt = findViewById(R.id.txt)
        title = findViewById(R.id.title)

        changeTheme()
        initViewCollect()

        query = db.collection("OpenAiToken").document("OpenAiToken").get().addOnCompleteListener(
            OnCompleteListener<DocumentSnapshot> { task ->
                val snapshot = task.result
                if (snapshot.exists()) {
                    try {
                        TOKEN = snapshot.getString("Token").toString()
                        println("Ashish>>>>" + TOKEN)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }).addOnFailureListener(OnFailureListener { e -> println(e) })


    }

    override fun onResume() {
        super.onResume()
        AdUtils.loadInitialInterstitialAds(this)
        AdUtils.loadAppOpenAds(this)
        AdUtils.loadInitialNativeList(this)
        AdUtils.loadInitialRewardedAds(this)
    }

    private fun initViewCollect() {
        with(viewModel) {

            generateButton!!.setOnClickListener {
                AdUtils.showRewardAd(this@OneActivity, object : AppInterfaces.InterstitialADInterface {
                    override fun adLoadState(isLoaded: Boolean) {
                        // Your code here
                        if (promptEditText!!.text.toString().isEmpty().not()) {
                            val imageSize = if (size1024!!.isChecked) {
                                Sizes.SIZE_1024
                            } else if (size512!!.isChecked) {
                                Sizes.SIZE_512
                            } else {
                                Sizes.SIZE_256
                            }
                            showProgressDialog(this@OneActivity, "Loading...")
                            generateImage(promptEditText!!.text.toString(), 4, imageSize)
                        } else {
                            promptEditText!!.error = getString(R.string.enter_prompt)
                        }
                    }
                })
            }

            promptEditText!!.setOnFocusChangeListener { view, b ->
                if (b) {

                }
            }

            backbt!!.setOnClickListener { onBackPressed() }
//                generatedImageCard.applyClickShrink()
//                generatedImageCard2.applyClickShrink()
//                generatedImageCard3.applyClickShrink()
//                generatedImageCard4.applyClickShrink()

            lifecycleScope.launchWhenStarted {
                state.collect { response ->
                    when (response) {
                        is Resource.Loading -> {
//                                binding.shimmerLayout.apply {
//                                    startShimmer()
//                                    visible()
//                                }
//                                binding.generatedImagesGrid.gone()
                        }
                        is Resource.Success -> {
//                                binding.shimmerLayout.apply {
//                                    stopShimmer()
//                                    gone()
//                                }
//                                binding.generatedImagesGrid.visible()
//
                            val imageDataList = ArrayList<String>()
                            imageDataList.add(response.data.data[0].url)
                            imageDataList.add(response.data.data[1].url)
                            imageDataList.add(response.data.data[2].url)
                            imageDataList.add(response.data.data[3].url)

                            hideProgressDialogg()

//                            AdUtils.showRewardAd(this@OneActivity, object : AppInterfaces.InterstitialADInterface {
//                                override fun adLoadState(isLoaded: Boolean) {
                                    val intent =
                                        Intent(
                                            this@OneActivity,
                                            AiSearchResultActivity::class.java
                                        ).apply {
                                            putStringArrayListExtra("data", imageDataList)
                                        }
                                    startActivity(intent)
                            finish()
//                                }
//                            })
//                                binding.generatedImageView.glideImage(response.data.data[0].url)
//                                binding.generatedImageView2.glideImage(response.data.data[1].url)
//                                generatedImageView3.glideImage(response.data.data[2].url)
//                                generatedImageView4.glideImage(response.data.data[3].url)
                            Log.d(
                                "",
                                "initViewCollect: --------------------------------------------------->" + response.data.data[0].url
                            )
                            Log.d(
                                "",
                                "initViewCollect: --------------------------------------------------->" + response.data
                            )

//                                generatedImageCard.setOnClickListener {
//                                    showImageFullPage(response.data.data[0].url)
//                                }
//                                generatedImageCard2.setOnClickListener {
//                                    showImageFullPage(response.data.data[1].url)
//                                }
//                                generatedImageCard3.setOnClickListener {
//                                    showImageFullPage(response.data.data[2].url)
//                                }
//                                generatedImageCard4.setOnClickListener {
//                                    showImageFullPage(response.data.data[3].url)
//                                }
                        }
                        is Resource.Error -> {
//                                shimmerLayout.apply {
//                                    stopShimmer()
//                                    gone()
//                                }
//                                generatedImagesGrid.gone()


//                                MotionToast.createColorToast(
//                                    this@OneActivity,
//                                    getString(R.string.error),
//                                    response.throwable.localizedMessage ?: "Error",
//                                    MotionToastStyle.ERROR,
//                                    MotionToast.GRAVITY_TOP or MotionToast.GRAVITY_CENTER,
//                                    MotionToast.LONG_DURATION,
//                                    null
//                                )

                            Log.e("Response", response.throwable.localizedMessage ?: "Error")
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun showProgressDialog(context: Context?, msg: String?) {
        progressDialog = ProgressDialog(context)
        progressDialog!!.setTitle(null)
        progressDialog!!.setMessage(msg)
        progressDialog!!.setIndeterminate(false)
        progressDialog!!.setCanceledOnTouchOutside(false)
        progressDialog!!.setCancelable(false)
        try {
            if (progressDialog != null && !progressDialog!!.isShowing()) progressDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideProgressDialogg() {
        try {
            if (progressDialog != null && progressDialog!!.isShowing()) progressDialog!!.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun changeTheme() {
        isDarkTheme = getSharedPreferences(
            resources.getString(R.string.app_name),
            MODE_PRIVATE
        ).getBoolean("isDarkTheme", false)

        AdUtils.showNativeAd(
            this@OneActivity,
            findViewById<LinearLayout>(R.id.native_ads),
            true,
            isDarkTheme, false
        )
        if (isDarkTheme) {
            mainLayout!!.setBackgroundResource(R.drawable.darkbg_t)
            generateButton!!.setTextColor(resources.getColor(R.color.blacktwo))
            txt!!.setTextColor(resources.getColor(R.color.greyt))
            title!!.setTextColor(resources.getColor(R.color.white))
            generateButton!!.setBackgroundResource(R.drawable.curvebt3)
            promptEditText!!.setHintTextColor(resources.getColor(R.color.greyt))
            promptEditText!!.setTextColor(resources.getColor(R.color.white))
            backbt!!.setImageTintList(ColorStateList.valueOf(resources.getColor(R.color.white)))
        } else {
            mainLayout!!.setBackgroundColor(resources.getColor(R.color.white))
            generateButton!!.setTextColor(resources.getColor(R.color.white))
            txt!!.setTextColor(Color.parseColor("#4D000000"))
            title!!.setTextColor(resources.getColor(R.color.blacktwo))
            generateButton!!.setBackgroundResource(R.drawable.curvebt)
            promptEditText!!.setHintTextColor(Color.parseColor("#4D000000"))
            promptEditText!!.setTextColor(resources.getColor(R.color.blacktwo))
            backbt!!.setImageTintList(ColorStateList.valueOf(resources.getColor(R.color.blacktwo)))
        }
    }


}
