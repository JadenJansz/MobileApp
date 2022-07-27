package lk.nibm.mad_cw

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yalantis.ucrop.UCrop
import java.io.File

class MyProfileMember :  Fragment(), View.OnClickListener {

    private lateinit var avatar : ImageView
    private lateinit var txtFname : TextInputEditText
    private lateinit var txtFnameLayout : TextInputLayout
    private lateinit var txtLname : TextInputEditText
    private lateinit var txtLnameLayout : TextInputLayout
    private lateinit var txtNIC : TextInputEditText
    private lateinit var txtNICLayout : TextInputLayout
    private lateinit var txtContact : TextInputEditText
    private lateinit var txtContactLayout : TextInputLayout
    private lateinit var txtEmergency : TextInputEditText
    private lateinit var txtEmergencyLayout : TextInputLayout
    private lateinit var btnMyProfile : Button
    private lateinit var progressBar : ProgressBar

    lateinit var mAuth : FirebaseAuth
    lateinit var reference: DatabaseReference
    lateinit var storageReference: StorageReference

    val toast = ToastClass()
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

        txtFname = view.findViewById(R.id.txt_fnameviewMemberADMIN)
        txtLname = view.findViewById(R.id.txt_lname_viewMemberADMIN)
        txtNIC = view.findViewById(R.id.txt_nic_viewMemberADMIN)
        txtContact = view.findViewById(R.id.txt_contact_viewMemberADMIN)
        txtEmergency = view.findViewById(R.id.txt_emergency_viewMemberADMIN)

        txtFnameLayout = view.findViewById(R.id.txt_fnameviewMemberADMINLayout)
        txtLnameLayout = view.findViewById(R.id.txt_lname_viewMemberADMINLayout)
        txtNICLayout = view.findViewById(R.id.txt_nic_viewMemberADMINLayout)
        txtContactLayout = view.findViewById(R.id.txt_contact_viewMemberADMINLayout)
        txtEmergencyLayout = view.findViewById(R.id.txt_emergency_viewMemberADMINLayout)
        progressBar = view.findViewById(R.id.progressBar)

        btnMyProfile = view.findViewById(R.id.btn_viewMemberADMIN)
        btnMyProfile.setOnClickListener(this)

        val layoutArray : Map<TextInputLayout, TextInputEditText> = mapOf<TextInputLayout, TextInputEditText>(txtFnameLayout to txtFname,
            txtLnameLayout to txtLname,
            txtNICLayout to txtNIC,
            txtContactLayout to txtContact,
            txtEmergencyLayout to txtEmergency )

        for (i in layoutArray.keys){
            layoutArray.get(i)!!.setOnClickListener(){
                i.error = ""
                i.boxStrokeColor = Color.rgb(213,128,255)
            }
        }

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
                    toast.showToast(requireContext(), "There Was An Error, Please Try Again!", 1)
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
        try {
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
        }catch (e: Exception) {
            e.printStackTrace()
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
                toast.showToast(requireContext(), "Image Saved", 0)
                var downloadUrl : String = fileRef.child("Profile Images/"+mAuth.currentUser!!.uid+".jpg").downloadUrl.toString()

                val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(mAuth.currentUser!!.uid)
                userRef.child("profileImage").setValue("https://firebasestorage.googleapis.com/v0/b/android-project-5438e.appspot.com/o/Profile%20Images%2F"+mAuth.currentUser!!.uid+".jpg?alt=media") //Had downloadUrl
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
//                            Toast.makeText(context, "Photo Uploaded To Database", Toast.LENGTH_SHORT).show()

                        }
                        else{
                            toast.showToast(requireContext(), "There Was An Error, Please Try Again!", 1)

                        }
                    }
            }
            else{
                toast.showToast(requireContext(), "There Was An Error, Please Try Again!", 1)

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
                toast.showToast(requireContext(), "Successfully Updated", 0)

            }
            .addOnFailureListener{
                progressBar.setVisibility(View.GONE)
                toast.showToast(requireContext(), "Failed To Update, Please Try Again!", 1)
            }
    }
}