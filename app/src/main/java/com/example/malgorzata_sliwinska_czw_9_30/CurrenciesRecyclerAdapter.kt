package com.example.malgorzata_sliwinska_czw_9_30

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CurrenciesRecyclerAdapter(var dataSet: Array<CurrencyDetails>, val context: Context) : RecyclerView.Adapter<CurrenciesRecyclerAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val currencyCodeTextView: TextView
        val currencyRateTextView: TextView
        val flagImageView: ImageView
        val riseOrFallTextView: TextView
        init {
            // Define click listener for the ViewHolder's View.
            currencyCodeTextView = view.findViewById(R.id.currencyCodeTextView)
            currencyRateTextView = view.findViewById(R.id.currencyRateTextView)
            flagImageView = view.findViewById(R.id.flagImage)
            riseOrFallTextView = view.findViewById((R.id.riseOfFallTextField))
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.currency_recycler_row, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val currency = dataSet[position]
        viewHolder.currencyCodeTextView.text = currency.currencyCode
        viewHolder.currencyRateTextView.text = currency.currencyRate.toString()
        viewHolder.flagImageView.setImageResource(currency.flagImage)
        viewHolder.riseOrFallTextView.text = currency.riseOrFallString
        when (currency.riseOrFall) {
            1 -> viewHolder.riseOrFallTextView.setTextColor(Color.GREEN)
            -1 -> viewHolder.riseOrFallTextView.setTextColor(Color.RED)
            0 -> viewHolder.riseOrFallTextView.setTextColor(Color.GRAY)
        }

        viewHolder.itemView.setOnClickListener { showDetails(currency.currencyCode, currency.tableId) }

    }

    private fun showDetails(currencyCode: String, tableId: String) {
        val intent = Intent(context, HistoryActivity::class.java).apply {
            putExtra("currencyCode", currencyCode)
            putExtra("tableId", tableId)
        }
        context.startActivity(intent)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
