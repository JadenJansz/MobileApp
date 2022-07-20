package lk.nibm.mad_cw

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class SplashScreen : AppCompatActivity() {

    lateinit var iv_note : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getSupportActionBar()?.hide()
        setContentView(R.layout.activity_splash_screen)

        iv_note = findViewById(R.id.iv_note)
        iv_note.alpha =0f
        iv_note.animate().setDuration(1000).alpha( 1f).withEndAction{
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }


    }

}