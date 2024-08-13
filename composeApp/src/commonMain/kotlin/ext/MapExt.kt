package ext

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend fun <A, B> List<A>.asyncMap(transform: suspend (A) -> B): List<B> = coroutineScope {
    map { async { transform(it) } }.awaitAll()
}
