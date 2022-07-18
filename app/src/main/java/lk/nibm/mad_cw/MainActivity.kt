package lk.nibm.mad_cw

import android.content.Intent
//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var txtUsername : EditText
    lateinit var txtPassword : EditText
    lateinit var btnLogin : Button
    lateinit var lblForgotPassword: TextView

    lateinit var mAuth : FirebaseAuth
    lateinit var reference: DatabaseReference

    lateinit var progressBar : ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getSupportActionBar()?.hide()
        setContentView(R.layout.activity_main)



        mAuth = FirebaseAuth.getInstance()

        txtUsername = findViewById(R.id.txt_username)
        txtPassword = findViewById(R.id.txt_pass)

        lblForgotPassword = findViewById(R.id.lbl_forgotPassword)
        lblForgotPassword.setOnClickListener(this)

        btnLogin = findViewById(R.id.btn_login)
        btnLogin.setOnClickListener(this)



        progressBar = findViewById(R.id.progressBar)
    }

    override fun onClick(v: View?) {
        when(v?.id) {

            R.id.btn_login -> {
                userLogin()
            }

            R.id.lbl_forgotPassword -> {
                var forPass = Intent(this, ForgotPassword::class.java)
                startActivity(forPass)
            }
        }
    }

    private fun userLogin(){
        var email : String = txtUsername.text.toString().trim()
        var password : String = txtPassword.text.toString().trim()

        if(email.isEmpty()){
            txtUsername.setError("Email is required")
            txtUsername.requestFocus()
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtUsername.setError("Invalid Email")
            txtUsername.requestFocus()
            return
        }
        if(password.length < 6){
            txtPassword!!.setError("Password should be more than 6 characters")
            txtPassword!!.requestFocus()
            return
        }
        if(password.isEmpty()){
            txtPassword!!.setError("Password cannot be empty")
            txtPassword!!.requestFocus()
            return
        }
        progressBar.setVisibility(View.VISIBLE)
        Log.d("Before", "Listener")
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {task ->
                if(task.isSuccessful){
                    val user : FirebaseUser? = FirebaseAuth.getInstance().currentUser
                    if(user!!.isEmailVerified()){
                        reference = FirebaseDatabase.getInstance().getReference("Users")
                        reference.child(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnCompleteListener(this) { task ->
                            if(task.isSuccessful){
                                val dataSnapshot : DataSnapshot = task.getResult()

                                if(dataSnapshot.child("role").getValue() == "ADMIN"){
                                    val adminPage = Intent(this, AdminHome::class.java)
                                    startActivity(adminPage)
                                    progressBar.setVisibility(View.GONE)
                                    //this.finish()
                                }
                                else if(dataSnapshot.child("role").getValue() == "MEMBER" && dataSnapshot.child("active").getValue() == "Y"){
                                    val memberHomePage = Intent(this, MemberHome::class.java)
                                    startActivity(memberHomePage)
                                    progressBar.setVisibility(View.GONE)
                                    //this.finish()
                                }
                                else{
                                    Toast.makeText(this, "Your Account Has Been Disabled! Contact Your Administrator!", Toast.LENGTH_SHORT).show()
                                    progressBar.setVisibility(View.GONE)
                                }
                            }
                        }

                    }
                    else{
                        reference = FirebaseDatabase.getInstance().getReference("Users")
                        reference.child(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnCompleteListener(this) { task ->
                            if(task.isSuccessful){
                                val dataSnapshot : DataSnapshot = task.getResult()

                                if(dataSnapshot.child("nic").getValue() == ""){
                                    val newRegistrationPage = Intent(this, NewRegistration::class.java)
                                    startActivity(newRegistrationPage)
                                    progressBar.setVisibility(View.GONE)
                                    //this.finish()

                                }
                                else if(dataSnapshot.child("nic").getValue() != ""){
                                    Toast.makeText(this, "Please Verify Your Email from your Gmail account", Toast.LENGTH_SHORT).show()
                                    progressBar.setVisibility(View.GONE)
                                }
                            }
                        }


//                        user.sendEmailVerification()
                        Toast.makeText(this, "Check Your Email", Toast.LENGTH_SHORT).show()
                        progressBar.setVisibility(View.GONE)
                    }

                }
                else{
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                    progressBar.setVisibility(View.GONE)
                }
            }
    }
}