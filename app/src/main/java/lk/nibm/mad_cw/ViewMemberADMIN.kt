package lk.nibm.mad_cw

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ViewMemberADMIN : AppCompatActivity() {

    private lateinit var btnDelete : Button
    private lateinit var uid : String
    private lateinit var progressBar : ProgressBar

    lateinit var mAuth : FirebaseAuth
    lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_member_admin)

        progressBar = findViewById(R.id.progressBar)
        val members = intent.getParcelableExtra<UserArray>("member")

        if(members != null){
            val name : TextView = findViewById(R.id.txt_name_viewMemberADMIN)
            val email : TextView = findViewById(R.id.txt_email_viewMemberADMIN)
            val dob : TextView = findViewById(R.id.txt_dob_viewMemberADMIN)
            val contact : TextView = findViewById(R.id.txt_contact_viewMemberADMIN)
            val emergency : TextView = findViewById(R.id.txt_emergency_viewMemberADMIN)
            val nic : TextView = findViewById(R.id.txt_nic_viewMemberADMIN)
            val avatar : ImageView = findViewById(R.id.avatar_viewMemberADMIN)

            uid = members.uid.toString()
            name.text = members.fname + " " + members.lname
            email.text = members.email
            dob.text = members.dob
            contact.text = members.contact
            emergency.text = members.emergency
            nic.text = members.nic

            Glide.with(this).load(members.profileImage).into(avatar)
        }

        btnDelete = findViewById(R.id.btn_viewMemberADMIN)
        btnDelete.setOnClickListener(){
            val builder = this.let { AlertDialog.Builder(it) }
            builder.setTitle("Disable Member Account")
            builder.setMessage("Are you sure to disable this member account ?")
            builder.setPositiveButton("Disable", DialogInterface.OnClickListener {
                    dialog, id ->
                dialog.cancel()
                deleteMember()
            })
            builder.setNegativeButton("Cancel", DialogInterface.OnClickListener{
                    dialog, id ->
                dialog.cancel()
            })
            val alert = builder.create()
            alert!!.show()

        }
    }

    private fun deleteMember() {
        progressBar.setVisibility(View.VISIBLE)
        reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.child(uid).get()
            .addOnSuccessListener {
                if(it.exists()){
                    val user = mapOf<String, String>(
                        "active" to "N"
                    )

                    reference.child(uid).updateChildren(user)
                        .addOnSuccessListener {
                            progressBar.setVisibility(View.GONE)
                            Toast.makeText(this, "Successfully Disabled Account", Toast.LENGTH_SHORT).show()

                        }
                }
                else{
                    progressBar.setVisibility(View.GONE)
                    Toast.makeText(this, "Error! Try Again", Toast.LENGTH_SHORT).show()
                }
            }
    }
}