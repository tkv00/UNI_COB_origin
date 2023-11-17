package com.example.uni_cob.writeboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uni_cob.R

class ListAdapter (private val itemList: ArrayList<ListLayout>,
    private val itemClickListener: OnItemClickListener): RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_map, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = itemList[position].name
        holder.road.text = itemList[position].road
        holder.address.text = itemList[position].address
        holder.selectButton.setOnClickListener {
            // 버튼 클릭시 처리할 로직
            itemClickListener.onSelectClick(position)
        }


// 아이템 클릭 이벤트
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_list_name)
        val road: TextView = itemView.findViewById(R.id.tv_list_road)
        val address: TextView = itemView.findViewById(R.id.tv_list_address)
        val selectButton:Button=itemView.findViewById(R.id.btn_map_select)
    }

    interface OnItemClickListener {
        fun onSelectClick(position: Int)
        fun onClick(v: View, position: Int)
    }


}