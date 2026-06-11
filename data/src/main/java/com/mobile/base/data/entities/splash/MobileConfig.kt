package com.mobile.base.data.entities.splash


data class MobileConfig(
    val changePassConfig: ChangePassConfig,
    val exPiryTime: Int,
    val timeSkip: Int
)

data class ChangePassConfig(
    val maxLength: Int,
    val minLength: Int
)