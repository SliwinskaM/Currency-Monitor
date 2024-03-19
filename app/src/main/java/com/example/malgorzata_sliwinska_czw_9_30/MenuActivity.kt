// Małgorzata Śliwińska
// Zrealizowałam wszystkie punkty.

package com.example.malgorzata_sliwinska_czw_9_30

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MenuActivity : AppCompatActivity() {
    internal lateinit var currencyRatesButton: Button
    internal lateinit var goldButton: Button
    internal lateinit var converterButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        DataHolderSingleton.prepare(applicationContext)

        currencyRatesButton = findViewById(R.id.currencyRatesButton)
        goldButton = findViewById(R.id.goldButton)
        converterButton = findViewById(R.id.converterButton)

        currencyRatesButton.setOnClickListener{
            val intent = Intent(this, CurrencyRatesActivity::class.java)
            startActivity(intent)
        }
        goldButton.setOnClickListener{
            val intent = Intent(this, GoldActivity::class.java)
            startActivity(intent)
        }
        converterButton.setOnClickListener{
            val intent = Intent(this, ConverterActivity::class.java)
            startActivity(intent)
        }
    }
}