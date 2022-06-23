package lk.nibm.mad_cw

import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class User(fname: String, lname: String, email: String, role : String) {

     var fname : String
     var lname : String
     var email : String
     var role : String

    init {
        this.fname = fname
        this.lname = lname
        this.email = email
        this.role = role
    }


}