package lk.nibm.mad_cw

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddMemberADMIN :  Fragment() {

    private lateinit var memEmail : EditText
    private lateinit var btnAddMem : Button
    private lateinit var progressBar : ProgressBar
    private lateinit var mAuth : FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.add_member_admin, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        memEmail = view.findViewById(R.id.txt_memEmail)
        btnAddMem = view.findViewById(R.id.btn_addMember)
        progressBar = view.findViewById(R.id.progressBar)

        mAuth = FirebaseAuth.getInstance()

        btnAddMem.setOnClickListener {
            addMember()
        }
    }

    private fun sendMail() {
        var email = memEmail.text.toString().trim()
        var subject = "Visit our app and activate your account "
        var message = "Welcome and Thank you for joining Fitness Arcade!"

        val javaMailAPI = JavaMailAPI(this, email, subject, message)
        javaMailAPI.execute()
    }

    private fun addMember(){
        var email = memEmail.text.toString().trim()

        if(email.isEmpty()){
            memEmail.setError("Email is required")
            memEmail.requestFocus()
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            memEmail.setError("Invalid Email")
            memEmail.requestFocus()
            return
        }

        var random = (0..1000000).random()

        progressBar.setVisibility(View.VISIBLE)
        mAuth.createUserWithEmailAndPassword(email, "1234567")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = User(FirebaseAuth.getInstance().currentUser!!.uid,"","","","",email,"","","", "MEMBER")
                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .setValue(user).addOnCompleteListener {task ->

                            if(task.isSuccessful){
                                Toast.makeText(context, "Successfully Registered", Toast.LENGTH_SHORT).show()
                                progressBar.setVisibility(View.GONE)
                                sendMail()
                            }else{
                                Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show()
                                progressBar.setVisibility(View.GONE)
                            }
                        }
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithEmail:success")

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    progressBar.setVisibility(View.GONE)
                }
            }
    }
}