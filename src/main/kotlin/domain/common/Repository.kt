package domain.common

interface Repository<I, E : Entity<I>> {

    fun save(entity: E)

    fun remove(entity: E)

    fun saveAll(entities: Collection<E>) = entities.forEach(::save)

    fun removeAll(entities: Collection<E>) = entities.forEach(::remove)

}