package lk.nibm.mad_cw

import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class User(fname: String, lname: String, email: String, nic : String, contact : String, emergency : String, role : String) {

     var fname : String
     var lname : String
     var email : String
     var role : String
     var nic : String
     var contact : String
     var emergency : String

    init {
        this.fname = fname
        this.lname = lname
        this.email = email
        this.nic = nic
        this.contact = contact
        this.emergency = emergency
        this.role = role
    }


}