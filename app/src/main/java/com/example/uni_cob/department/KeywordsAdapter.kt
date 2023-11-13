package com.example.uni_cob.department
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uni_cob.R
import com.example.uni_cob.department.keywords.Lecture
import java.util.Locale

class KeywordsAdapter(private var lectures: List<Lecture>) :
    RecyclerView.Adapter<KeywordsAdapter.KeywordViewHolder>() ,Filterable{
    var filteredList: List<Lecture> = lectures
    // ViewHolder 클래스 정의
    class KeywordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val professorNameTextView: TextView = view.findViewById(R.id.text_item_description1)
        val locationTextView: TextView = view.findViewById(R.id.text_item_description2)
        val lectureNameTextView: TextView = view.findViewById(R.id.text_item_title)

        val imageView:ImageView=view.findViewById(R.id.image_item)
    }

    // ViewHolder를 생성하는 메소드
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeywordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false) // 여기서 'lecture_item_layout'은 각 항목을 위한 레이아웃 파일입니다.
        return KeywordViewHolder(view)
    }

    // 데이터를 ViewHolder에 바인딩하는 메소드
    override fun onBindViewHolder(holder: KeywordViewHolder, position: Int) {
        val lecture = lectures[position]
        holder.professorNameTextView.text = lecture.teacher
        holder.locationTextView.text = lecture.where
        holder.lectureNameTextView.text = lecture.lecturnName
        holder.imageView.setOnClickListener {
            // URL 형식을 확인합니다. 유효한 웹 주소인지 확인합니다.
            val url = lecture.getUrl
            if (URLUtil.isValidUrl(url)) {
                // 유효한 URL인 경우, 웹 브라우저를 통해 열도록 인텐트를 설정합니다.
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                holder.itemView.context.startActivity(intent)
            } else {
                // URL이 유효하지 않은 경우, 사용자에게 알립니다.
                Toast.makeText(holder.itemView.context, "유효하지 않은 URL입니다.", Toast.LENGTH_LONG).show()
            }
        }
        // 이미지 URL을 생성합니다. 예를 들어, URL이 특정 패턴을 따른다고 가정합니다.
        val imageUrl = lecture.getUrl!!.replace("cview.do?cid=", "common/contents/thumbnail/07/t") + ".jpg"
        // Glide를 사용하여 이미지를 로드하고 ImageView에 적용합니다.
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.noimg) // 기본 이미지 설정
            .error(R.drawable.noimg) // 로드 실패 시 기본 이미지 설정
            .into(holder.imageView)


    }

    // 데이터를 갱신하는 메소드
    fun updateData(newLectures: List<Lecture>) {
        this.lectures = newLectures
        notifyDataSetChanged() // 어댑터에 데이터가 변경되었음을 알립니다.
    }
    fun getData(): List<Lecture> {
        return lectures
    }
    // 데이터셋의 크기를 반환하는 메소드
    override fun getItemCount() = lectures.size
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                filteredList = if (charSearch.isEmpty()) {
                    lectures
                } else {
                    val resultList = ArrayList<Lecture>()
                    for (row in lectures) {
                        // 여기서 검색 조건을 정의합니다.
                        // 예를 들어, 강의명과 교수명으로 필터링을 할 수 있습니다.
                        if (row.lecturnName?.lowercase(Locale.ROOT)?.contains(charSearch.lowercase(Locale.ROOT)) == true ||
                                    row.teacher?.lowercase(Locale.ROOT)?.contains(charSearch.lowercase(Locale.ROOT)) == true) {
                                resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as List<Lecture>
                notifyDataSetChanged()
            }
        }
    }
}
