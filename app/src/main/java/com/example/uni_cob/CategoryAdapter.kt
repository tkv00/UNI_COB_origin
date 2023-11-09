package com.example.uni_cob

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uni_cob.utility.Category

class CategoryAdapter(private val context: Context, private val categories: List<Category>) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageViewCategory)
        val textView: TextView = view.findViewById(R.id.textViewCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.imageView.setImageResource(category.imageResId)
        holder.textView.text = category.text
        holder.itemView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(category.url))
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = categories.size
}
