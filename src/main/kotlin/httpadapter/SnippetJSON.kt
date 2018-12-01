package httpadapter

import domain.snippet.Snippet

fun Collection<Snippet>.toJSONFormat(): Any {
    return map(Snippet::toJSONFormat)
}

@Suppress("unused")
fun Snippet.toJSONFormat(): Any {
    return object {
        val id = rawId.rawId
        val content = text
        val userName = user
    }
}

