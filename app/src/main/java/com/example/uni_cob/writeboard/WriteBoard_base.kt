package com.example.uni_cob.writeboard

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uni_cob.MainActivity
import com.example.uni_cob.R
import com.example.uni_cob.utility.Board1
import com.example.uni_cob.utility.Board2
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar
import java.util.Locale

class WriteBoard_base : AppCompatActivity() {
    private lateinit var btn_select: Button
    private lateinit var et_write: EditText
    private lateinit var btn_cancel1: Button
    private lateinit var btn_cancel2: Button
    private lateinit var btn_category: Button
    private lateinit var et_title: EditText
    private var selectedLocation: String? = null
    private val startForResult=registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedLocation = result.data?.getStringExtra("roadAddress")
            findViewById<Button>(R.id.btn_take_where).text = selectedLocation ?: "선택된 위치가 없습니다."
        }
    }


    private lateinit var delete: Button
    private val currentUser = FirebaseAuth.getInstance().currentUser

    //보드2추가 버튼
    private var selectedDate: String? = null
    private var selectedTime: String? = null

    private val userId = currentUser?.uid

    // 카테고리를 저장할 리스트
    private val categories = mutableListOf<String>()

    // 카테고리 리스트를 표시할 RecyclerView의 어댑터

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_board_base)
        //버튼들 초기화
        et_write = findViewById(R.id.write)//글쓰기
        btn_cancel1 = findViewById(R.id.close)
        btn_cancel2 = findViewById(R.id.btn_delete)
        btn_category = findViewById(R.id.category)
        et_title = findViewById(R.id.et_title)//제목




        btn_select = findViewById(R.id.select)
        delete = findViewById(R.id.close)
        delete.setOnClickListener {
            finish()
        }

        btn_category.setOnClickListener {
            showDialog()
        }

        btn_select.setOnClickListener {
            showBoardSelectionDialog()
        }







// btnSubmitBoard2와 btnSubmitBoard3에 대해서도 동일하게 적용할 수 있습니다.

    }

    // ... 기존 코드 ...
    // 글쓰기 등록 버튼을 클릭했을 때 호출되는 함수
    private fun submitBoard1() {
        val title = et_title.text.toString().trim()
        val content = et_write.text.toString().trim()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // 카테고리 정보를 포함하여 게시글 객체 생성
        val board1 = Board1(userId, title, content, categories)
        saveBoard1ToFirebase("Board1", board1)
    }

    // 게시글을 Firebase에 저장하는 함수
    private fun saveBoard1ToFirebase(boardType: String, board: Board1) {
        val databaseReference = FirebaseDatabase.getInstance().getReference(boardType)
        val boardId = databaseReference.push().key ?: return

        databaseReference.child(boardId).setValue(board).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "게시글 저장 성공", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "게시글 저장 실패: ${task.exception?.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun showDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)


        bottomSheetDialog.setContentView(R.layout.dialog_category)
        // 다이얼로그의 배경을 흐릿하게 설정
        // 뒷배경 흐리게 설정
        val window = bottomSheetDialog.window
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            val params = window.attributes
            params.dimAmount = 0.5f // 흐릿함 정도 설정 (0.0 to 1.0)
            window.attributes = params
        }

        val etdialogText: EditText = bottomSheetDialog.findViewById(R.id.etDialogInput) ?: return
        val categoryRegister: Button =
            bottomSheetDialog.findViewById(R.id.btn_category_register) ?: return
        val countPeopleTextView: TextView =
            bottomSheetDialog.findViewById(R.id.count_people) ?: return
        val recyclerView: RecyclerView =
            bottomSheetDialog.findViewById(R.id.recycler_view4) ?: return
        val close: Button = bottomSheetDialog.findViewById(R.id.close_02) ?: return
        //취소버튼 누르면 다이얼로그 종료
        close.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        lateinit var adapter: CategoryAdapter

        adapter = CategoryAdapter(categories) { position ->
            if (position < categories.size) {
                categories.removeAt(position)
                adapter.notifyItemRemoved(position)
                countPeopleTextView.text = categories.size.toString()
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 카테고리 등록 버튼에 클릭 리스너 설정
        categoryRegister.setOnClickListener {
            val categoryText = etdialogText.text.toString()
            if (categoryText.isNotEmpty() && categories.size < 5) {
                categories.add(categoryText)
                adapter.notifyItemInserted(categories.size - 1)
                countPeopleTextView.text = categories.size.toString()
                etdialogText.text.clear()
            } else {
                Toast.makeText(this, "카테고리는 최대 5개까지만 추가할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 다이얼로그 내 등록 버튼 클릭 리스너
        val submitButton: Button = bottomSheetDialog.findViewById(R.id.register_category) ?: return
        submitButton.setOnClickListener {
                // 카테고리 정보를 메인 액티비티의 상태에 저장
                categories.addAll(
                    ArrayList(
                        etdialogText.text.toString().split(",").map { it.trim() })
                )
                bottomSheetDialog.dismiss()

        }

        bottomSheetDialog.show()
    }

// ... 기존 코드 ...


    private fun showBoardSelectionDialog() {
        val boards = arrayOf("전공관련대화", "아고라", "원데이클래스") // 실제 게시판 이름으로 교체하세요.
        val builder = AlertDialog.Builder(this)
        builder.setTitle("게시판 선택")
        builder.setItems(boards) { _, which ->
            when (which) {
                0 -> setLayoutForBoard1()
                1 -> setLayoutForBoard2()
                2 -> setLayoutForBoard3()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    //전공관련대화 레이아웃셋팅
    private fun setLayoutForBoard1() {
        // Board 1에 대한 레이아웃 업데이트를 처리합니다.
        // "게시판 선택" 버튼의 ID를 사용하여 버튼의 참조를 가져옵니다.
        val buttonBoardSelect: Button = findViewById(R.id.select)
        // 버튼의 텍스트를 "전공관련대화"로 변경합니다.
        buttonBoardSelect.text = "전공관련대화"

        // 추가적인 레이아웃 변경 로직이 필요하다면 여기에 구현합니다.
        val submit:Button=findViewById(R.id.btn_register)
        submit.setOnClickListener{
            submitBoard1()
        }

    }

    //아고라 레이아웃셋팅
    private fun setLayoutForBoard2() {

        // 현재 ConstraintLayout의 부모 뷰그룹을 가져옵니다.



        val parentLayout: ViewGroup = findViewById(R.id.baselayout)
        parentLayout.removeAllViews()
        val inflater = layoutInflater
        val newLayout = inflater.inflate(R.layout.board_layout_2, parentLayout, false)


        // 새로운 레이아웃을 부모 뷰그룹에 추가합니다.
        parentLayout.addView(newLayout)


        val btn_online = newLayout.findViewById<Button>(R.id.btn_online)
        val btn_offline = newLayout.findViewById<Button>(R.id.btn_offline)
        val btn_board=newLayout.findViewById<Button>(R.id.select)
        val btn_category2=newLayout.findViewById<Button>(R.id.category)
        val btn_close1=newLayout.findViewById<Button>(R.id.close)
        val btn_close2=newLayout.findViewById<Button>(R.id.btn_delete)
        val et_title=newLayout.findViewById<EditText>(R.id.et_title)
        val et_write=newLayout.findViewById<EditText>(R.id.write)


        // 새로운 레이아웃에 있는 버튼을 찾아서 클릭 이벤트를 설정합니다.
        val btn_map: Button = findViewById(R.id.btn_take_where)
        btn_map.setOnClickListener {
            // MapSearchActivity를 시작하기 위해 startForResult 사용
            val intent = Intent(this, MapSearchActivity::class.java)
            startForResult.launch(intent)
        }



        val et_people:EditText=newLayout.findViewById(R.id.et_how_many_people)


        // 클래스 내에서 온라인/오프라인 상태를 추적하는 변수를 선언합니다.
        var isOnlineClass = true // 기본값은 온라인으로 설정

// onCreate 함수 내에 있는 코드의 일부
        btn_board.text="아고라"
        btn_close1.setOnClickListener{
            finish()
        }
        btn_close2.setOnClickListener{
            finish()
        }


        btn_offline.setOnClickListener {
            btn_offline.setBackgroundResource(R.drawable.btn_register)
            btn_online.setBackgroundResource(R.drawable.et_gray)

            // 오프라인 버튼이 클릭되면 isOnlineClass를 false로 설정
            isOnlineClass = false
            btn_online.isSelected = false
            btn_offline.isSelected = true
            btn_map.isEnabled = false // 장소 선택 버튼 비활성화

        }


        btn_online.setOnClickListener {
            btn_online.setBackgroundResource(R.drawable.btn_register)
            btn_offline.setBackgroundResource(R.drawable.et_gray)
            // 온라인 버튼이 클릭되면 isOnlineClass를 true로 설정
            isOnlineClass = true
            btn_online.isSelected = true
            btn_offline.isSelected = false
            btn_map.isEnabled = true // 장소 선택 버튼 활성화

        }
        btn_category2.setOnClickListener{
            showDialog()
        }


        // 게시글 등록 버튼에 리스너를 설정합니다.
        val submit: Button = newLayout.findViewById(R.id.btn_register)
        submit.setOnClickListener {
            val title = et_title.text.toString().trim()
            val content = et_write.text.toString().trim()
            val peopleString = et_people.text.toString().trim()
            val people = peopleString.toIntOrNull() ?: 0 // 유효하지 않은 값이면 0을 반환
            val Location=btn_map.getText().toString()

            // 디버깅을 위해 수집된 데이터 로깅
            Log.d("SubmitDebug", "Title: $title")
            Log.d("SubmitDebug", "Content: $content")
            Log.d("SubmitDebug", "People: $people")
            Log.d("SubmitDebug", "Location: $Location")
            Log.d("SubmitDebug", "Categories: ${categories.joinToString()}")

            // 모든 정보를 입력했는지 확인합니다.
            if (title.isEmpty() || content.isEmpty() || people <= 0 || (!isOnlineClass && selectedLocation.isNullOrBlank())) {
                Toast.makeText(this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Board2 객체를 생성합니다.
            val board2 = Board2(
                userId = userId,
                title = title,
                content = content,
                categories = categories,
                date = System.currentTimeMillis(),
                time = selectedTime,
                eventDate = selectedDate,
                location = Location,
                numberOfPeople = people,
                online = isOnlineClass
            )

            // Firebase에 게시글을 저장하는 함수를 호출합니다.
            saveBoard2ToFirebase("Board2", board2)
        }
        // 날짜 및 시간 선택 버튼 참조를 가져옵니다.
        val btn_date = newLayout.findViewById<Button>(R.id.btn_select_date)
        val btn_time = newLayout.findViewById<Button>(R.id.btn_select_clock)

        // 날짜 선택 버튼 클릭 리스너 설정
        btn_date.setOnClickListener {
            showDatePickerDialog()
        }

        // 시간 선택 버튼 클릭 리스너 설정
        btn_time.setOnClickListener {
            showTimePickerDialog()
        }


    }


    private fun saveBoard2ToFirebase(boardType: String, board: Board2) {
        val databaseReference = FirebaseDatabase.getInstance().getReference(boardType)
        val boardId = databaseReference.push().key ?: return

        // Board2 객체에 올바른 위치 정보가 있는지 확인
        if (board.location.isNullOrEmpty()) {
            Toast.makeText(this, "위치 정보가 없습니다", Toast.LENGTH_SHORT).show()
            return
        }

        databaseReference.child(boardId).setValue(board).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "게시글 저장 성공", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "게시글 저장 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_MAP_SEARCH && resultCode == Activity.RESULT_OK) {
            val selectedLocation = data?.getStringExtra("roadAddress")
            // 받은 주소를 사용합니다. 예를 들어, 버튼 텍스트를 설정합니다.
            findViewById<Button>(R.id.btn_take_where).text = selectedLocation ?: "Location not selected"
            Log.d("CallerActivity", "Received road: $selectedLocation") // 로그 추가
            // 나머지 로직 처리
        }
    }


    companion object {
        private const val REQUEST_CODE_MAP_SEARCH = 1
    }





    private fun showTimePickerDialog() {
        // 현재 시간을 기본값으로 설정합니다.
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // TimePickerDialog 인스턴스를 생성합니다.
        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            // 버튼의 텍스트를 선택된 시간으로 업데이트합니다.
            selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
            findViewById<Button>(R.id.btn_select_clock).text = selectedTime


        }, hour, minute, true)

        // TimePickerDialog를 보여줍니다.
        timePickerDialog.show()
    }

    private fun showDatePickerDialog() {
        // 현재 날짜를 기본값으로 설정합니다.
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // DatePickerDialog 인스턴스를 생성합니다.
        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // 버튼의 텍스트를 선택된 날짜로 업데이트합니다.
            val selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            findViewById<Button>(R.id.btn_select_date).text = selectedDate

        }, year, month, day)

        // DatePickerDialog를 보여줍니다.
        datePickerDialog.show()
    }

    //원데이클래스 레이아웃셋팅
    private fun setLayoutForBoard3() {
        // Board 3에 대한 레이아웃 업데이트를 처리합니다.
        // 현재 ConstraintLayout의 부모 뷰그룹을 가져옵니다.
        val parentLayout: ViewGroup = findViewById(R.id.baselayout)
        // 부모 뷰그룹에서 모든 자식 뷰를 제거합니다.
        parentLayout.removeAllViews()

        // LayoutInflater를 사용하여 새로운 레이아웃을 가져옵니다.
        val inflater = layoutInflater
        val newLayout = inflater.inflate(R.layout.board_layout_3, parentLayout, false)

        // 새로운 레이아웃을 부모 뷰그룹에 추가합니다.
        parentLayout.addView(newLayout)
    }


}
class CategoryAdapter(private val categories: MutableList<String>,
                      private val onItemRemoved: (Int) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val removeButton: Button=view.findViewById(R.id.btn_category_delete)
        val textView: TextView = view.findViewById(R.id.category_word)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_word, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        // 현재 아이템의 데이터를 설정
        holder.textView.text = categories[position]

        // 삭제 버튼에 대한 클릭 리스너 설정
        holder.removeButton.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                // 리스트에서 아이템 삭제
                categories.removeAt(currentPosition)

                // 어댑터에 항목이 제거된 것을 알림
                notifyItemRemoved(currentPosition)


                // 삭제된 아이템에 대한 추가 처리를 위한 콜백 함수 호출
                onItemRemoved(currentPosition)
            }
        }
    }

    override fun getItemCount() = categories.size

}

