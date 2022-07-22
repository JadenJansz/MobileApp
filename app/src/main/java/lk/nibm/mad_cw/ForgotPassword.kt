package lk.nibm.mad_cw

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword : AppCompatActivity() {

    private lateinit var txtEmail : TextInputEditText
    private lateinit var txtEmailLayout: TextInputLayout
    private lateinit var btnReset : Button
    private lateinit var progressBar : ProgressBar

    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getSupportActionBar()?.hide()
        setContentView(R.layout.activity_forgot_password)

        mAuth = FirebaseAuth.getInstance()

        txtEmail = findViewById(R.id.txt_email_forgot_pw)
        txtEmailLayout = findViewById(R.id.txt_email_forgot_pw_layout)
        txtEmail.setOnClickListener(){
            txtEmailLayout.error = ""
            txtEmailLayout.boxStrokeColor = Color.rgb(213,128,255)
        }

        progressBar = findViewById(R.id.progressBar)

        btnReset = findViewById(R.id.btn_forgotPass)
        btnReset.setOnClickListener { resetPassword() }
    }

    private fun resetPassword(){
        val email : String = txtEmail.text.toString().trim()

        if(email.isEmpty()){
            txtEmailLayout.error = "*email is required"
            txtEmailLayout.boxStrokeColor = Color.RED
            txtEmail.requestFocus()
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtEmailLayout.error = "*invalid email"
            txtEmailLayout.boxStrokeColor = Color.RED
            txtEmail.requestFocus()
            return
        }

        progressBar.setVisibility(View.VISIBLE)
        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    Toast.makeText(this, "Check Your Email", Toast.LENGTH_SHORT).show()
                    progressBar.setVisibility(View.GONE)
                }
                else{
                    Toast.makeText(this, "Try Again!!!", Toast.LENGTH_SHORT).show()
                    progressBar.setVisibility(View.GONE)
                }
            }
    }
}