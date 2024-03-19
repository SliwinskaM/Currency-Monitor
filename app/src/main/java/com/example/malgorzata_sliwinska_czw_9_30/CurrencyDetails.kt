package com.example.malgorzata_sliwinska_czw_9_30

data class CurrencyDetails(var currencyCode: String, var currencyRate: Double, var tableId: String, var riseOrFall: Int, val riseOrFallString: String, var flagImage: Int = 0) {
}
