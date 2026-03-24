package vn.shb.cam.utils.extensions

fun ByteArray.toInt(): Int {
    var result = 0
    for (i in this.indices) {
        result = result or (this[i].toInt() and 0xFF shl (8 * i))
    }
    return result
}

fun ByteArray.byteArrayToString() = String(this, Charsets.UTF_8)

