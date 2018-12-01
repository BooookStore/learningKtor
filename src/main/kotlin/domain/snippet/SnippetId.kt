package domain.snippet

import java.util.*

/**
 * スニペットID。
 */
data class SnippetId(val rawId: String)

/**
 * スニペットIDを採番。
 */
fun nextSnippetId() = SnippetId(UUID.randomUUID().toString())

