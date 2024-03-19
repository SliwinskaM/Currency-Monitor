package com.example.malgorzata_sliwinska_czw_9_30

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import org.json.JSONObject

class HistoryActivity : AppCompatActivity() {
    internal lateinit var todaysRateTextView : TextView
    internal lateinit var yesterdaysRateTextView : TextView
    internal lateinit var monthlyChartTitle : TextView
    internal lateinit var weeklyChartTitle : TextView
    internal lateinit var lineChartMonth: LineChart
    internal lateinit var lineChartWeek: LineChart
    private lateinit var currencyCode: String
    private lateinit var tableId: String
    private lateinit var dataForChart: Array<Pair<String, Double>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        currencyCode = intent.getStringExtra("currencyCode")!!
        tableId = intent.getStringExtra("tableId")!!
        todaysRateTextView = findViewById(R.id.todaysRate)
        yesterdaysRateTextView = findViewById(R.id.yesterdaysRate)
        monthlyChartTitle = findViewById(R.id.monthlyChartTitle)
        weeklyChartTitle = findViewById(R.id.weeklyChartTitle)
        lineChartMonth = findViewById(R.id.historyChartMonth)
        lineChartWeek = findViewById(R.id.historyChartWeek)
        getHistoricRates()
    }

    private fun getHistoricRates() {
        val queue = DataHolderSingleton.queue
        val url = "http://api.nbp.pl/api/exchangerates/rates/%s/%s/last/30?format=json".format(tableId, currencyCode)

        val historicRatesRequest = JsonObjectRequest( Request.Method.GET, url, null,
                    Response.Listener { response ->
                        loadHistoricData(response)
                        showData()
                    },
                    Response.ErrorListener {
                        TODO()
                    }
        )
        queue.add(historicRatesRequest)
    }

    private fun showData() {
        todaysRateTextView.text = getString(R.string.todaysRateTextView, dataForChart.last().second)
        yesterdaysRateTextView.text = getString(R.string.yesterdaysRateTextView, dataForChart[dataForChart.size-2].second)
        monthlyChartTitle.text = getString(R.string.chartTitle, currencyCode, 30)
        weeklyChartTitle.text = getString(R.string.chartTitle, currencyCode, 7)

        val entries = ArrayList<Entry>()
        for ((index, element) in dataForChart.withIndex()) {
            entries.add(Entry(index.toFloat(), element.second.toFloat()))
        }

//        month
        val entriesDataSetMonth = LineDataSet(entries, "Kursy")
        entriesDataSetMonth.lineWidth = 5f
        entriesDataSetMonth.color = R.color.purple_700
        entriesDataSetMonth.circleRadius = 3.5f
        entriesDataSetMonth.setCircleColor(R.color.purple_500)
        entriesDataSetMonth.setDrawValues(false)

        val lineDataMonth = LineData(entriesDataSetMonth)
        lineChartMonth.data = lineDataMonth
        lineChartMonth.axisLeft.setDrawGridLines(false)
        lineChartMonth.axisRight.setDrawGridLines(false)
        lineChartMonth.xAxis.setDrawGridLines(false)
        lineChartMonth.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChartMonth.xAxis.valueFormatter = IndexAxisValueFormatter(dataForChart.map{ it.first }.toTypedArray())
        lineChartMonth.legend.isEnabled = false
        lineChartMonth.description.setEnabled(false)
        lineChartMonth.invalidate()

//        week
        val entriesWeek = entries.slice(0..6)
        val entriesDataSetWeek = LineDataSet(entriesWeek, "Kursy")
        entriesDataSetWeek.lineWidth = 5f
        entriesDataSetWeek.color = R.color.purple_700
        entriesDataSetWeek.circleRadius = 3.5f
        entriesDataSetWeek.setCircleColor(R.color.purple_500)
        entriesDataSetWeek.setDrawValues(false)

        val lineDataWeek = LineData(entriesDataSetWeek)
        lineChartWeek.data = lineDataWeek
        lineChartWeek.axisLeft.setDrawGridLines(false)
        lineChartWeek.axisRight.setDrawGridLines(false)
        lineChartWeek.xAxis.setDrawGridLines(false)
        lineChartWeek.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChartWeek.xAxis.valueFormatter = IndexAxisValueFormatter(dataForChart.map{ it.first }.toTypedArray())
        lineChartWeek.legend.isEnabled = false
        lineChartWeek.description.setEnabled(false)
        lineChartWeek.invalidate()
    }

    private fun loadHistoricData(response: JSONObject?) {
        response?.let {
            val rates = response.getJSONArray("rates")
            val ratesNum = rates.length()
            val tmpData = arrayOfNulls<Pair<String, Double>>(ratesNum)

            for(i in 0 until ratesNum) {
                val date = rates.getJSONObject(i).getString("effectiveDate")
                val currencyRate = rates.getJSONObject(i).getDouble("mid")
                tmpData[i] = Pair(date, currencyRate)
            }
            dataForChart = tmpData as Array<Pair<String, Double>>
        }
    }
}