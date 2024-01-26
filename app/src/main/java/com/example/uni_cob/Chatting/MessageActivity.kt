package com.example.uni_cob

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.uni_cob.Chatting.ChatModel
import com.example.uni_cob.utility.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
class MessageActivity : AppCompatActivity() {

    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private var chatRoomUid: String? = null
    private var destinationUid: String? = null
    private var uid: String? = null
    private var recyclerView: RecyclerView? = null
    private lateinit var btn_image:Button
    private lateinit var btn_back:Button
    private val imageChooserLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedImageUri ->
            uploadImageToFirebaseStorage(selectedImageUri)
        }
    }
    companion object{
        const val IMAGE_REQUEST_CODE=102
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        btn_image=findViewById<Button>(R.id.btn_send_image)
        btn_back=findViewById(R.id.btn_back)
        val imageView = findViewById<ImageView>(R.id.messageActivity_ImageView)
        val editText = findViewById<TextView>(R.id.messageActivity_editText)
        btn_back.setOnClickListener{
            finish()
        }
        //메세지를 보낸 시간
        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM월dd일 hh:mm")
        val curTime = dateFormat.format(Date(time)).toString()

        // 인텐트에서 사용자의 정보를 가져옵니다.
        val destinationName = intent.getStringExtra("destinationName")
        val destinationProfileImageUrl = intent.getStringExtra("destinationProfileImageUrl")
        destinationUid = intent.getStringExtra("destinationUid")

        val textViewTopName = findViewById<TextView>(R.id.messageActivity_textView_topName)
        textViewTopName.text = destinationName
        uid = Firebase.auth.currentUser?.uid.toString()
        recyclerView = findViewById(R.id.messageActivity_recyclerview)

        imageView.setOnClickListener {
            Log.d("클릭 시 dest", "$destinationUid")
            val chatModel = ChatModel()
            chatModel.users.put(uid.toString(), true)
            chatModel.users.put(destinationUid!!, true)
            val currentTimestamp = System.currentTimeMillis()
            val comment = ChatModel.Comment(uid, editText.text.toString(), "",currentTimestamp,"")
            if (chatRoomUid == null) {
                imageView.isEnabled = false
                fireDatabase.child("chatrooms").push().setValue(chatModel).addOnSuccessListener {
                    //채팅방 생성
                    checkChatRoom()
                    //메세지 보내기
                    Handler(Looper.getMainLooper()).postDelayed({
                        println(chatRoomUid)
                        fireDatabase.child("chatrooms").child(chatRoomUid.toString())
                            .child("comments").push().setValue(comment)
                        editText.text = null
                    }, 1000L)
                    Log.d("chatUidNull dest", "$destinationUid")
                }
            } else {
                fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("comments")
                    .push().setValue(comment)
                editText.text = null
                Log.d("chatUidNotNull dest", "$destinationUid")
            }
        }
        checkChatRoom()

        btn_image.setOnClickListener{
            imageChooserLauncher.launch("image/*")
        }
    }
    //이미지 선택 Intent를 열기위한 함수




    private fun checkChatRoom() {
        val imageView = findViewById<ImageView>(R.id.messageActivity_ImageView)
        fireDatabase.child("chatrooms").orderByChild("users/$uid").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (item in snapshot.children) {
                        println(item)
                        val chatModel = item.getValue<ChatModel>()
                        if (chatModel?.users!!.containsKey(destinationUid)) {
                            chatRoomUid = item.key
                            imageView.isEnabled = true
                            recyclerView?.layoutManager = LinearLayoutManager(this@MessageActivity)
                            recyclerView?.adapter = RecyclerViewAdapter()
                        }
                    }
                }
            })
    }// 이미지 선택 결과를 처리하는 함수
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImageUri: Uri = data.data!!
            uploadImageToFirebaseStorage(selectedImageUri)
        }
    }

    // Firebase Storage에 이미지 업로드하는 함수
    private fun uploadImageToFirebaseStorage(selectedImageUri: Uri) {
        val filename = UUID.randomUUID().toString()
        val ref = Firebase.storage.reference.child("chat_images/$filename")

        ref.putFile(selectedImageUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    val imageUrl = it.toString()
                    sendMessageWithImage(imageUrl)
                }
            }
            .addOnFailureListener {
                // 실패 처리

            }
    }

    // 이미지를 포함한 메시지를 채팅방에 전송하는 함수
    private fun sendMessageWithImage(imageUrl: String) {
        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM월dd일 hh:mm", Locale.getDefault())
        val currentTimestamp = System.currentTimeMillis()
        val comment = ChatModel.Comment(uid, null,imageUrl, currentTimestamp, messageType = "image")
        fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("comments")
            .push().setValue(comment)
    }


    inner class RecyclerViewAdapter :
        RecyclerView.Adapter<RecyclerViewAdapter.MessageViewHolder>() {

        private val comments = ArrayList<ChatModel.Comment>()
        private var friend: User? = null
        private val textView_TopName = findViewById<TextView>(R.id.messageActivity_textView_topName)

        init {
            fireDatabase.child("users").child(destinationUid.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        friend = snapshot.getValue<User>()
                        textView_TopName.text = friend?.name
                        getMessageList()
                    }
                })
        }

        fun getMessageList() {
            fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("comments")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        comments.clear()
                        for (data in snapshot.children) {
                            val comment = data.getValue<ChatModel.Comment>()
                            comment?.let {
                                if (it.uid != uid && !it.isRead) {
                                    // 메시지가 본인이 보낸 것이 아니고, 아직 읽지 않은 경우
                                    fireDatabase.child("chatrooms").child(chatRoomUid.toString())
                                        .child("comments").child(data.key!!)
                                        .child("isRead").setValue(true)
                                }
                                comments.add(it)
                            }
                        }
                        notifyDataSetChanged()
                        recyclerView?.scrollToPosition(comments.size - 1)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // 에러 처리
                    }
                })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)

            return MessageViewHolder(view)
        }

        @SuppressLint("RtlHardcoded")
        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            val comment = comments[position]
            val context = holder.itemView.context
            holder.textView_time.text = comments[position].timestamp.toString()


            // 프로필 이미지와 이름 설정
            val destinationName = intent.getStringExtra("destinationName")
            val destinationProfileImageUrl = intent.getStringExtra("destinationProfileImageUrl")
            val textViewTopName = findViewById<TextView>(R.id.messageActivity_textView_topName)
            textViewTopName.text=intent.getStringExtra("destinationName")

            if (comment.uid == uid) { // 본인 채팅
                holder.layout_main.gravity = Gravity.RIGHT
                holder.textView_time.visibility=View.VISIBLE
                holder.textView_name.visibility = View.INVISIBLE

                if (comment.messageType == "image") {
                    holder.textView_message.visibility = View.GONE
                    holder.textView_time.visibility=View.VISIBLE
                    holder.imageView_message.visibility = View.VISIBLE
                    Glide.with(context)
                        .load(comment.imageUrl)
                        .into(holder.imageView_message)
                    holder.imageView_message.setBackgroundResource(R.drawable.rightbubble) // 이미지일 때는 배경 설정
                } else {
                    holder.textView_message.visibility = View.VISIBLE
                    holder.textView_time.visibility=View.VISIBLE
                    holder.imageView_message.visibility = View.GONE
                    holder.textView_message.text = comment.message
                    holder.textView_message.setBackgroundResource(R.drawable.rightbubble)
                }
            } else { // 상대방 채팅
                holder.layout_main.gravity = Gravity.LEFT
                holder.layout_destination.visibility = View.VISIBLE
                holder.textView_time.visibility=View.VISIBLE
                holder.textView_name.visibility = View.VISIBLE
                holder.textView_name.text = destinationName // 상대방 이름 설정

                Glide.with(context)
                    .load(destinationProfileImageUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.imageView_profile)

                if (comment.messageType == "image") {
                    holder.textView_message.visibility = View.GONE
                    holder.textView_time.visibility=View.VISIBLE
                    holder.imageView_message.visibility = View.VISIBLE
                    Glide.with(context)
                        .load(comment.imageUrl)
                        .into(holder.imageView_message)
                    holder.imageView_message.setBackgroundResource(R.drawable.leftbubble) // 이미지일 때는 배경 설정
                } else {
                    holder.textView_message.visibility = View.VISIBLE
                    holder.imageView_message.visibility = View.GONE
                    holder.textView_message.text = comment.message
                    holder.textView_time.visibility=View.VISIBLE
                    holder.textView_message.setBackgroundResource(R.drawable.leftbubble)
                }
            }

            holder.textView_time.text = comment.timestamp.toString() // 메시지 시간 설정
        }



        inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imageView_message: ImageView=view.findViewById(R.id.imageView_message)
            val textView_message: TextView = view.findViewById(R.id.messageItem_textView_message)
            val textView_name: TextView = view.findViewById(R.id.messageItem_textview_name)
            val imageView_profile: ImageView = view.findViewById(R.id.messageItem_imageview_profile)
            val layout_destination: LinearLayout =
                view.findViewById(R.id.messageItem_layout_destination)
            val layout_main: LinearLayout = view.findViewById(R.id.messageItem_linearlayout_main)
            val textView_time: TextView = view.findViewById(R.id.messageItem_textView_time)
        }

        override fun getItemCount(): Int {
            return comments.size
        }

    }

}
