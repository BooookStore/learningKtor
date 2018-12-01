package domain.snippet

import domain.common.Entity

/**
 * スニペット。
 */
class Snippet(snippetId: SnippetId, val user: String, val text: String): Entity<SnippetId>(snippetId)
