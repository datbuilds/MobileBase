package vn.shb.core.core.domain.source.api

object ENDPOINT {
    const val APP_VERSION = "app-versions/latest"

    // Auth
    const val AUTH_REFRESH_TOKEN = "identity-service/api/v1/auth/refresh-token"
    const val AUTH_LOGIN = "identity-service/api/v1/auth/login"
    const val AUTH_LOGOUT = "identity-service/api/v1/auth/logout"
    const val USER_INFO = "account-service/api/v1/users/info"
    const val ACCOUNTS_INFO = "account-service/api/v1/accounts"
    const val ACCOUNT_DETAILS = "account-service/api/v1/accounts/details"
    const val TRANSACTION = "fundtransfer-service/api/v1/transactions"
}