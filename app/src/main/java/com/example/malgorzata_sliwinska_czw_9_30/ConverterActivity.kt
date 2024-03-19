package com.example.malgorzata_sliwinska_czw_9_30

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.mynameismidori.currencypicker.ExtendedCurrency
import org.json.JSONArray

class ConverterActivity : AppCompatActivity() {
    internal lateinit var resultTextView: TextView
    internal lateinit var convertButton: Button
    internal lateinit var currencyPicker: NumberPicker
    internal lateinit var nativeCurrencyTextEdit: EditText
    internal lateinit var foreignCurrencyTextEdit: EditText
    internal lateinit var directionButton: ToggleButton

    internal lateinit var currenciesList: Array<CurrencyDetails>
    internal lateinit var currentCurrency: CurrencyDetails

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_converter)

        resultTextView = findViewById(R.id.resultTextView)
        convertButton = findViewById(R.id.doConvertButton)
        currencyPicker = findViewById(R.id.currencyPicker)
        nativeCurrencyTextEdit = findViewById(R.id.nativeCurrencyTextEdit)
        foreignCurrencyTextEdit = findViewById(R.id.foreignCurrencyTextEdit)
        directionButton = findViewById(R.id.toggleButton)

        resultTextView.text = ""
        nativeCurrencyTextEdit.setText("0.0")
        foreignCurrencyTextEdit.setText("0.0")
        convertButton.isActivated = false

        convertButton.setOnClickListener { calculate() }

        makeRequest()
    }

    private fun setPicker() {
        convertButton.isActivated = true
        currentCurrency = currenciesList.first()
        currencyPicker.minValue = 0
        currencyPicker.maxValue = currenciesList.size - 1
        currencyPicker.displayedValues = currenciesList.map{ it.currencyCode }.toTypedArray()
        
        val valueListener = NumberPicker.OnValueChangeListener{ _, _, newVal -> 
                        currentCurrency = currenciesList[newVal]
        }
        currencyPicker.setOnValueChangedListener(valueListener)
    }

    private fun loadData(response: JSONArray?, tableId: String): Array<CurrencyDetails> {
        response?.let {
            val rates = response.getJSONObject(0).getJSONArray("rates")
            val ratesNum = rates.length()
            val tmpData = arrayOfNulls<CurrencyDetails>(ratesNum)

            for(i in 0 until ratesNum) {
                val currencyCode = rates.getJSONObject(i).getString("code")
                val currencyRate = rates.getJSONObject(i).getDouble("mid")
                var flagNumber = 17301571
                if ( ExtendedCurrency.getCurrencyByISO(currencyCode) != null) {
                    val currencyForFlag = ExtendedCurrency.getCurrencyByISO(currencyCode) //Get currency by its code
                    flagNumber =  currencyForFlag.flag // returns android resource id of flag or -1, if none is associated*/
                }
                val currencyObject = CurrencyDetails(currencyCode, currencyRate, tableId,0, "", flagNumber)
                tmpData[i] = currencyObject
            }
            return tmpData as Array<CurrencyDetails>
        }
        return emptyArray()
    }

    private fun calculate() {
        if(directionButton.isChecked) {
            // Native -> Foreign
            val rate = 1 / currentCurrency.currencyRate
            val result = nativeCurrencyTextEdit.text.toString().toDouble() * rate
            resultTextView.text = getString(R.string.resultText, nativeCurrencyTextEdit.text.toString().toDouble(),
                getString(R.string.nativeCurrencyTextView), result, currentCurrency.currencyCode)
        }
        else{
            // Foreign -> Native
            val result = foreignCurrencyTextEdit.text.toString().toDouble() * currentCurrency.currencyRate
            resultTextView.text = getString(R.string.resultText, foreignCurrencyTextEdit.text.toString().toDouble(),
                currentCurrency.currencyCode, result, getString(R.string.nativeCurrencyTextView))
        }
    }

    fun makeRequest() {
        val queue = DataHolderSingleton.queue

        val urlLast2TableA = "http://api.nbp.pl/api/exchangerates/tables/A/last/2?format=json"
        val urlLast2TableB = "http://api.nbp.pl/api/exchangerates/tables/B/last/2?format=json"

        val currenciesRatesRequestTableA = JsonArrayRequest(Request.Method.GET, urlLast2TableA, null,
            Response.Listener { response ->
                currenciesList = loadData(response, "A")
            },
            Response.ErrorListener {
                TODO()
            }
        )
        queue.add(currenciesRatesRequestTableA)

        val currenciesRatesRequestTableB = JsonArrayRequest(Request.Method.GET, urlLast2TableB, null,
            Response.Listener { response ->
                currenciesList += loadData(response, "B")
                setPicker()
            },
            Response.ErrorListener {
                TODO()
            }
        )
        queue.add(currenciesRatesRequestTableB)
    }

}