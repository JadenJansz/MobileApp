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

class ViewMembersADMIN : Fragment() {

    private lateinit var memberRecycleView : RecyclerView
    private lateinit var userArrayList : ArrayList<UserArray>
    private lateinit var reference : DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.view_members_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        memberRecycleView = view.findViewById(R.id.row_exercise)
        memberRecycleView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        userArrayList = arrayListOf<UserArray>()



        reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for (userSnapshot in snapshot.children){
                        if(userSnapshot.child("role").getValue() == "MEMBER"){
                            val user = userSnapshot.getValue(UserArray::class.java)
                            userArrayList.add(user!!)
                            Log.d("Response", userArrayList.toString())
                        }
                        Log.d("Response", userSnapshot.toString())
                    }
                    val memberAdapter = context?.let { AdapterMemberRow(userArrayList, it) }
                    memberRecycleView.adapter = memberAdapter

                    memberAdapter!!.onItemClick = {
                        val intent = Intent(context, ViewMemberADMIN::class.java)
                        intent.putExtra("member", it)
                        startActivity(intent)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }


}