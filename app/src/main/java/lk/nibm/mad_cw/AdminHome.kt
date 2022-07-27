package lk.nibm.mad_cw

import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class AdminHome : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

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
        setContentView(R.layout.activity_admin_home)

        val refresh = intent.getStringExtra("refresh")

        if(refresh == "refresh"){
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ViewMembersADMIN()).commit()
            findViewById<View?>(R.id.img_adminhome).visibility = View.GONE
            findViewById<View?>(R.id.lbl_dsgym).visibility = View.GONE
            findViewById<View?>(R.id.lbl_welcome).visibility = View.GONE
        }

        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)
        val navigationView : NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawer, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

//        if(savedInstanceState == null){
//            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,  AddMemberADMIN()).commit()
//            navigationView.setCheckedItem(R.id.add_member)
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


    }

    override fun onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }
        else{
            drawer.openDrawer(GravityCompat.START)
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.add_member -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container,  AddMemberADMIN()).commit()
                findViewById<View?>(R.id.img_adminhome).visibility = View.GONE
                findViewById<View?>(R.id.lbl_dsgym).visibility = View.GONE
                findViewById<View?>(R.id.lbl_welcome).visibility = View.GONE
            }

            R.id.view_member -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ViewMembersADMIN()).commit()
                findViewById<View?>(R.id.img_adminhome).visibility = View.GONE
                findViewById<View?>(R.id.lbl_dsgym).visibility = View.GONE
                findViewById<View?>(R.id.lbl_welcome).visibility = View.GONE
            }

            R.id.notice -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, NoticeADMIN()).commit()
                findViewById<View?>(R.id.img_adminhome).visibility = View.GONE
                findViewById<View?>(R.id.lbl_dsgym).visibility = View.GONE
                findViewById<View?>(R.id.lbl_welcome).visibility = View.GONE
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
                    val toast = ToastClass()
                    toast.showToast(this, "Error Loading  Data", 1)
                }
            }
    }
}