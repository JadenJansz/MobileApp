package lk.nibm.mad_cw

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Layout
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class ToastClass : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.toast_layout)
    }

    fun showToast(context: Context, message : String, code : Int) {
        val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.toast_layout, null)
        val toastText = layout.findViewById<TextView>(R.id.toast_text)
        val toastImage = layout.findViewById<ImageView>(R.id.toast_image)
        val toastBackground = layout.findViewById<LinearLayout>(R.id.toast_root)

        if(code == 0) {
            toastText.text = message
            toastImage.setImageResource(R.drawable.success_icon)
            toastBackground.setBackgroundResource(R.drawable.toast_back_green)
            val toast = Toast(context)
            toast.duration = Toast.LENGTH_LONG
            toast.setView(layout)
            toast.show()
        }else if(code == 1) {
            toastText.text = message
            toastImage.setImageResource(R.drawable.error_icon)
            toastBackground.setBackgroundResource(R.drawable.toast_back_red)
            val toast = Toast(context)
            toast.duration = Toast.LENGTH_LONG
            toast.setView(layout)
            toast.show()
        }else if(code == 2) {
            toastText.text = message
            toastImage.setImageResource(R.drawable.information_icon)
            toastBackground.setBackgroundResource(R.drawable.toast_back_blue)
            val toast = Toast(context)
            toast.duration = Toast.LENGTH_LONG
            toast.setView(layout)
            toast.show()
        }
    }
}