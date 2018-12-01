package repository

import domain.snippet.Snippet
import domain.snippet.SnippetId
import domain.snippet.SnippetRepository
import java.util.*

object InMemorySnippetRepository : SnippetRepository {

    private val snippets = Collections.synchronizedMap(mutableMapOf<SnippetId, Snippet>())

    override fun save(entity: Snippet) {
        snippets[entity.rawId] = entity
    }

    override fun remove(entity: Snippet) {
        snippets.remove(entity.rawId)
    }

}