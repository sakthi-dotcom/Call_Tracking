package com.example.calltracking

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calltracking.dataclass.CallLogEntity

class CallAdapter : RecyclerView.Adapter<CallAdapter.ViewHolder>() {

    private var list = listOf<CallLogEntity>()

    fun submitList(data: List<CallLogEntity>) {
        list = data
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNumber: TextView = view.findViewById(R.id.tvNumber)
        val tvType: TextView = view.findViewById(R.id.tvType)
        val tvDuration: TextView = view.findViewById(R.id.tvDuration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_call, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.tvNumber.text = item.number
        holder.tvType.text = item.type
        holder.tvDuration.text = formatDuration(item.duration)

        when (item.type) {
            "INCOMING" -> holder.tvType.setTextColor(Color.parseColor("#00E676"))
            "OUTGOING" -> holder.tvType.setTextColor(Color.parseColor("#FF5252"))
            "MISSED" -> holder.tvType.setTextColor(Color.parseColor("#AAAAAA"))
            else -> holder.tvType.setTextColor(Color.WHITE)
        }
    }

    private fun formatDuration(ms: Long): String {
        val seconds = ms / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02dm:%02ds", minutes, remainingSeconds)
    }
}