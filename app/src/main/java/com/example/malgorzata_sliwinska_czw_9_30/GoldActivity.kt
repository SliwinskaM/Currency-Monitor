package com.example.malgorzata_sliwinska_czw_9_30

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import org.json.JSONArray
import org.json.JSONObject

class GoldActivity : AppCompatActivity() {
    internal lateinit var todaysRateTextView : TextView
    internal lateinit var monthlyChartTitle : TextView
    internal lateinit var lineChart: LineChart
    private lateinit var dataForChart: Array<Pair<String, Double>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gold)

        todaysRateTextView = findViewById(R.id.todaysRateGold)
        monthlyChartTitle = findViewById(R.id.monthlyChartTitleGold)
        lineChart = findViewById(R.id.historyChartGold)
        getHistoricRates()
    }

    private fun getHistoricRates() {
        val queue = DataHolderSingleton.queue
        val url = "http://api.nbp.pl/api/cenyzlota/last/30?format=json"

        val historicRatesRequest = JsonArrayRequest( Request.Method.GET, url, null,
            Response.Listener { response ->
                loadHistoricData(response)
                showData()
            },
            Response.ErrorListener {
                TODO("Error to implement")
            }
        )
        queue.add(historicRatesRequest)
    }

    private fun showData() {
        todaysRateTextView.text = getString(R.string.todaysRateTextView, dataForChart.last().second)
        monthlyChartTitle.text = getString(R.string.chartTitle, "złota", 30)

        val entries = ArrayList<Entry>()
        for ((index, element) in dataForChart.withIndex()) {
            entries.add(Entry(index.toFloat(), element.second.toFloat()))
        }
        val entriesDataSet = LineDataSet(entries, "Cena")
        entriesDataSet.lineWidth = 5f
        entriesDataSet.color = R.color.purple_700
        entriesDataSet.circleRadius = 3.5f
        entriesDataSet.setCircleColor(R.color.purple_500)
        entriesDataSet.setDrawValues(false)

        val lineData = LineData(entriesDataSet)
        lineChart.data = lineData
        lineChart.axisLeft.setDrawGridLines(false)
        lineChart.axisRight.setDrawGridLines(false)
        lineChart.xAxis.setDrawGridLines(false)
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(dataForChart.map{ it.first }.toTypedArray())
        lineChart.legend.isEnabled = false
        lineChart.description.isEnabled = false

        lineChart.invalidate()
    }

    private fun loadHistoricData(response: JSONArray?) {
        response?.let {
            val ratesCount = response.length()
            val tmpData = arrayOfNulls<Pair<String,Double>>(ratesCount)
            for(i in 0 until (ratesCount)){
                val date = response.getJSONObject(i).getString("data")
                val currencyRate = response.getJSONObject(i).getDouble("cena")

                tmpData[i]=Pair(date,currencyRate)
            }
            dataForChart = tmpData as Array<Pair<String,Double>>
        }
    }
}