package com.ringga.security.ui.auth

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.ringga.security.R
import com.ringga.security.data.api.RetrofitClient
import com.ringga.security.data.model.ResponAbsen
import com.ringga.security.data.model.auth.BaseRespon
import com.ringga.security.data.model.auth.LoginRespon
import com.ringga.security.database.PreferencesToken
import com.ringga.security.database.PreferencesTokenFirebase
import com.ringga.security.database.SharedPrefManager
import com.ringga.security.ui.home.HomeActivity
import com.ringga.security.util.snackbar
import com.ringga.security.util.toast
import kotlinx.android.synthetic.main.activity_absen_scan.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.costum_scan.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ScanLoginActivity : AppCompatActivity() {
    /**
     * fungsi scan
     * */
    lateinit var mTTS: TextToSpeech
    private lateinit var captureManager: CaptureManager
    private var torchState: Boolean = false

    private var scanContinuousState: Boolean = false
    private lateinit var scanContinuousBG: Drawable
    lateinit var beepManager: BeepManager
    private var lastScan = Date()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_login)

        mTTS = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                //if there is no error then set language
                mTTS.language = Locale("id", "ID")
            }
        })
        barcodeView.setOnClickListener {
            barcodeView.cameraSettings.focusMode
        }


        captureManager = CaptureManager(this, barcodeView)
        captureManager.initializeFromIntent(intent, savedInstanceState)

        beepManager = BeepManager(this)
        beepManager.isVibrateEnabled = true


        scanContinuousBG = btnScanContinuous.background

        val callback = object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    val current = Date()
                    txtResultContinuous.text = it.text
                    beepManager.playBeepSoundAndVibrate()

                    lastScan = current

                    animateBackground()
                    scanContinuousState = !scanContinuousState
                    btnScanContinuous.background = scanContinuousBG
                    barcodeView.barcodeView.stopDecoding()
                    toast(this@ScanLoginActivity, it.text)
                    /**
                     * pengecekan hasil scan
                     * */
                    RetrofitClient.instance.loginScan(it.text).enqueue(
                        object : Callback<LoginRespon> {
                            override fun onResponse(
                                call: Call<LoginRespon>,
                                response: Response<LoginRespon>
                            ) {
                                if (response.body()?.stts == true) {
                                    response.body()?.data?.let { it1 ->
                                        SharedPrefManager.getInstance(applicationContext)!!
                                            .saveUser(response.body()?.data!!)

                                        PreferencesToken.setToken(
                                            this@ScanLoginActivity,
                                            response.body()?.key?.token
                                        )

                                        saveToken(response.body()?.data?.id_bet, response.body()?.key?.token!!)
                                        val intent = Intent(applicationContext, HomeActivity::class.java)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                                        startActivity(intent)
                                        finish()
                                    }
                                } else {
                                    snackbar(response.body()?.msg.toString(),this@ScanLoginActivity.window.decorView.rootView)
                                }
                            }

                            override fun onFailure(call: Call<LoginRespon>, t: Throwable) {
                                snackbar(t.message.toString(), this@ScanLoginActivity.window.decorView.rootView)
                            }
                        }
                    )

//
                }
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
            }
        }



        btnScanContinuous.setOnClickListener {
            if (!scanContinuousState) {
                scanContinuousState = !scanContinuousState
                txtResultContinuous.text = "scanning..."
                barcodeView.decodeContinuous(callback)
            } else {
                scanContinuousState = !scanContinuousState
                btnScanContinuous.background = scanContinuousBG
                barcodeView.barcodeView.stopDecoding()
            }
        }

        switch_flashlight.setOnClickListener {
            if (torchState) {
                torchState = false
                barcodeView.setTorchOff()
                switch_flashlight.setBackgroundResource(R.drawable.bg_senter_off)
            } else {
                switch_flashlight.setBackgroundResource(R.drawable.bg_senter_on)
                torchState = true
                barcodeView.setTorchOn()
            }
        }
    }

    private fun saveToken(id: String?, token_user: String) {
        val token = PreferencesTokenFirebase.getToken(this).toString()

        RetrofitClient.instance.saveToken(token_user, token, id.toString()).enqueue(
            object : Callback<BaseRespon> {
                override fun onResponse(call: Call<BaseRespon>, response: Response<BaseRespon>) {

                }

                override fun onFailure(call: Call<BaseRespon>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

            }
        )

    }



    override fun onPause() {
        super.onPause()
        captureManager.onPause()
        if (mTTS.isSpeaking) {
            //if speaking then stop
            mTTS.stop()
            //mTTS.shutdown()
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        captureManager.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        captureManager.onDestroy()
    }

    private fun animateBackground() {
        val colorFrom = resources.getColor(R.color.purple_700)
        val colorTo = resources.getColor(R.color.teal_200)
        val colorAnimation =
            ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimation.duration = 250 // milliseconds

        colorAnimation.addUpdateListener { animator ->
            txtResultContinuous.setBackgroundColor(
                animator.animatedValue as Int
            )
        }
        colorAnimation.start()
    }

}
