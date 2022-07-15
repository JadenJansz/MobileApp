package lk.nibm.mad_cw

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yalantis.ucrop.UCrop
import java.io.File

class MyProfileMember :  Fragment(), View.OnClickListener {

    private lateinit var avatar : ImageView
    private lateinit var txtFname : EditText
    private lateinit var txtLname : EditText
    private lateinit var txtNIC : EditText
    private lateinit var txtContact : EditText
    private lateinit var txtEmergency : EditText
    private lateinit var btnMyProfile : Button
    private lateinit var progressBar : ProgressBar

    lateinit var mAuth : FirebaseAuth
    lateinit var reference: DatabaseReference
    lateinit var storageReference: StorageReference

    private val CODE_IMAGE_GALLERY = 1
    private val SAMPLE_CROPPED_IMG_NAME = "SampleCropping"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.my_profile_member, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()


        avatar = view.findViewById(R.id.avatar_viewMember)
        avatar.setOnClickListener(this)

        txtFname = view.findViewById(R.id.txt_)
        txtLname = view.findViewById(R.id.txt_email_viewMemberADMIN)
        txtNIC = view.findViewById(R.id.txt_nic_viewMemberADMIN)
        txtContact = view.findViewById(R.id.txt_contact_viewMemberADMIN)
        txtEmergency = view.findViewById(R.id.txt_emergency_viewMemberADMIN)
        progressBar = view.findViewById(R.id.progressBar)

        btnMyProfile = view.findViewById(R.id.btn_viewMemberADMIN)
        btnMyProfile.setOnClickListener(this)

        storageReference = FirebaseStorage.getInstance().reference.child("Profile Images/"+mAuth.currentUser!!.uid+".jpg")
        val localfile = File.createTempFile("tempImage", "jpg")
        storageReference.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            avatar.setImageBitmap(bitmap)

        }

        reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.child(mAuth.currentUser!!.uid).get()
            .addOnSuccessListener {
                if(it.exists()){

                    txtFname.setText(it.child("fname").value.toString())
                    txtLname.setText(it.child("lname").value.toString())
                    txtNIC.setText(it.child("nic").value.toString())
                    txtContact.setText(it.child("contact").value.toString())
                    txtEmergency.setText(it.child("emergency").value.toString())
                }
                else{
                    Toast.makeText(context, "Error Loading Data", Toast.LENGTH_SHORT).show()
                }
            }

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.avatar_viewMember -> {
//                saveProfilePic()
                startActivityForResult(
                    Intent().setAction(Intent.ACTION_GET_CONTENT)
                        .setType("image/*"), CODE_IMAGE_GALLERY
                )
            }

            R.id.btn_viewMemberADMIN -> {
                updateMember()
            }
        }
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
        val uCrop = UCrop.of(uri, Uri.fromFile(File(requireActivity().getCacheDir(), destinationFileName)))
        uCrop.withAspectRatio(1f, 1f)
        uCrop.withMaxResultSize(450, 450)
        uCrop.withOptions(getCropOptions())
        uCrop.start(requireActivity(), this)
    }

    private fun getCropOptions(): UCrop.Options {
        val options = UCrop.Options()
        options.setCompressionQuality(70)
        options.setHideBottomControls(false)
        options.setFreeStyleCropEnabled(true)

        return options
    }

    private fun uploadImageToFirebase(imageUri : Uri){
        val fileRef : StorageReference = storageReference

        fileRef.putFile(imageUri).addOnCompleteListener {task ->
            if (task.isSuccessful){
                Toast.makeText(context, "Photo Uploaded", Toast.LENGTH_SHORT).show()
                var downloadUrl : String = fileRef.child("Profile Images/"+mAuth.currentUser!!.uid+".jpg").downloadUrl.toString()

                val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(mAuth.currentUser!!.uid)
                userRef.child("profileImage").setValue("https://firebasestorage.googleapis.com/v0/b/android-project-5438e.appspot.com/o/Profile%20Images%2F"+mAuth.currentUser!!.uid+".jpg?alt=media") //Had downloadUrl
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            Toast.makeText(context, "Photo Uploaded To Database", Toast.LENGTH_SHORT).show()

                        }
                        else{
                            Toast.makeText(context, "Error Try Again" + task.exception!!.message, Toast.LENGTH_SHORT).show()

                        }
                    }
            }
            else{
                Toast.makeText(context, "Error Try Again", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun updateMember(){
        val fname : String = txtFname.text.toString()
        val lname : String = txtLname.text.toString()
        val nic : String = txtNIC.text.toString()
        val contact : String = txtContact.text.toString()
        val emergencyContact : String = txtEmergency.text.toString()

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

        progressBar.setVisibility(View.VISIBLE)

        reference = FirebaseDatabase.getInstance().getReference("Users")
        val user = mapOf<String, String>(
            "fname" to fname,
            "lname" to lname,
            "nic" to nic,
            "contact" to contact,
            "emergency" to emergencyContact
        )
        reference.child(FirebaseAuth.getInstance().currentUser!!.uid).updateChildren(user)
            .addOnSuccessListener {
                progressBar.setVisibility(View.GONE)
                Toast.makeText(context, "Successfully Updated", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener{
                progressBar.setVisibility(View.GONE)
                Toast.makeText(context, "Failed to Update! Try Again", Toast.LENGTH_SHORT).show()
            }
    }
}