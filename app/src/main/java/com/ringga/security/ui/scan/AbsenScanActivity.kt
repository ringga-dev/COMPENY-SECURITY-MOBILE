package com.ringga.security.ui.scan

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.ringga.security.R
import com.ringga.security.data.api.RetrofitClient
import com.ringga.security.data.model.ResponAbsen
import kotlinx.android.synthetic.main.activity_shift_scan.*
import kotlinx.android.synthetic.main.costum_scan.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import com.journeyapps.barcodescanner.ScanOptions

import android.R.string.no
import android.graphics.Camera
import com.journeyapps.barcodescanner.camera.AutoFocusManager
import android.R.string.no
import com.google.zxing.integration.android.IntentIntegrator

import android.R.string.no
import com.journeyapps.barcodescanner.camera.CameraManager


class AbsenScanActivity : AppCompatActivity() {
    lateinit var mTTS: TextToSpeech
    private lateinit var captureManager: CaptureManager
    private var torchState: Boolean = false

    private var scanContinuousState: Boolean = false
    private lateinit var scanContinuousBG: Drawable
    lateinit var beepManager: BeepManager
    private var lastScan = Date()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_absen_scan)

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

        var callback = object : BarcodeCallback {
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

                    RetrofitClient.instance.absen(it.text).enqueue(
                        object : Callback<ResponAbsen> {
                            override fun onResponse(
                                call: Call<ResponAbsen>,
                                response: Response<ResponAbsen>
                            ) {

                                val toSpeak = response.body()?.txt


                                if (toSpeak == "") {
                                    //if there is no text in edit text


                                    showAlertSuccess("Qr Dalam keadaan kosong")
                                } else {
                                    val data_text =
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                            Html.fromHtml(
                                                response.body()?.msg,
                                                Html.FROM_HTML_MODE_COMPACT
                                            )
                                        } else {
                                            Html.fromHtml(response.body()?.msg)
                                        }
                                    //if there is text in edit text
                                    showAlertSuccess(data_text.toString())
                                    mTTS.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null)
                                }
                            }

                            override fun onFailure(call: Call<ResponAbsen>, t: Throwable) {
                                showAlertSuccess(t.message.toString())
                            }
                        }
                    )


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

    private fun showAlertSuccess(text: String) {
        val inflate = layoutInflater
        val infla_view = inflate.inflate(R.layout.custom_alert_ok, null)
        val cencel = infla_view.findViewById<ImageView>(R.id.btn_close)
        val tv_text = infla_view.findViewById<TextView>(R.id.tv_text)
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setView(infla_view)
        alertDialog.setCancelable(false)

        val dialog = alertDialog.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        tv_text.text = text
        cencel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showAlertError(text: String) {
        val inflate = layoutInflater
        val infla_view = inflate.inflate(R.layout.custom_alert_error, null)
        val cencel = infla_view.findViewById<ImageView>(R.id.btn_close)
        val tv_text = infla_view.findViewById<TextView>(R.id.tv_text)
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setView(infla_view)
        alertDialog.setCancelable(false)

        val dialog = alertDialog.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        tv_text.text = text
        cencel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
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

