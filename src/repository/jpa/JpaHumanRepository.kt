package repository.jpa

import domain.human.Human
import domain.human.HumanRepository
import domain.human.Person
import jakarta.persistence.EntityManager

class JpaHumanRepository(private val entityManager: EntityManager) : HumanRepository {
    override fun <T : Human> save(human: T): T {
        if (human.id == 0) {
            entityManager.persist(human)
            return human
        }

        return entityManager.merge(human)
    }

    override fun findById(id: Int): Human? {
        return entityManager.find(Person::class.java, id)
    }

    override fun findAll(): List<Human> {
        return entityManager
            .createQuery("select person from Person person", Person::class.java)
            .resultList
    }
}
