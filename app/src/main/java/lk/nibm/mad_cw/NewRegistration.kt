package lk.nibm.mad_cw

//import android.support.v7.app.AppCompatActivity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
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
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
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
    private lateinit var txtFname : TextInputEditText
    private lateinit var txtFnameLayout : TextInputLayout
    private lateinit var txtLname : TextInputEditText
    private lateinit var txtLnameLayout : TextInputLayout
    private lateinit var txtNIC : TextInputEditText
    private lateinit var txtNICLayout : TextInputLayout
    private lateinit var txtDob : TextInputEditText
    private lateinit var txtDobLayout : TextInputLayout
    private lateinit var imgCalender : ImageView
    private lateinit var txtContact : TextInputEditText
    private lateinit var txtContactLayout : TextInputLayout
    private lateinit var txtEmergency : TextInputEditText
    private lateinit var txtEmergencyLayout : TextInputLayout
    private var txtPassword : TextInputEditText? = null
    private lateinit var txtPasswordLayout : TextInputLayout
    private var txtConfirmPassword : TextInputEditText? = null
    private lateinit var txtConfirmPasswordLayout : TextInputLayout
    private lateinit var progressBar : ProgressBar

    private lateinit var mAuth : FirebaseAuth
    private lateinit var database : DatabaseReference
    private lateinit var storageReference: StorageReference
    private val calenderInstance: Calendar = Calendar.getInstance()
    private var yearText : Int = 0
    private val dob = calenderInstance

    val toast = ToastClass()
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
        txtDobLayout = findViewById(R.id.txt_dobLayout)
        imgCalender = findViewById(R.id.image_dob)
        txtDobLayout.setOnClickListener(this)
        imgCalender.setOnClickListener(this)

        txtFname = findViewById(R.id.txt_fname)
        txtFnameLayout = findViewById(R.id.txt_fnameLayout)
        txtLname = findViewById(R.id.txt_lname)
        txtLnameLayout = findViewById(R.id.txt_lnameLayout)
        txtNIC = findViewById(R.id.txt_nic)
        txtNICLayout = findViewById(R.id.txt_nicLayout)
        txtContact = findViewById(R.id.txt_contact)
        txtContactLayout = findViewById(R.id.txt_contactLayout)
        txtEmergency = findViewById(R.id.txt_emergency_contact)
        txtEmergencyLayout = findViewById(R.id.txt_emergency_contactLayout)
        txtPassword = findViewById(R.id.txt_password)
        txtPasswordLayout = findViewById(R.id.txt_passwordLayout)
        txtConfirmPassword = findViewById(R.id.txt_confpass)
        txtConfirmPasswordLayout = findViewById(R.id.txt_confpassLayout)


        val layoutArray : Map<TextInputLayout, TextInputEditText> = mapOf<TextInputLayout, TextInputEditText>(txtFnameLayout to txtFname,
            txtLnameLayout to txtLname,
            txtNICLayout to txtNIC,
            txtDobLayout to txtDob,
            txtContactLayout to txtContact,
            txtEmergencyLayout to txtEmergency,
            (txtPasswordLayout to txtPassword!!),
            txtConfirmPasswordLayout to txtConfirmPassword!! )

        for (i in layoutArray.keys){
            layoutArray.get(i)!!.setOnClickListener(){
                i.error = ""
                i.boxStrokeColor = Color.rgb(213,128,255)
            }
        }

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

            R.id.txt_dobLayout, R.id.image_dob -> {
                showDatePickerDialog()
                txtDobLayout.error = ""
                txtDobLayout.boxStrokeColor = Color.rgb(213,128,255)
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
                toast.showToast(this, "Image Saved", 0)
                val downloadUrl : String = fileRef.downloadUrl.toString()

                val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(mAuth.currentUser!!.uid)
                userRef.child("profileImage").setValue(downloadUrl)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
//                            Toast.makeText(this, "Photo Uploaded To Database", Toast.LENGTH_SHORT).show()

                        }
                        else{
                            toast.showToast(this, "There Was An Error, Please Try Again!", 1)

                        }
                    }
            }
            else{
                toast.showToast(this, "There Was An Error, Please Try Again!", 1)
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
            txtFnameLayout.error = "*name is required"
            txtFnameLayout.boxStrokeColor = Color.RED
            txtFname.requestFocus()
            return
        }

        if(lname.isEmpty()){
            txtLnameLayout.error = "*name is required"
            txtLnameLayout.boxStrokeColor = Color.RED
            txtLname.requestFocus()
            return
        }

        if(nic.isEmpty()){
            txtNICLayout.error = "*nic is required"
            txtNICLayout.boxStrokeColor = Color.RED
            txtNIC.requestFocus()
            return
        }
        if(nic.length < 10 || nic.length > 12) {
            txtNICLayout.error = "*invalid nic"
            txtNICLayout.boxStrokeColor = Color.RED
            txtNIC.requestFocus()
            return
        }

        if(dobText.isEmpty()){
            txtDobLayout.error = "*DOB is required"
            txtDobLayout.boxStrokeColor = Color.RED
            txtDob.requestFocus()
            return
        }

        if(contact.isEmpty()){
            txtContactLayout.error = "*contact number is required"
            txtContactLayout.boxStrokeColor = Color.RED
            txtContact.requestFocus()
            return
        }
        if(contact.length < 10){
            txtContactLayout.error = "*invalid contact number"
            txtContactLayout.boxStrokeColor = Color.RED
            txtContact.requestFocus()
            return
        }

        if(emergencyContact.isEmpty()){
            txtEmergencyLayout.error = "*contact number is required"
            txtEmergencyLayout.boxStrokeColor = Color.RED
            txtEmergency.requestFocus()
            return
        }
        if(emergencyContact.length < 10){
            txtEmergencyLayout.error = "*invalid contact number"
            txtEmergencyLayout.boxStrokeColor = Color.RED
            txtEmergency.requestFocus()
            return
        }

        if(password.length < 6){
            txtPassword!!.setError("Password should be more than 6 characters")
            txtPassword!!.requestFocus()
            return
        }
        if(password.isEmpty()){
            txtPasswordLayout.error = "*password cannot be empty"
            txtPasswordLayout.boxStrokeColor = Color.RED
            txtPassword!!.requestFocus()
            return
        }
        if(conPassword.isEmpty()){
            txtConfirmPasswordLayout.error = "*required"
            txtConfirmPasswordLayout.boxStrokeColor = Color.RED
            txtConfirmPassword!!.requestFocus()
            return
        }
        else if (conPassword != password){
            txtConfirmPasswordLayout.error = "*passwords do not match"
            txtConfirmPasswordLayout.boxStrokeColor = Color.RED
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
                        toast.showToast(this, "Successfully Updated", 0)

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }

            }
            .addOnFailureListener{
                progressBar.setVisibility(View.GONE)
                toast.showToast(this, "Failed To Update, Please Try Again!", 1)
            }
    }
}