package lk.nibm.mad_cw

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.util.ArrayList

class ViewNoticeMember : Fragment() {

    private lateinit var memberRecycleView : RecyclerView
    private var noticeArrayList : ArrayList<NoticeArray> = ArrayList()
    private lateinit var reference : DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.view_notices, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        memberRecycleView = view.findViewById(R.id.row_notice)
        memberRecycleView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        reference = FirebaseDatabase.getInstance().getReference("Notice")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for (userSnapshot in snapshot.children){
                        val notice = userSnapshot.getValue(NoticeArray::class.java)
                        noticeArrayList.add(notice!!)
                    }
                    val memberAdapter = context?.let { AdapterNotice(noticeArrayList, it) }
                    memberRecycleView.adapter = memberAdapter

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
}