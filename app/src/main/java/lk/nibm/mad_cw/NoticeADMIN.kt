package lk.nibm.mad_cw

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.type.DateTime
import java.util.*

class NoticeADMIN : Fragment() {

    private lateinit var txtSubject : TextInputEditText
    private lateinit var txtSubjectLayout : TextInputLayout
    private lateinit var txtMessage : TextInputEditText
    private lateinit var txtMessageLayout : TextInputLayout
    private lateinit var btnSendMessage : Button

    private lateinit var progressBar : ProgressBar

    lateinit var reference: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.admin_notice, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtSubject = view.findViewById(R.id.txt_subject)
        txtSubject.setOnClickListener(){
            txtSubjectLayout.error = ""
            txtSubjectLayout.boxStrokeColor = Color.rgb(213,128,255)
        }
        txtSubjectLayout = view.findViewById(R.id.txt_subjectLayout)

        txtMessage = view.findViewById(R.id.txt_message)
        txtMessage.setOnClickListener(){
            txtMessageLayout.error = ""
            txtMessageLayout.boxStrokeColor = Color.rgb(213,128,255)
        }
        txtMessageLayout = view.findViewById(R.id.txt_messageLayout)

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
            txtSubjectLayout.error = "*subject is required"
            txtSubjectLayout.boxStrokeColor = Color.RED
            txtSubject.requestFocus()
            return
        }

        if(message.isEmpty()){
            txtMessageLayout.error = "*subject is required"
            txtMessageLayout.boxStrokeColor = Color.RED
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
                val toast = ToastClass()
                toast.showToast(requireContext(), "Successfully Sent", 0)
                txtSubject.setText("")
                txtMessage.setText("")
            }
            .addOnFailureListener{
                progressBar.setVisibility(View.GONE)
                val toast = ToastClass()
                toast.showToast(requireContext(), "Failed to Send! Try Again", 1)
            }
    }
}