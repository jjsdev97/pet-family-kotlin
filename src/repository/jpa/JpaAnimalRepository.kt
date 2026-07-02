package repository.jpa

import domain.animal.Animal
import domain.animal.AnimalRepository
import domain.animal.BaseAnimal
import jakarta.persistence.EntityManager

class JpaAnimalRepository(private val entityManager: EntityManager) : AnimalRepository {
    override fun <T : Animal> save(animal: T): T {
        if (animal.id == 0) {
            entityManager.persist(animal)
            return animal
        }

        return entityManager.merge(animal)
    }

    override fun findById(id: Int): Animal? {
        return entityManager.find(BaseAnimal::class.java, id)
    }

    override fun findAll(): List<Animal> {
        return entityManager
            .createQuery("select animal from BaseAnimal animal", BaseAnimal::class.java)
            .resultList
    }
}
