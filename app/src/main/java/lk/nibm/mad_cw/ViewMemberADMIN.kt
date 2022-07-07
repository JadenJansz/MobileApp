package lk.nibm.mad_cw

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class ViewMemberADMIN : AppCompatActivity() {

    private lateinit var btnDelete : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_member_admin)

        val members = intent.getParcelableExtra<UserArray>("member")

        btnDelete = findViewById(R.id.btn_viewMemberADMIN)
        btnDelete.setOnClickListener(){

        }



        if(members != null){
            val name : TextView = findViewById(R.id.txt_)
            val email : TextView = findViewById(R.id.txt_email_viewMemberADMIN)
            val dob : TextView = findViewById(R.id.txt_dob_viewMemberADMIN)
            val contact : TextView = findViewById(R.id.txt_contact_viewMemberADMIN)
            val emergency : TextView = findViewById(R.id.txt_emergency_viewMemberADMIN)
            val nic : TextView = findViewById(R.id.txt_nic_viewMemberADMIN)
            val avatar : ImageView = findViewById(R.id.avatar_viewMemberADMIN)

            name.text = members.fname + " " + members.lname
            email.text = members.email
            dob.text = members.dob
            contact.text = members.contact
            emergency.text = members.emergency
            nic.text = members.nic

            Glide.with(this).load(members.profileImage).into(avatar)
        }
    }
}