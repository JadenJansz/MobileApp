package lk.nibm.mad_cw

//import android.support.v7.app.AppCompatActivity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class NewRegistration : AppCompatActivity(), View.OnClickListener {

    private lateinit var banner : TextView
    private lateinit var signUp : Button

    private lateinit var txtFname : EditText
    private lateinit var txtLname : EditText
    private lateinit var txtEmail : EditText
    private var txtPassword : EditText? = null
    private var txtConfirmPassword : EditText? = null
    private lateinit var progressBar : ProgressBar

    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_registration)


        mAuth = FirebaseAuth.getInstance()

        banner = findViewById(R.id.banner)
        banner.setOnClickListener(this)

        signUp = findViewById(R.id.btn_signUp)
        signUp.setOnClickListener(this)

        txtFname = findViewById(R.id.txt_fname)
        txtLname = findViewById(R.id.txt_lname)
        txtEmail = findViewById(R.id.txt_email)
        txtPassword = findViewById(R.id.txt_password)
        txtConfirmPassword = findViewById(R.id.txt_confpass)


        progressBar = findViewById(R.id.progressBar)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.banner -> {
                var main = Intent(this, MainActivity::class.java)
                startActivity(main)
            }

            R.id.btn_signUp -> {
                registerUser()
            }
        }
    }

    private fun registerUser(){
        var fname : String = txtFname.text.toString()
        var lname : String = txtLname.text.toString()
        var email : String = txtEmail.text.toString()
        var password : String = txtPassword?.text.toString()
        var conPassword : String = txtConfirmPassword?.text.toString()

        if (fname.isEmpty()){
            txtFname.setError("Full Name is required")
            txtFname.requestFocus()
            return
        }
        if(lname.isEmpty()){
            txtLname.setError("Full Name is required")
            txtLname.requestFocus()
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtEmail.setError("Full Name is required")
            txtEmail.requestFocus()
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
        if(conPassword.isEmpty()){
            txtConfirmPassword!!.setError("Cannot be empty")
            txtConfirmPassword!!.requestFocus()
            return
        }
        else if (conPassword != password){
            txtConfirmPassword!!.setError("Passwords do not match")
            txtConfirmPassword!!.requestFocus()
            return
        }

        progressBar.setVisibility(View.VISIBLE)
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = User(fname,lname,email, "Admin")
                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .setValue(user).addOnCompleteListener(this) {task ->

                            if(task.isSuccessful){
                                Toast.makeText(this, "Successfully Registered", Toast.LENGTH_SHORT).show()
                                progressBar.setVisibility(View.GONE)
                            }else{
                                Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show()
                                progressBar.setVisibility(View.GONE)
                            }
                        }
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    progressBar.setVisibility(View.GONE)
                }
            }
    }
}