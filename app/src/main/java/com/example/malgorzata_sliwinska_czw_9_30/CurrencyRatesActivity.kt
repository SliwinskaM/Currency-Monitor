package com.example.malgorzata_sliwinska_czw_9_30

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.mynameismidori.currencypicker.ExtendedCurrency
import org.json.JSONArray


class CurrencyRatesActivity : AppCompatActivity() {
    internal lateinit var currenciesRecyclerView: RecyclerView
    internal lateinit var adapter: CurrenciesRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency_rates)
        currenciesRecyclerView = findViewById(R.id.currenciesRecycler)
        currenciesRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CurrenciesRecyclerAdapter(emptyArray(), this)
        currenciesRecyclerView.adapter = adapter

        makeRequest()
    }

    fun makeRequest() {
        val queue = DataHolderSingleton.queue

        val urlLast2TableA = "http://api.nbp.pl/api/exchangerates/tables/A/last/2?format=json"
        val urlLast2TableB = "http://api.nbp.pl/api/exchangerates/tables/B/last/2?format=json"

        val currenciesRatesRequestTableA = JsonArrayRequest(Request.Method.GET, urlLast2TableA, null,
            Response.Listener { response ->
                adapter.dataSet = loadData(response, "A")
                adapter.notifyDataSetChanged()
            },
            Response.ErrorListener {
                TODO()
            }
        )
        queue.add(currenciesRatesRequestTableA)

        val currenciesRatesRequestTableB = JsonArrayRequest(Request.Method.GET, urlLast2TableB, null,
            Response.Listener { response ->
                adapter.dataSet = adapter.dataSet + loadData(response, "B")
                adapter.notifyDataSetChanged()
            },
            Response.ErrorListener {
                TODO()
            }
        )
        queue.add(currenciesRatesRequestTableB)
    }

    private fun loadData(response: JSONArray?, tableId: String): Array<CurrencyDetails> {
        response?.let {
            val ratesToday = response.getJSONObject(1).getJSONArray("rates")
            val ratesYesterday = response.getJSONObject(0).getJSONArray("rates")
            val ratesNum = ratesToday.length()
            val tmpData = arrayOfNulls<CurrencyDetails>(ratesNum)

            for(i in 0 until ratesNum) {
                val currencyCode = ratesToday.getJSONObject(i).getString("code")
                val currencyRate = ratesToday.getJSONObject(i).getDouble("mid")

                var riseOrFall = 0
                var riseOrFallString = "-"
                val currencyCodeYesterday = ratesYesterday.getJSONObject(i).getString("code")
                if (currencyCode == currencyCodeYesterday) {
                    val currencyRateYesterday = ratesYesterday.getJSONObject(i).getDouble("mid")
                    if (currencyRateYesterday < currencyRate) {
                        riseOrFall = 1
                        riseOrFallString = getString(R.string.arrowUp)
                    } else if (currencyRateYesterday > currencyRate) {
                        riseOrFall = -1
                        riseOrFallString = getString(R.string.arrowDown)
                    }
                    else {
                        riseOrFallString = getString(R.string.arrowFlat)
                    }
                }

                var flagNumber = 17301571
                if ( ExtendedCurrency.getCurrencyByISO(currencyCode) != null) {
                    val currencyForFlag = ExtendedCurrency.getCurrencyByISO(currencyCode) //Get currency by its code
                    flagNumber =  currencyForFlag.flag // returns android resource id of flag or -1, if none is associated*/
                }
                val currencyObject = CurrencyDetails(currencyCode, currencyRate, tableId, riseOrFall, riseOrFallString, flagNumber)
                tmpData[i] = currencyObject
            }
            return tmpData as Array<CurrencyDetails>
        }
        return emptyArray()
    }
}