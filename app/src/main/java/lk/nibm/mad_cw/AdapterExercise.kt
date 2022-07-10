package lk.nibm.mad_cw

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import pl.droidsonroids.gif.GifImageView
import java.io.IOException
import java.lang.Exception

class AdapterExercise(context: Context, jsonArray : JSONArray) :  RecyclerView.Adapter<AdapterExercise.ViewHolder>() {

    lateinit var context : Context
    var jsonArray : JSONArray

    init {
        this.context = context
        this.jsonArray = jsonArray
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterExercise.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.display_exercises, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        var gifUrl : String = ""

         try {

             val jsonItem = jsonArray.getJSONObject(position)

             gifUrl = jsonItem.getString("gifUrl").toString()
             holder.name.text = jsonItem.getString("name")
             holder.bodyPart.text = jsonItem.getString("bodyPart")
             holder.equipment.text = jsonItem.getString("equipment")
             holder.target.text = jsonItem.getString("target")

                Glide.with(context).load(gifUrl).into(holder.image)

         }catch (e: JSONException) {
             e.printStackTrace()
         }

    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView){
        var name : TextView =itemView.findViewById(R.id.exercise_name)
        var bodyPart : TextView =itemView.findViewById(R.id.exercise_bodypart)
        var target : TextView =itemView.findViewById(R.id.exercise_target)
        var equipment : TextView =itemView.findViewById(R.id.exercise_equipment)
        var image : GifImageView =itemView.findViewById(R.id.gif_lol)
    }


}