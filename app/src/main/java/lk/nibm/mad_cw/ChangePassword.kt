package lk.nibm.mad_cw

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class ChangePassword : Fragment(), View.OnClickListener {

    private lateinit var txtCurrentPasswordLayout : TextInputLayout
    private lateinit var txtCurrentPassword: TextInputEditText

    private lateinit var txtNewPasswordLayout : TextInputLayout
    private lateinit var txtNewPassword: TextInputEditText

    private lateinit var txtConfirmPasswordLayout : TextInputLayout
    private lateinit var txtConfirmPassword: TextInputEditText

    private lateinit var btnVerify : Button
    private lateinit var btnChange : Button

    lateinit var mAuth : FirebaseAuth
    private lateinit var progressBar : ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.change_password, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        progressBar = view.findViewById(R.id.progressBar)

        txtCurrentPasswordLayout = view.findViewById(R.id.txt_currentPasswordLayout)
        txtCurrentPassword = view.findViewById(R.id.txt_currentPassword)
        txtCurrentPassword.setOnClickListener {
            txtCurrentPasswordLayout.error = ""
            txtCurrentPasswordLayout.boxStrokeColor = Color.rgb(213,128,255)
        }

        txtNewPasswordLayout = view.findViewById(R.id.txt_chgpasswordLayout)
        txtNewPassword = view.findViewById(R.id.txt_chgpassword)
        txtNewPassword.setOnClickListener {
            txtNewPasswordLayout.error = ""
            txtNewPasswordLayout.boxStrokeColor = Color.rgb(213,128,255)
        }

        txtConfirmPasswordLayout = view.findViewById(R.id.txt_chgconfpassLayout)
        txtConfirmPassword = view.findViewById(R.id.txt_chgconfpass)
        txtConfirmPassword.setOnClickListener {
            txtConfirmPasswordLayout.error = ""
            txtConfirmPasswordLayout.boxStrokeColor = Color.rgb(213,128,255)
        }


        btnVerify = view.findViewById(R.id.btn_verifyPassword)
        btnVerify.setOnClickListener(this)


        btnChange = view.findViewById(R.id.btn_changePassword)
        btnChange.setOnClickListener(this)
        btnChange.isEnabled = false
        btnChange.isClickable = false
        btnChange.setBackgroundColor(Color.rgb(169,169,169))
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            R.id.btn_verifyPassword -> {
                verifyPassword()
            }

            R.id.btn_changePassword -> {
                changePassword()
            }
        }
    }

    private fun verifyPassword(){

        val password : String = txtCurrentPassword.text.toString().trim()

        if(password.isEmpty()){
            txtCurrentPasswordLayout.error = "*password cannot be empty"
            txtCurrentPasswordLayout.boxStrokeColor = Color.RED
            txtCurrentPassword.requestFocus()
            return
        }

        progressBar.visibility = View.VISIBLE

        mAuth.signInWithEmailAndPassword(mAuth.currentUser!!.email.toString(), password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val toast = ToastClass()
                    toast.showToast(requireContext(), "Password Verified!", 0)
                    progressBar.visibility = View.GONE

                    btnChange.isEnabled = true
                    btnChange.isClickable = true
                    btnChange.setBackgroundResource(R.drawable.button_bg)
                }
                else{
                    val toast = ToastClass()
                    toast.showToast(requireContext(), "Incorrect Password!", 1)
                    progressBar.visibility = View.GONE
                }
            }
    }

    private fun changePassword(){
        val password : String = txtNewPassword.text.toString().trim()
        val conPassword : String = txtConfirmPassword.text.toString().trim()

        if(password.length < 6){
            txtNewPasswordLayout.error = "Password should be more than 6 characters"
            txtNewPasswordLayout.boxStrokeColor = Color.RED
            txtNewPassword.requestFocus()
            return
        }
        if(password.isEmpty()){
            txtNewPasswordLayout.error = "*password cannot be empty"
            txtNewPasswordLayout.boxStrokeColor = Color.RED
            txtNewPassword.requestFocus()
            return
        }
        if(conPassword.isEmpty()){
            txtConfirmPasswordLayout.error = "*required"
            txtConfirmPasswordLayout.boxStrokeColor = Color.RED
            txtConfirmPassword.requestFocus()
            return
        }
        else if (conPassword != password){
            txtConfirmPasswordLayout.error = "*passwords do not match"
            txtConfirmPasswordLayout.boxStrokeColor = Color.RED
            txtConfirmPassword.requestFocus()
            return
        }

        progressBar.visibility = View.VISIBLE

        val pass = mAuth.currentUser
        pass!!.updatePassword(password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                progressBar.visibility = View.GONE
                val toast = ToastClass()
                toast.showToast(requireContext(), "Successfully Updated!", 0)

                txtCurrentPassword.setText("")
                txtNewPassword.setText("")
                txtConfirmPassword.setText("")


            }else{
                val toast = ToastClass()
                toast.showToast(requireContext(), "Error! Please try again", 1)
            }
        }

    }
}