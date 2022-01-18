package com.ringga.security.ui.home.fm

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ringga.security.R
import com.ringga.security.ui.history.HistoryPatrolActivity
import com.ringga.security.ui.patrol.ListPatrolActivity
import com.ringga.security.ui.profile.ProfileActivity
import com.ringga.security.ui.scan.PatrolActivity
import kotlinx.android.synthetic.main.fragment_patrol.*


class PatrolFragment : Fragment() {
    companion object {
        fun newInstance() = PatrolFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patrol, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btn_patrol.setOnClickListener {
            activity?.startActivity(Intent(requireContext(), PatrolActivity::class.java))
        }
        btn_history_patrol.setOnClickListener {
            activity?.startActivity(Intent(requireContext(), HistoryPatrolActivity::class.java))
        }
        btn_profile.setOnClickListener {
            activity?.startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }
        tv_list_qrcode.setOnClickListener {
            activity?.startActivity(Intent(requireContext(), ListPatrolActivity::class.java))
        }


    }

}