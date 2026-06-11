package com.mobile.base.core.core.domain.source.response

import com.mobile.base.core.core.delivery.BaseResponse
import com.mobile.base.data.entities.splash.LatestVersion
import com.mobile.base.data.entities.splash.MobileConfig

class VersionResponse : BaseResponse<LatestVersion>()

class MobileConfigResponse : BaseResponse<MobileConfig>()