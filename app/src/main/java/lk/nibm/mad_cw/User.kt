package lk.nibm.mad_cw

import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class User(uid: String,dob : String,age:String,fname: String, lname: String, email: String, nic : String, contact : String, emergency : String, role : String, profileImage : String) {

     var uid : String
     var dob : String
     var age : String
     var fname : String
     var lname : String
     var email : String
     var role : String
     var nic : String
     var contact : String
     var emergency : String
     var profileImage : String

    init {
        this.uid = uid
        this.dob = dob
        this.age = age
        this.fname = fname
        this.lname = lname
        this.email = email
        this.nic = nic
        this.contact = contact
        this.emergency = emergency
        this.role = role
        this.profileImage = profileImage
    }


}