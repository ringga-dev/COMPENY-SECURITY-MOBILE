package com.ringga.security.ui.home.fm

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.ringga.security.R
import com.ringga.security.ui.history.DaftarVisitorActivity
import com.ringga.security.ui.history.DaftarVisitorBerjalanActivity
import com.ringga.security.ui.history.DaftarVisitorPlanActivity
import com.ringga.security.ui.scan.HistoryVisitorActivity
import com.ringga.security.ui.scan.VisitActivity
import kotlinx.android.synthetic.main.fragment_visitor.*


class VisitorFragment : Fragment() {
    companion object {
        fun newInstance() = VisitorFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_visitor, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btn_izin.setOnClickListener {
            activity?.startActivity(Intent(requireContext(), HistoryVisitorActivity::class.java))
        }

        btn_scan_visitor.setOnClickListener {
            showCustomAlertVisitor()
        }
        btn_visit.setOnClickListener {
            showCustomAlert()
        }

    }
    private fun showCustomAlert() {

        val inflate = layoutInflater
        val infla_view = inflate.inflate(R.layout.alert_visit, null)
        val jadwal_visitor = infla_view.findViewById<CardView>(R.id.btn_jadwal_visitor)
        val daftar_visitor = infla_view.findViewById<CardView>(R.id.btn_daftar_visitor)
        val visitor_now = infla_view.findViewById<CardView>(R.id.btn_visitor_now)
        val cencel = infla_view.findViewById<ImageView>(R.id.btn_cencel)
        jadwal_visitor.setOnClickListener {
            activity?.startActivity(Intent(requireContext(), DaftarVisitorPlanActivity::class.java))
        }

        daftar_visitor.setOnClickListener {
            activity?.startActivity(Intent(requireContext(), DaftarVisitorActivity::class.java))
        }

        visitor_now.setOnClickListener {
            activity?.startActivity(Intent(requireContext(), DaftarVisitorBerjalanActivity::class.java))
        }

        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setView(infla_view)
        alertDialog.setCancelable(false)


        val dialog = alertDialog.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        cencel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }
    private fun showCustomAlertVisitor() {

        val inflate = layoutInflater
        val infla_view = inflate.inflate(R.layout.custom_alert_visitor, null)
        val datang = infla_view.findViewById<CardView>(R.id.btn_datang)
        val pulang = infla_view.findViewById<CardView>(R.id.btn_pulang)
        val cencel = infla_view.findViewById<ImageView>(R.id.btn_cencel)
        datang.setOnClickListener {
            val i = Intent(requireContext(), VisitActivity::class.java)
            i.putExtra("stts", "datang")
            activity?.startActivity(i)

        }

        pulang.setOnClickListener {
            val i = Intent(requireContext(), VisitActivity::class.java)
            i.putExtra("stts", "pulang")
            activity?.startActivity(i)

        }

        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setView(infla_view)
        alertDialog.setCancelable(false)

        val dialog = alertDialog.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        cencel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }
}