package com.example.musicplayer.domain.model


data class SmartAdResponse(
    val status: String,
    val message: String,
    val data: AdData
)

data class AdData(
    val ads: AdsDetails
)

data class AdsDetails(
    val total_clicks: Int,
    val random_ad: RandomAd
)

data class RandomAd(
    val id: Int,
    val name: String,
    val image: String,
    val adUrl: String,
    val imageAlt: String
)