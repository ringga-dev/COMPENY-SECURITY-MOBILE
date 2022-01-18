package com.ringga.security.ui.home.fm

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ringga.security.R
import com.ringga.security.ui.history.DaftarShiftActivity
import com.ringga.security.ui.scan.AbsenScanActivity
import com.ringga.security.ui.scan.GagalFingerActivity
import com.ringga.security.ui.ui_user.UserLateActivity
import kotlinx.android.synthetic.main.fragment_etowa_user.*


class EtowaUserFragment : Fragment() {
    companion object {
        fun newInstance() = EtowaUserFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_etowa_user, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        btn_failed_for_finger.setOnClickListener {
            activity?.startActivity(Intent(requireContext(), GagalFingerActivity::class.java))
        }
        user_late.setOnClickListener {
            activity?.startActivity(Intent(requireContext(), UserLateActivity::class.java))
        }
        btn_user_late.setOnClickListener {
            activity?.startActivity(Intent(requireContext(), DaftarShiftActivity::class.java))
        }
        btn_pindah.setOnClickListener {
            activity?.startActivity(Intent(requireContext(), AbsenScanActivity::class.java))
//            startActivity(Intent(this, EditProfileActivity::class.java))
        }

    }

}