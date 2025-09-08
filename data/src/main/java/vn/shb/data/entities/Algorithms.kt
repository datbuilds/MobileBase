package vn.shb.data.entities

/**
 * Thuật toán KMP (Knuth–Morris–Pratt) để tăng hiệu quả khi tìm kiếm chuỗi con
 */
fun kmpSearch(text: String, pattern: String): Boolean {
    if (pattern.isEmpty()) return true
    val lps = computeLPSArray(pattern)
    var i = 0 // chỉ số cho text
    var j = 0 // chỉ số cho pattern

    while (i < text.length) {
        if (pattern[j] == text[i]) {
            i++
            j++
        }

        if (j == pattern.length) {
            return true // tìm thấy
        } else if (i < text.length && pattern[j] != text[i]) {
            if (j != 0) {
                j = lps[j - 1]
            } else {
                i++
            }
        }
    }

    return false
}

fun computeLPSArray(pattern: String): IntArray {
    val lps = IntArray(pattern.length)
    var length = 0
    var i = 1

    while (i < pattern.length) {
        if (pattern[i] == pattern[length]) {
            length++
            lps[i] = length
            i++
        } else {
            if (length != 0) {
                length = lps[length - 1]
            } else {
                lps[i] = 0
                i++
            }
        }
    }

    return lps
}