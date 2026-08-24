package com.example.utilityledger.data

object Category {
    const val RENT = "Rent"
    const val ELECTRICITY = "Electricity"
    const val MOBILE = "Mobile"
    const val RECHARGE = "Recharge"
    const val OTHER = "Other"
    const val ALL = "All"

    val addable = listOf(RENT, ELECTRICITY, MOBILE, RECHARGE, OTHER)
    val tabs = listOf(ALL, RENT, ELECTRICITY, MOBILE, RECHARGE, OTHER)
}
