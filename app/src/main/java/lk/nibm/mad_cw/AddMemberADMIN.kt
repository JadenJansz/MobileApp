package lk.nibm.mad_cw

import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class AddMemberADMIN :  Fragment() {

    private lateinit var memEmail : TextInputEditText
    private lateinit var memEmailLayout : TextInputLayout
    private lateinit var btnAddMem : Button
    private lateinit var spinner : Spinner
    private lateinit var progressBar : ProgressBar
    private lateinit var mAuth : FirebaseAuth
    var random = (1000000..10000000).random()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.add_member_admin, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        memEmail = view.findViewById(R.id.txt_memEmail)
        memEmail.setOnClickListener(){
            memEmailLayout.error = ""
            memEmailLayout.boxStrokeColor = Color.rgb(213,128,255)
        }
        memEmailLayout = view.findViewById(R.id.txt_memEmailLayout)

        btnAddMem = view.findViewById(R.id.btn_addMember)
        progressBar = view.findViewById(R.id.progressBar)
        spinner = view.findViewById(R.id.spinner)

        val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.spinner, android.R.layout.simple_spinner_item)

        adapter.setDropDownViewResource(R.layout.spin_item)
        spinner.adapter = adapter

        mAuth = FirebaseAuth.getInstance()

        btnAddMem.setOnClickListener {
            addMember()
        }
    }

    private fun sendMail() {
        var email = memEmail.text.toString().trim()
        var subject = "Visit our app and activate your account "
        var message = "Hi,\n" +
                "\n" +
                "Welcome to the D's Gym family!\n" +
                "\n" +
                "By joining us, you’ve already taken the first step on your fitness journey and we couldn’t be happier\n" +
                " to walk with you on the path to achieve your health and fitness goals!\n" +
                "\n" +
                "Get started by visiting our app to check our hours of operation, view class schedules or to learn more about our facility’s offering. \n" +
                "\n" +
                "Use the following credentials to login to your account.\n" +
                "\n" +
                "Username : $email\n" +
                "Password : $random\n" +
                "\n" +
                "D's Gym"

        val javaMailAPI = JavaMailAPI(this, email, subject, message)
        javaMailAPI.execute()
    }

    private fun addMember(){
        var email = memEmail.text.toString().trim()
        var type : String = "MEMBER"
        type = if(spinner.selectedItem.toString() == "MEMBER") {
            "MEMBER"
        } else{
            "ADMIN"
        }
        if(email.isEmpty()){
            memEmailLayout.error = "*email is required"
            memEmailLayout.boxStrokeColor = Color.RED
            memEmail.requestFocus()
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            memEmailLayout.error = "*invalid email"
            memEmailLayout.boxStrokeColor = Color.RED
            memEmail.requestFocus()
            return
        }



        progressBar.setVisibility(View.VISIBLE)
        mAuth.createUserWithEmailAndPassword(email, "1234567")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = User(FirebaseAuth.getInstance().currentUser!!.uid,"","","","",email,"","","", type, "")
                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .setValue(user).addOnCompleteListener {task ->

                            if(task.isSuccessful){
                                val toast = ToastClass()
                                toast.showToast(requireContext(), "Successfully Registered", 0)
                                progressBar.setVisibility(View.GONE)
                                sendMail()
                            }else{
                                val toast = ToastClass()
                                toast.showToast(requireContext(), "Try Again", 1)
                                progressBar.setVisibility(View.GONE)
                            }
                        }
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithEmail:success")

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                    val toast = ToastClass()
                    toast.showToast(requireContext(), "This User Already Exists", 1)
                    progressBar.setVisibility(View.GONE)
                }
            }
    }
}