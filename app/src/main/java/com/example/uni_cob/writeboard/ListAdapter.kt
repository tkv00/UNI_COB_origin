package com.example.uni_cob.writeboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uni_cob.R

class ListAdapter (
    private val context:Context,
    private val itemList: ArrayList<ListLayout>,
    private val itemClickListener: OnItemClickListener): RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_map, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.name.text = item.name
        holder.road.text = item.road
        holder.address.text = item.address

        // '선택' 버튼에 리스너를 설정합니다.
        holder.selectButton.setOnClickListener {
            // 리스너를 통해 'road' 텍스트를 WriteBoard 클래스로 전달합니다.
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