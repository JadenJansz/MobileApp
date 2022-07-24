package lk.nibm.mad_cw

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.anychart.chart.common.dataentry.DataEntry
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet


class GraphViewer : AppCompatActivity() {

    var exerciseName : ArrayList<String> = ArrayList()
    lateinit var spinner : Spinner
    lateinit var lineChart : LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getSupportActionBar()?.hide()
        setContentView(R.layout.chart_viewer)


        lineChart = findViewById(R.id.graph_fitness)


        val dB = DBHelper(this)
        val name = dB.getName()

        if (name!!.count == 0){
            val builder = this.let { AlertDialog.Builder(it) }
            builder.setTitle("No Data")
            builder.setMessage("Start adding your workout status daily and return!!!")
            builder.setCancelable(false);
            builder.setPositiveButton("OKAY", DialogInterface.OnClickListener {
                    dialog, id ->
                dialog.cancel()
            })
            val alert = builder.create()

            alert.show()
                return
        }
        else if (name.moveToFirst()) {
            do {
                exerciseName.add(name.getString(0))
                Log.e("Name: " + name.getString(0).length,"")
            }while (name.moveToNext())
        }

        spinner = findViewById(R.id.spinnerExercise)
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, exerciseName)
        arrayAdapter.setDropDownViewResource(R.layout.spin_item)
        spinner.adapter = arrayAdapter

        spinner.onItemSelectedListener = object  :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                var lineDataSet = LineDataSet(lineChartDataSet(exerciseName[position]),"Exercises")
                val iLineDataSets: ArrayList<ILineDataSet> = ArrayList()
                iLineDataSets.add(lineDataSet);
                val lineData: LineData = LineData(iLineDataSets)
                lineChart.data = lineData
                lineChart.invalidate()

                lineDataSet.color = Color.BLUE
                lineDataSet.setCircleColor(Color.GREEN)
                lineDataSet.setDrawCircles(true)
                lineDataSet.setDrawCircleHole(true)
                lineDataSet.lineWidth = 3f
                lineDataSet.circleRadius = 4f
                lineDataSet.circleHoleRadius = 10f
                lineDataSet.valueTextSize = 10f
                lineDataSet.valueTextColor = Color.BLACK
//                lineDataSet.enableDashedLine(10f, 5f, 3f)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun lineChartDataSet(name : String): ArrayList<Entry>? {
        val dataSet = ArrayList<Entry>()
        val dB = DBHelper(this)
        val week = dB.getWeekCount()

        if (week!!.count == 0){
            val toast = ToastClass()
            toast.showToast(this, "No Available Data to Display", 1)

        }
        else if (week != null && week.moveToFirst()) {
            Log.e("First IF", week!!.getString(0))
            for (i in 1 until week!!.getString(0).toInt() + 1)
            {
                val data = dB.getDataGraph((i).toString(), name)

                if(data!!.moveToFirst()) {
                    Log.e("Respnse", data!!.getString(0) + " " + data.getString(1))
                    do {
                        dataSet.add(Entry(data.getString(0).toFloat(), data.getString(1).toFloat()))
                        Log.e("LOOP", i.toString())
                    }while (data.moveToNext())
                }
            }

        }

        return dataSet
    }
}