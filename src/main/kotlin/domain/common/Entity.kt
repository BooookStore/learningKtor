package domain.common

/**
 * エンティティのレイヤースーパータイプ。
 *
 * @param rawId ID値
 * @param E ID値の型
 */
abstract class Entity<E>(val rawId: E) {

    fun isIdentifiedBy(entity: Entity<E>): Boolean = rawId == entity.rawId

}