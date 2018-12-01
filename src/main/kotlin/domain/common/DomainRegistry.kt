package domain.common

import domain.snippet.SnippetRepository
import repository.InMemorySnippetRepository

object DomainRegistry {

    val snippetRepository: SnippetRepository
        get() = InMemorySnippetRepository

}