package vn.shb.core.core.local

object LocalData {
    private val data = mutableMapOf<String, Any?>()

    fun put(key: String, value: Any?) {
        if (data.containsKey(key)) {
            data.remove(key)
        }
        data[key] = value
    }

    fun get(key: String, defaultValue: Any? = null): Any? {
        return data[key] ?: defaultValue
    }

    fun remove(key: String) {
        data.remove(key)
    }

    fun clear() {
        data.clear()
    }
}

enum class LocalConst {

}
