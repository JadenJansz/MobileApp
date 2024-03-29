package lk.nibm.mad_cw

//import android.support.v7.app.AppCompatActivity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var txtUsername : TextInputEditText
    lateinit var txtUsernameLayout : TextInputLayout
    lateinit var txtPassword : TextInputEditText
    lateinit var txtPasswordLayout : TextInputLayout

    lateinit var btnLogin : Button
    lateinit var lblForgotPassword: TextView

    lateinit var mAuth : FirebaseAuth
    lateinit var reference: DatabaseReference

    lateinit var progressBar : ProgressBar
    val toast = ToastClass()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getSupportActionBar()?.hide()
        setContentView(R.layout.activity_main)



        mAuth = FirebaseAuth.getInstance()

        txtUsername = findViewById(R.id.txt_username)
        txtUsernameLayout = findViewById(R.id.txt_usernameLayout)
        txtUsername.setOnClickListener(){
            txtUsernameLayout.error = ""
            txtUsernameLayout.boxStrokeColor = Color.rgb(213,128,255)
        }

        txtPassword = findViewById(R.id.txt_pass)
        txtPasswordLayout = findViewById(R.id.txt_passLayout)
        txtPassword.setOnClickListener(){
            txtPasswordLayout.error = ""
            txtPasswordLayout.boxStrokeColor = Color.rgb(213,128,255)
        }

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
                val forPass = Intent(this, ForgotPassword::class.java)
                startActivity(forPass)
            }
        }
    }

    private fun userLogin(){
        val email : String = txtUsername.text.toString().trim()
        val password : String = txtPassword.text.toString().trim()

        if(email.isEmpty()){
            txtUsernameLayout.error = "*email is required"
            txtUsernameLayout.boxStrokeColor = Color.RED
            txtUsername.requestFocus()
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtUsernameLayout.error = "*invalid email address"
            txtUsernameLayout.boxStrokeColor = Color.RED
            txtUsername.requestFocus()
            return
        }
        if(password.isEmpty()){
            txtPasswordLayout.error = "*password cannot be empty"
            txtPasswordLayout.boxStrokeColor = Color.RED
            txtPassword.requestFocus()
            return
        }
        if(password.length < 6){
            txtPasswordLayout.error = "*password should be more than 6 characters"
            txtPasswordLayout.boxStrokeColor = Color.RED
            txtPassword.requestFocus()
            return
        }

        if (!isNetworkAvailable()) {
            toast.showToast(this, "A Network Connection Is Required", 1)
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
                                    this.finish()
                                }
                                else if(dataSnapshot.child("role").getValue() == "MEMBER" && dataSnapshot.child("active").getValue() == "Y"){
                                    val memberHomePage = Intent(this, MemberHome::class.java)
                                    startActivity(memberHomePage)
                                    progressBar.setVisibility(View.GONE)
                                    this.finish()
                                }
                                else{
                                    toast.showToast(this, "Your Account has been Disabled, Contact Your Administrator!", 2)
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
                                    this.finish()

                                }
                                else if(dataSnapshot.child("nic").value != ""){
                                    toast.showToast(this, "Please Verify Your Email from your Gmail Account", 2)
                                    progressBar.setVisibility(View.GONE)
                                }
                            }
                        }


                        user.sendEmailVerification()
                        toast.showToast(this, "Check Your Email", 2)
                        progressBar.setVisibility(View.GONE)
                    }

                }
                else{
                    toast.showToast(this, "Invalid Credentials", 1)
                    progressBar.setVisibility(View.GONE)
                }
            }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}