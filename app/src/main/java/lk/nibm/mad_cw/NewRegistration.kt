package lk.nibm.mad_cw

//import android.support.v7.app.AppCompatActivity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
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
import com.yalantis.ucrop.UCrop
import java.io.File
import java.net.URI
import java.util.*
import kotlin.properties.Delegates


class NewRegistration : AppCompatActivity(), View.OnClickListener {

    private lateinit var signUp : Button

    private var age : Int = 0

    private lateinit var avatar : ImageView
    private lateinit var txtFname : EditText
    private lateinit var txtLname : EditText
    private lateinit var txtNIC : EditText
    private lateinit var txtDob : TextView
    private lateinit var txtContact : EditText
    private lateinit var txtEmergency : EditText
    private var txtPassword : EditText? = null
    private var txtConfirmPassword : EditText? = null
    private lateinit var progressBar : ProgressBar

    private lateinit var mAuth : FirebaseAuth
    private lateinit var database : DatabaseReference
    private lateinit var storageReference: StorageReference
    private val calenderInstance: Calendar = Calendar.getInstance()
    private var yearText : Int = 0
    private val dob = calenderInstance

    private val CODE_IMAGE_GALLERY = 1
    private val SAMPLE_CROPPED_IMG_NAME = "SampleCropping"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_registration)


        mAuth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference.child("Profile Images")

        signUp = findViewById(R.id.btn_signUp)
        signUp.setOnClickListener(this)

        avatar = findViewById(R.id.avatar_new_reg)
        avatar.setOnClickListener(this)

        txtDob = findViewById(R.id.txt_dob)
        txtDob.setOnClickListener(this)

        txtFname = findViewById(R.id.txt_fname)
        txtLname = findViewById(R.id.txt_lname)
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

            R.id.txt_dob -> {
                showDatePickerDialog()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePickerDialog(){
        val year = calenderInstance.get(Calendar.YEAR)
        val month = calenderInstance.get(Calendar.MONTH)
        val day = calenderInstance.get(Calendar.DAY_OF_MONTH)

        val datePicker =
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener{ view, year, month, day ->
                txtDob.setText("$day-$month-$year")
                dob.set(year, month, day)
                yearText = year
            }, year, month, day)
        Log.e("Age",yearText.toString())
        datePicker.show()
    }

    private fun saveProfilePic(){
        startActivityForResult(
            Intent().setAction(Intent.ACTION_GET_CONTENT)
                .setType("image/*"), CODE_IMAGE_GALLERY
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE_IMAGE_GALLERY && resultCode == Activity.RESULT_OK) {
            val imageUri = data!!.data
            imageUri?.let { startCrop(it) }
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            val imageUriResultCrop = UCrop.getOutput(data!!)
            if (imageUriResultCrop != null) {
                avatar.setImageURI(imageUriResultCrop)
                uploadImageToFirebase(imageUriResultCrop)
            }
        }
    }

    private fun startCrop(uri: Uri) {
        var destinationFileName: String = SAMPLE_CROPPED_IMG_NAME
        destinationFileName += ".jpg"
        val uCrop = UCrop.of(uri, Uri.fromFile(File(this.getCacheDir(), destinationFileName)))
        uCrop.withAspectRatio(1f, 1f)
        uCrop.withMaxResultSize(450, 450)
        uCrop.withOptions(getCropOptions())
        uCrop.start(this)
    }

    private fun getCropOptions(): UCrop.Options {
        val options = UCrop.Options()
        options.setCompressionQuality(70)
        options.setHideBottomControls(false)
        options.setFreeStyleCropEnabled(true)

        return options
    }

    private fun uploadImageToFirebase(imageUri : Uri){
        var fileRef : StorageReference = storageReference.child(mAuth.currentUser!!.uid + ".jpg")

        fileRef.putFile(imageUri).addOnCompleteListener(this) {task ->
            if (task.isSuccessful){
                Toast.makeText(this, "Photo Uploaded", Toast.LENGTH_SHORT).show()
                val downloadUrl : String = fileRef.downloadUrl.toString()

                val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(mAuth.currentUser!!.uid)
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
        val fname : String = txtFname.text.toString().trim()
        val lname : String = txtLname.text.toString().trim()
        val nic : String = txtNIC.text.toString().trim()
        val contact : String = txtContact.text.toString().trim()
        val emergencyContact : String = txtEmergency.text.toString().trim()
        val password : String = txtPassword?.text.toString().trim()
        val conPassword : String = txtConfirmPassword?.text.toString().trim()
        val dobText : String = txtDob.text.toString().trim()

        age = Calendar.getInstance().get(Calendar.YEAR) - dob.get(Calendar.YEAR)
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

        if(dobText.isEmpty()){
            txtDob.setError("DOB is required")
            txtDob.requestFocus()
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
            "uid" to FirebaseAuth.getInstance().currentUser!!.uid,
            "fname" to fname,
            "lname" to lname,
            "nic" to nic,
            "dob" to dobText,
            "age" to age.toString(),
            "contact" to contact,
            "emergency" to emergencyContact,
            "active" to "Y"
        )
        database.child(FirebaseAuth.getInstance().currentUser!!.uid).updateChildren(user)
            .addOnSuccessListener {
                val pass = mAuth.currentUser
                pass!!.updatePassword(password).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        progressBar.setVisibility(View.GONE)
                        Toast.makeText(this, "Successfully Updated", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
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