package lk.nibm.mad_cw

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class ViewMemberADMIN : AppCompatActivity() {

    private lateinit var btnDelete : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_member_admin)

        btnDelete = findViewById(R.id.btn_viewMemberADMIN)
        btnDelete.setOnClickListener(){
            
        }

        val members = intent.getParcelableExtra<UserArray>("member")

        if(members != null){
            val name : TextView = findViewById(R.id.txt_name_viewMemberADMIN)
            val email : TextView = findViewById(R.id.txt_email_viewMemberADMIN)
            val contact : TextView = findViewById(R.id.txt_contact_viewMemberADMIN)
            val emergency : TextView = findViewById(R.id.txt_emergency_viewMemberADMIN)
            val nic : TextView = findViewById(R.id.txt_nic_viewMemberADMIN)
            val avatar : ImageView = findViewById(R.id.avatar_viewMemberADMIN)

            name.text = members.fname + " " + members.lname
            email.text = members.email
            contact.text = members.contact
            emergency.text = members.emergency
            nic.text = members.nic

            Glide.with(this).load(members.profileImage).into(avatar)
        }
    }
}