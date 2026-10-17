package com.mobile.core.core.domain.source.response

import com.mobile.core.core.delivery.BaseResponse
import com.mobile.data.entities.splash.LatestVersion
import com.mobile.data.entities.splash.MobileConfig

class VersionResponse : BaseResponse<LatestVersion>()

class MobileConfigResponse : BaseResponse<MobileConfig>()