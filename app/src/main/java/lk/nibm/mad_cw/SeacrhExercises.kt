package lk.nibm.mad_cw

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import org.json.JSONArray
import java.io.IOException

class SeacrhExercises : Fragment() {

    private lateinit var noResults : TextView
    private lateinit var gifRecycleView : RecyclerView
    private lateinit var searchView : SearchView
    var jsonArray = JSONArray()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.search_exercises, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )

        getExercises("leg squat")

        noResults = view.findViewById(R.id.no_results)

        searchView = view.findViewById(R.id.search)
        searchView.clearFocus()
        searchView.setIconifiedByDefault(false)
        searchView.setOnCloseListener { true }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(p0: String): Boolean {
                Log.d("Response", "lllllllllllllllllllllll")
                getExercises(p0)
                return true
            }

        })

        gifRecycleView = view.findViewById(R.id.row_exercise)
        gifRecycleView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


    }

    fun getExercises(name : String) {
        var name = name
        if(name == ""){
            name = "deadlift"
        }
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://exercisedb.p.rapidapi.com/exercises/name/$name")
            .get()
            .addHeader("X-RapidAPI-Key", "b6d501b04amshfc49b3609daef08p1528b2jsn3c8f661e41e1")
            .addHeader("X-RapidAPI-Host", "exercisedb.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(request: Request?, e: IOException?) {
                val context : Context? = null
                Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
                Log.d("Error", "mmmmmmmmmmmmmmmmm")
            }

            override fun onResponse(response: Response?) {
                var resp = response!!.body().string()
                Handler(Looper.getMainLooper()).post(Runnable {
                jsonArray  = JSONArray(resp)

                    if(jsonArray.length() == 0) {
                        noResults.setVisibility(View.VISIBLE)
                    }else{
                        noResults.setVisibility(View.INVISIBLE)
                    }

                val gifAdapter = context?.let { AdapterExercise(it, jsonArray) }
                gifRecycleView.adapter = gifAdapter
            })
            }

        })
    }
}