package com.mobile.base.data.entities.home

import java.io.Serializable

data class BannerItem(
    val id: String = "",
    val name: String = "",
    val nameResId: Int = 0,
    val bannerResId: Int = 0,
) : Serializable