package lk.nibm.mad_cw

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class AdapterMemberRow(private val userList: ArrayList<UserArray>, context: Context) :  RecyclerView.Adapter<AdapterMemberRow.ViewHolder>() {
    var context : Context
    var onItemClick : ((UserArray) -> Unit)? = null
    init {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterMemberRow.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_members, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem = userList[position]
        holder.email.text = currentItem.email
        holder.name.text = currentItem.fname + " " + currentItem.lname

        if(currentItem.profileImage == ""){
            Glide.with(context).load(R.mipmap.profileicon).into(holder.profileImage)
        }else{
            Glide.with(context).load(currentItem.profileImage).into(holder.profileImage)
        }

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(currentItem)
        }

    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView){
        val name : TextView =itemView.findViewById(R.id.row_member_name)
        val email : TextView =itemView.findViewById(R.id.row_member_email)
        var profileImage : ImageView =itemView.findViewById(R.id.row_member_avatar)
    }


}