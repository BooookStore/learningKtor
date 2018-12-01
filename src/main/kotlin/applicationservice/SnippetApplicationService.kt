package applicationservice

import domain.common.DomainRegistry
import domain.snippet.Snippet
import domain.snippet.nextSnippetId

/** スニペット作成コマンド。 */
data class AddSnippetCommand(val user: String, val text: String)

/** スニペットを新規追加。 */
fun addSnippet(command: AddSnippetCommand) {
    val snippet = Snippet(nextSnippetId(), command.user, command.text)
    DomainRegistry.snippetRepository.save(snippet)
}

/** 全てのスニペットを取得する。 */
fun fetchSnippetAll() = DomainRegistry.snippetRepository.fetchAll()