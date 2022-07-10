package lk.nibm.mad_cw

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import kotlin.properties.Delegates

class MemberHome : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    PaymentResultListener {

    private lateinit var drawer : DrawerLayout
    private lateinit var avatar : ImageView
    private lateinit var nav_name : TextView
    private lateinit var nav_email : TextView

    private lateinit var storageReference: StorageReference
    private lateinit var mAuth : FirebaseAuth
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getSupportActionBar()?.hide()
        setContentView(R.layout.activity_member_home)



        var toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)
        var navigationView : NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        var toggle = ActionBarDrawerToggle(this, drawer, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

//        if(savedInstanceState == null){
//            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,  UseClass).commit()
//            navigationView.setCheckedItem(R.id.my_profile)
//        }

        var header : View = navigationView.getHeaderView(0)

        mAuth = FirebaseAuth.getInstance()
        getNavDetails()

        nav_name = header.findViewById(R.id.nav_bar_name)
        nav_email = header.findViewById(R.id.nav_bar_email)

        avatar = header.findViewById(R.id.nav_bar_image)


        storageReference = FirebaseStorage.getInstance().reference.child("Profile Images/"+mAuth.currentUser!!.uid+".jpg")
        val localfile = File.createTempFile("tempImage", "jpg")
        storageReference.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)

            Glide.with(this).load(bitmap).into(avatar)
        }

        if(avatar.drawable == null){
            Toast.makeText(this, "No image", Toast.LENGTH_SHORT).show()

            var drawableImg : Drawable = resources.getDrawable(R.drawable.profileicon)
            avatar.setImageDrawable(drawableImg)
        }
    }

    override fun onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }
        else{
            super.onBackPressed()
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.my_profile -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container,  MyProfileMember()).commit()
            }

            R.id.track_workout -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container,  EnterFitnessStatusMEMBER()).commit()
            }

            R.id.search_exercises -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container,  SeacrhExercises()).commit()
            }

            R.id.payment -> {
//                var payment = Intent(this, PaymentMEMBER::class.java)
//                startActivity(payment)
                doPayment()
            }

            R.id.logout -> {
                var builder =  AlertDialog.Builder(this)
                builder.setTitle("Log Out")
                builder.setMessage("Are You Sure ?")
                builder.setPositiveButton("Leave", DialogInterface.OnClickListener {
                    dialog, id ->
                    mAuth.signOut()
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                    dialog.cancel()
                })
                builder.setNegativeButton("Stay", DialogInterface.OnClickListener{
                    dialog, id ->
                    dialog.cancel()
                })
                var alert = builder.create()
                alert.show()

            }

        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun getNavDetails(){
        reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.child(mAuth.currentUser!!.uid).get()
            .addOnSuccessListener {
                if(it.exists()){

                    nav_name.setText(it.child("fname").value.toString() + " " + it.child("lname").value.toString())
                    nav_email.setText(it.child("email").value.toString())
                }
                else{
                    Toast.makeText(this, "Error Loading Data", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun doPayment(){
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