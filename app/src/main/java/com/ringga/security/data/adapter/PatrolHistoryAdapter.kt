package com.ringga.security.data.adapter
/*=================== T H A N K   Y O U ===================*/
/*============= TELAH MENGUNAKAN CODE SAYA ================*/
            /* https://github.com/ringga-dev */
/*=========================================================*/
/*     R I N G G A   S E P T I A  P R I B A D I            */
/*=========================================================*/
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ringga.security.R
import com.ringga.security.data.model.historiPatrol.Data
import kotlinx.android.synthetic.main.card_list_qr_location.view.*
import kotlinx.android.synthetic.main.card_list_visitor.view.*
import java.util.*

class PatrolHistoryAdapter(
    private var wallpaper: MutableList<Data>,
    private var context: Context
) : RecyclerView.Adapter<PatrolHistoryAdapter.ViewHolder>() {

    /**
     * file ini berfungsi untuk menampilkan data patroli yang sudah di lakukan user
     * */
    fun setWallpapers(r: List<Data>) {
        wallpaper.clear()
        wallpaper.addAll(r)
        notifyDataSetChanged()
    }

    /**
     * membagun ui
     * */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.card_list_qr_location, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(wallpaper[position], context)

    override fun getItemCount() = wallpaper.size


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(wallpaper: Data, context: Context) {
            itemView.tv_idqr.text = "ID QR :  ${wallpaper.qr_code}"
            itemView.tv_lot.text = "time :  ${wallpaper.tgl}"

//            itemView.cart_item.setOnClickListener {
//                val i = Intent(context, DitailVisitorActivity::class.java)
//                i.putExtra("description", wallpaper.description)
//                context.startActivity(i)
//            }
        }
    }
}