package lk.nibm.mad_cw

import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONException
import org.json.JSONObject

class PaymentMEMBER : AppCompatActivity(), PaymentResultListener {

    private lateinit var btnPayment : Button
    private lateinit var txtAmount : EditText

    private var email : String = ""
    private var contact : String = ""

    private var mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        setContentView(R.layout.activity_payment_member)

        txtAmount = findViewById(R.id.txt_feeAmount)
        getDetails()

        btnPayment = findViewById(R.id.btn_payment)
        btnPayment.setOnClickListener {
            val amount : String = txtAmount.text.toString().trim()

            if (amount.isEmpty()){
                txtAmount.error = "Amount is required"
                txtAmount.requestFocus()
            }else{
                makePayment()
            }
        }
    }


    override fun onPaymentSuccess(p0: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Payment ID")
        builder.setMessage(p0)
        builder.show()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this, "Payment Was Not Successful", Toast.LENGTH_SHORT).show()
    }

    private fun makePayment() {
        val checkOut = Checkout()

        checkOut.setKeyID("rzp_test_qPDDRARmQCv77P")
        checkOut.setImage(R.drawable.profileicon)

        val jsonObject = JSONObject()

        try {
            jsonObject.put("name", "D's GYM")
            jsonObject.put("description", "Montly Payment")
            jsonObject.put("theme.color","#000000")
            jsonObject.put("currency", "USD")
            jsonObject.put("amount", "2000")
            jsonObject.put("prefill.contact", contact)
            jsonObject.put("prefill.email", email)

            checkOut.open(this, jsonObject)
        }catch (e : JSONException){
            e.printStackTrace()
        }
    }

    private fun getDetails(){
        reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.child(mAuth.currentUser!!.uid).get()
            .addOnSuccessListener {
                if(it.exists()){

                    email = it.child("email").value.toString()
                    contact = it.child("contact").value.toString()                }
            }
    }

}