package lk.nibm.mad_cw

//import android.support.v7.app.AppCompatActivity

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.net.URI


class NewRegistration : AppCompatActivity(), View.OnClickListener {

    private lateinit var signUp : Button

    private lateinit var avatar : ImageView
    private lateinit var txtFname : EditText
    private lateinit var txtLname : EditText
    private lateinit var txtEmail : EditText
    private lateinit var txtNIC : EditText
    private lateinit var txtContact : EditText
    private lateinit var txtEmergency : EditText
    private var txtPassword : EditText? = null
    private var txtConfirmPassword : EditText? = null
    private lateinit var progressBar : ProgressBar

    private lateinit var mAuth : FirebaseAuth
    private lateinit var database : DatabaseReference
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_registration)


        mAuth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference.child("Profile Images")

        signUp = findViewById(R.id.btn_signUp)
        signUp.setOnClickListener(this)

        avatar = findViewById(R.id.avatar_new_reg)
        avatar.setOnClickListener(this)

        txtFname = findViewById(R.id.txt_fname)
        txtLname = findViewById(R.id.txt_lname)
        txtEmail = findViewById(R.id.txt_email)
        txtNIC = findViewById(R.id.txt_nic)
        txtContact = findViewById(R.id.txt_contact)
        txtEmergency = findViewById(R.id.txt_emergency_contact)
        txtPassword = findViewById(R.id.txt_password)
        txtConfirmPassword = findViewById(R.id.txt_confpass)


        progressBar = findViewById(R.id.progressBar)

    }

    override fun onClick(v: View?) {
        when(v?.id) {

            R.id.btn_signUp -> {
                registerUser()
            }

            R.id.avatar_new_reg -> {
                saveProfilePic()
            }
        }
    }

    private fun saveProfilePic(){
        var openGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(openGallery, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, Imagedata: Intent?) {
        super.onActivityResult(requestCode, resultCode, Imagedata)

        if (requestCode == 1000){
            if (resultCode === Activity.RESULT_OK){
                var imageUri : Uri? = Imagedata!!.data
                avatar.setImageURI(imageUri)

                uploadImageToFirebase(imageUri!!)
            }
        }
    }

    private fun uploadImageToFirebase(imageUri : Uri){
        var fileRef : StorageReference = storageReference.child(mAuth.currentUser!!.uid + ".jpg")

        fileRef.putFile(imageUri).addOnCompleteListener(this) {task ->
            if (task.isSuccessful){
                Toast.makeText(this, "Photo Uploaded", Toast.LENGTH_SHORT).show()
                var downloadUrl : String = fileRef.downloadUrl.toString()

                var userRef = FirebaseDatabase.getInstance().reference.child("Users").child(mAuth.currentUser!!.uid)
                userRef.child("profileImage").setValue(downloadUrl)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            Toast.makeText(this, "Photo Uploaded To Database", Toast.LENGTH_SHORT).show()

                        }
                        else{
                            Toast.makeText(this, "Error Try Again" + task.exception!!.message, Toast.LENGTH_SHORT).show()

                        }
                    }
            }
            else{
                Toast.makeText(this, "Error" + task.exception!!.message , Toast.LENGTH_SHORT).show()
                Log.d("Error", task.exception!!.message.toString())

            }
        }
    }

    private fun registerUser(){
        var fname : String = txtFname.text.toString().trim()
        var lname : String = txtLname.text.toString().trim()
        var email : String = txtEmail.text.toString().trim()
        var nic : String = txtNIC.text.toString().trim()
        var contact : String = txtContact.text.toString().trim()
        var emergencyContact : String = txtEmergency.text.toString().trim()
        var password : String = txtPassword?.text.toString().trim()
        var conPassword : String = txtConfirmPassword?.text.toString().trim()

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

        if(nic.isEmpty()){
            txtNIC.setError("NIC is required")
            txtNIC.requestFocus()
            return
        }
        if(nic.length < 12){
            txtNIC.setError("Invalid NIC")
            txtNIC.requestFocus()
            return
        }

        if(contact.isEmpty()){
            txtContact.setError("Contact number is required")
            txtContact.requestFocus()
            return
        }
        if(contact.length < 10){
            txtContact.setError("Invalid Contact number")
            txtContact.requestFocus()
            return
        }

        if(emergencyContact.isEmpty()){
            txtEmergency.setError("Contact number is required")
            txtEmergency.requestFocus()
            return
        }
        if(emergencyContact.length < 10){
            txtEmergency.setError("Invalid Contact number")
            txtEmergency.requestFocus()
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

        database = FirebaseDatabase.getInstance().getReference("Users")
        val user = mapOf<String, String>(
            "fname" to fname,
            "lname" to lname,
            "nic" to nic,
            "contact" to contact,
            "emergency" to emergencyContact
        )
        database.child(FirebaseAuth.getInstance().currentUser!!.uid).updateChildren(user)
            .addOnSuccessListener {
                val pass = mAuth.currentUser
                pass!!.updatePassword(password).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        progressBar.setVisibility(View.GONE)
                        Toast.makeText(this, "Successfully Updated", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            .addOnFailureListener{
                progressBar.setVisibility(View.GONE)
                Toast.makeText(this, "Failed to Update! Try Again", Toast.LENGTH_SHORT).show()
            }

//        mAuth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    val user = User(fname,lname,email,nic, contact, emergencyContact, "MEMBER")
//                    FirebaseDatabase.getInstance().getReference("Users")
//                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
//                        .setValue(user).addOnCompleteListener(this) {task ->
//
//                            if(task.isSuccessful){
//                                Toast.makeText(this, "Successfully Registered", Toast.LENGTH_SHORT).show()
//                                progressBar.setVisibility(View.GONE)
//                            }else{
//                                Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show()
//                                progressBar.setVisibility(View.GONE)
//                            }
//                        }
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithEmail:success")
//
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "signInWithEmail:failure", task.exception)
//                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
//                    progressBar.setVisibility(View.GONE)
//                }
//            }
    }
}