package lk.nibm.mad_cw

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONException
import org.json.JSONObject

class PaymentMEMBER : AppCompatActivity(), PaymentResultListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getSupportActionBar()?.hide()
        setContentView(R.layout.activity_payment_member)

        val checkOut : Checkout = Checkout()

        checkOut.setKeyID("rzp_test_qPDDRARmQCv77P")
        checkOut.setImage(R.drawable.profileicon)

        val jsonObject : JSONObject = JSONObject()

        try {
            jsonObject.put("name", "D's GYM")
            jsonObject.put("description", "Montly Payment")
            jsonObject.put("theme.color","#000000")
            jsonObject.put("currency", "LKR")
            jsonObject.put("amount", "2000")
            jsonObject.put("prefill.contact", "071 563 9188")
            jsonObject.put("prefill.email", "dsgymnasiummattegoda@gmail.com")

            checkOut.open(this, jsonObject)
        }catch (e : JSONException){
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Payment ID")
        builder.setMessage(p0)
        builder.show()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this, p1, Toast.LENGTH_SHORT).show()
    }
}