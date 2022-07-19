package lk.nibm.mad_cw

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.database.Cursor
import android.database.SQLException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import java.util.*


class EnterFitnessStatusMEMBER : Fragment(), View.OnClickListener {

    lateinit var drop : Button
    lateinit var btnAddExercise : Button
    lateinit var btnSubmitExercise : Button
    lateinit var dateText : TextView
    lateinit var weekText : TextView
    lateinit var layoutList : LinearLayout
    private lateinit var progressBar : ProgressBar
    lateinit var lblLastUpdated : TextView
    private val calenderInstance: Calendar = Calendar.getInstance()
    var currentWeek : Int = 0

    var exerciseList : ArrayList<Exercises> = ArrayList()
    var DB : DBHelper = DBHelper(context, exerciseList)



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.enter_fitness_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutList = view.findViewById(R.id.layout_list)
        progressBar = view.findViewById(R.id.progressBar)
        weekText = view.findViewById(R.id.txt_week)
        lblLastUpdated = view.findViewById(R.id.lbl_lastUpdated)

        btnAddExercise = view.findViewById(R.id.btn_addExercise)
        btnAddExercise.setOnClickListener(this)

        btnSubmitExercise = view.findViewById(R.id.btn_submitExercise)
        btnSubmitExercise.setOnClickListener(this)

        drop = view.findViewById(R.id.drop)
        drop.setOnClickListener(){
            DB = DBHelper(context, exerciseList)
            DB.deleteAllData()
        }

        dateText = view.findViewById(R.id.txt_date)
        dateText.setOnClickListener(this)


//        dateText.text = calenderInstance.get(Calendar.DAY_OF_MONTH).toString() + " - " + calenderInstance.get(Calendar.MONTH).toString() + " - " + calenderInstance.get(Calendar.YEAR).toString()
        showCurrentData()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_addExercise -> {
                addElement("","")
            }

            R.id.btn_submitExercise -> {
                if(checkValidation()){
                    addDataToDatabase()
                }
            }

            R.id.txt_date -> {
                showDatePickerDialog()
            }
        }

    }

    private fun showDatePickerDialog(){
        val year = calenderInstance.get(Calendar.YEAR)
        val month = calenderInstance.get(Calendar.MONTH)
        val day = calenderInstance.get(Calendar.DAY_OF_MONTH)

        dateText.text = "$day-$month-$year"

        val datePicker = context?.let {
            DatePickerDialog(it,DatePickerDialog.OnDateSetListener{
                view, year, month, day ->
                dateText.text = "$day-$month-$year"
            }, year, month, day)
        }

        datePicker?.show()
    }

    private fun checkValidation(): Boolean {
        exerciseList.clear()
        var result : Boolean = true
        lateinit var exercise : Exercises

        for(i in 0 until layoutList.childCount){
            val exerciseView : View = layoutList.getChildAt(i)

            val editTextName : EditText = exerciseView.findViewById(R.id.fitness_name)
            val editTextWeight : EditText = exerciseView.findViewById(R.id.txt_date)

            if(editTextName.text.toString() != "" && editTextWeight.text.toString() != "" && dateText.text.toString() != "" && weekText.text.toString() != ""){
                exercise = Exercises(editTextName.text.toString(), editTextWeight.text.toString().toDouble(), dateText.text.toString(), weekText.text.toString())
            }
            else{
                result = false
                Toast.makeText(context, "Enter Details Completely", Toast.LENGTH_SHORT).show()
                break
            }

            exerciseList.add(exercise)

        }

        if(exerciseList.size == 0){
            result = false
            Toast.makeText(context, "Enter the date", Toast.LENGTH_SHORT).show()
        }else if(!result){
            Toast.makeText(context, "Enter All Details", Toast.LENGTH_SHORT).show()
        }

        return result
    }

    private fun addElement(name : String, weight : String) {
        val exercise : View = layoutInflater.inflate(R.layout.row_enter_fitness, null, false)

        val fitnessName : EditText = exercise.findViewById(R.id.fitness_name)
        val fitnessWeight : EditText = exercise.findViewById(R.id.txt_date)
        val fitnessRemove : ImageView = exercise.findViewById(R.id.image_remove)

        fitnessName.setText(name)
        fitnessWeight.setText(weight)


        fitnessRemove.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                removeElement(exercise)
            }

        })

        layoutList.addView(exercise)
    }

    private fun removeElement(view : View){
        val builder = context?.let { AlertDialog.Builder(it) }
        builder?.setTitle("Remove Exercise")
        builder?.setMessage("Remove This Field ?")
        builder?.setPositiveButton("Remove", DialogInterface.OnClickListener {
                dialog, id ->
            dialog.cancel()
            layoutList.removeView(view)
        })
        builder?.setNegativeButton("Cancel", DialogInterface.OnClickListener{
                dialog, id ->
            dialog.cancel()
        })
        val alert = builder?.create()
        alert!!.show()

    }

    private fun addDataToDatabase(){
        DB = DBHelper(context, exerciseList)

        try {
            if(weekText.text.toString().toInt()  == currentWeek + 1 ) {
                for(i in 0 until exerciseList.size){
                    val insertTrue = DB.insertData(
                        FirebaseAuth.getInstance().currentUser!!.uid,
                        exerciseList[i].date!!,
                        exerciseList[i].week!!,
                        exerciseList[i].name!!,
                        exerciseList[i].weight)

                    if(insertTrue){
                        Toast.makeText(context, "New Entry Added", Toast.LENGTH_SHORT).show()
                        currentWeek += 1
                    }else{
                        Toast.makeText(context, "This week is already added", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else if(weekText.text.toString().toInt()  > currentWeek + 1){
                Toast.makeText(context, "Week "+(currentWeek+1)+" is not recorded", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context, "This week is already added", Toast.LENGTH_SHORT).show()

            }

        }catch (e: SQLException){
            e.printStackTrace()
        }



    }

    private fun showCurrentData(){

        DB = DBHelper(context, exerciseList)

        try {
            val res: Cursor? = DB.getData(FirebaseAuth.getInstance().currentUser!!.uid)
            if (res!!.count === 0) {
                Toast.makeText(context, "No Entry Exists", Toast.LENGTH_SHORT).show()
                return
            }


            if (res != null && res.moveToFirst()) {
                do {
                    addElement(res.getString(0), res.getString(3))
                    lblLastUpdated.text = "Last Updated Date " + res.getString(1).toString()
                    weekText.text = res.getString(2).toString()
                    currentWeek = res.getString(2).toString().toInt()
                }while (res.moveToNext())
            }
        }catch (e: SQLException){
            e.printStackTrace()
        }

    }
}