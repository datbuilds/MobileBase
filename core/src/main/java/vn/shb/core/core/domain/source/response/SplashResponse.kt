package vn.shb.core.core.domain.source.response

import vn.shb.core.core.delivery.BaseResponse
import vn.shb.data.entities.splash.LatestVersion
import vn.shb.data.entities.splash.MobileConfig

class VersionResponse : BaseResponse<LatestVersion>()

class MobileConfigResponse : BaseResponse<MobileConfig>()