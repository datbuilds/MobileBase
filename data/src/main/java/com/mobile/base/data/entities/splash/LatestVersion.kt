package com.mobile.base.data.entities.splash

data class LatestVersion(
    val latest_version: String? = null,
    val version_information: VersionInformation? = null
)

data class VersionInformation(
    val app_type: String? = null,
    val content: String? = null,
    val force: Boolean? = null,
    val is_enable: Boolean? = null,
    val platform: String? = null,
    val popup: Boolean? = null,
    val version: String? = null
)