package lk.nibm.mad_cw

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.type.DateTime
import java.util.*

class NoticeADMIN : Fragment() {

    private lateinit var txtSubject : EditText
    private lateinit var txtMessage : EditText
    private lateinit var btnSendMessage : Button

    private lateinit var progressBar : ProgressBar

    lateinit var reference: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.admin_notice, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtSubject = view.findViewById(R.id.txt_subject)
        txtMessage = view.findViewById(R.id.txt_message)
        progressBar = view.findViewById(R.id.progressBar)

        btnSendMessage = view.findViewById(R.id.btn_sendMessage)
        btnSendMessage.setOnClickListener {
            sendMessage()
        }
    }

    private fun sendMessage() {
        val subject = txtSubject.text.toString()
        val message = txtMessage.text.toString()

        if (subject.isEmpty()){
            txtSubject.setError("Full Name is required")
            txtSubject.requestFocus()
            return
        }

        if(message.isEmpty()){
            txtMessage.setError("Full Name is required")
            txtMessage.requestFocus()
            return
        }

        progressBar.setVisibility(View.VISIBLE)

        reference = FirebaseDatabase.getInstance().getReference("Notice")
        val notice = mapOf<String,String>(
            "date" to Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString() + "-" + Calendar.getInstance().get(Calendar.MONTH) + "-" + Calendar.getInstance().get(Calendar.YEAR),
            "subject" to subject,
            "message" to message

        )
        reference.child(Calendar.getInstance().getTime().toString()).setValue(notice)
            .addOnSuccessListener {
                progressBar.setVisibility(View.GONE)
                Toast.makeText(context, "Successfully Sent", Toast.LENGTH_SHORT).show()
                txtSubject.setText("")
                txtMessage.setText("")
            }
            .addOnFailureListener{
                progressBar.setVisibility(View.GONE)
                Toast.makeText(context, "Failed to Update! Try Again", Toast.LENGTH_SHORT).show()
            }
    }
}