package lk.nibm.mad_cw

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.my_profile_member, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()


        avatar = view.findViewById(R.id.avatar_viewMemberADMIN)
        avatar.setOnClickListener(this)

        txtFname = view.findViewById(R.id.txt_name_viewMemberADMIN)
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
            R.id.avatar_viewMemberADMIN -> {
                saveProfilePic()
            }

            R.id.btn_viewMemberADMIN -> {
                updateMember()
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
        var fileRef : StorageReference = storageReference

        fileRef.putFile(imageUri).addOnCompleteListener {task ->
            if (task.isSuccessful){
                Toast.makeText(context, "Photo Uploaded", Toast.LENGTH_SHORT).show()
                var downloadUrl : String = fileRef.child("Profile Images/"+mAuth.currentUser!!.uid+".jpg").downloadUrl.toString()

                var userRef = FirebaseDatabase.getInstance().reference.child("Users").child(mAuth.currentUser!!.uid)
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
        var fname : String = txtFname.text.toString()
        var lname : String = txtLname.text.toString()
        var nic : String = txtNIC.text.toString()
        var contact : String = txtContact.text.toString()
        var emergencyContact : String = txtEmergency.text.toString()

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