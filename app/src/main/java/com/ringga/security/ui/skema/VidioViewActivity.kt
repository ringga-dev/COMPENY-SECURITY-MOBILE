package com.ringga.security.ui.skema
/*=================== T H A N K   Y O U ===================*/
/*============= TELAH MENGUNAKAN CODE SAYA ================*/
/* https://github.com/ringga-dev */
/*=========================================================*/
/*     R I N G G A   S E P T I A  P R I B A D I            */
/*=========================================================*/

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import com.ringga.security.R

import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter

import es.voghdev.pdfviewpager.library.RemotePDFViewPager

import androidx.constraintlayout.widget.ConstraintLayout
import es.voghdev.pdfviewpager.library.remote.DownloadFile
import java.lang.Exception
import es.voghdev.pdfviewpager.library.util.FileUtil
import com.ringga.security.data.api.RetrofitClient.BASE_URL


class VidioViewActivity : AppCompatActivity(), DownloadFile.Listener {


    var root: ConstraintLayout? = null
    var remotePDFViewPager: RemotePDFViewPager? = null
    var adapter: PDFPagerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vidio_view)
        root = findViewById(R.id.remote_pdf_root);

        setDownloadButtonListener();

    }

    override fun onDestroy() {
        super.onDestroy()
        if (adapter != null) {
            adapter!!.close()
        }
    }

    protected fun setDownloadButtonListener() {

        val ctx: Context = this
        val listener: DownloadFile.Listener = this

        remotePDFViewPager = RemotePDFViewPager(ctx, getUrlFromEditText(), listener)
        remotePDFViewPager!!.id = R.id.pdfViewPager


    }

    protected fun getUrlFromEditText(): String {
        return "$BASE_URL" + "assets/pdf/tutor.pdf"
    }


    fun updateLayout() {
        root!!.removeAllViewsInLayout()

        root!!.addView(
            remotePDFViewPager,
            ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onSuccess(url: String?, destinationPath: String?) {
        adapter = PDFPagerAdapter(this, FileUtil.extractFileNameFromURL(url))
        remotePDFViewPager!!.adapter = adapter
        updateLayout()
    }

    override fun onFailure(e: Exception) {
        e.printStackTrace()
    }

    override fun onProgressUpdate(progress: Int, total: Int) {}

}