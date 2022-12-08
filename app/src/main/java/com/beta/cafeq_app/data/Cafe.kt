package com.beta.cafeq_app.data

data class Cafe(val img: String = "",
                val name: String = "-",
                val address: String = "-",
                var chair: Int = 0,
                val distance: String = "-",
                val rating: String = "-")