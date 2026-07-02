package repository.jpa

import domain.animal.mix.MixedAnimal
import service.AnimalBreedingService
import service.AnimalFamilyService
import service.AnimalService
import service.BreedingResult
import service.HumanService
import service.OwnershipResult
import service.OwnershipService
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class JpaRepositoryTest {
    private lateinit var entityManagerFactory: EntityManagerFactory
    private lateinit var entityManager: EntityManager
    private lateinit var humanRepository: JpaHumanRepository
    private lateinit var animalRepository: JpaAnimalRepository

    @BeforeTest
    fun setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("kotlin-prac-test")
        entityManager = entityManagerFactory.createEntityManager()
        humanRepository = JpaHumanRepository(entityManager)
        animalRepository = JpaAnimalRepository(entityManager)
    }

    @AfterTest
    fun tearDown() {
        if (entityManager.transaction.isActive) {
            entityManager.transaction.rollback()
        }

        entityManager.close()
        entityManagerFactory.close()
    }

    @Test
    fun jpaRepositoriesPersistAndFindHumanAndAnimal() {
        runInTransaction {
            val humanService = HumanService(humanRepository)
            val animalService = AnimalService(animalRepository)
            val ownershipService = OwnershipService(animalRepository)

            val human = humanService.createPerson("민수")
            val animal = animalService.createRandomDog("초코")

            assertNotEquals(0, human.id)
            assertNotEquals(0, animal.id)
            assertEquals(OwnershipResult.Success, ownershipService.relate(animal, human))
        }

        entityManager.clear()

        runInTransaction {
            val humans = humanRepository.findAll()
            val animals = animalRepository.findAll()

            assertEquals(1, humans.size)
            assertEquals(1, animals.size)
            assertEquals("민수", humans.first().name)
            assertEquals("초코", animals.first().name)
        }
    }

    @Test
    fun jpaPersistsMixedChildBreedPercentagesAndFamilyLinks() {
        runInTransaction {
            val humanService = HumanService(humanRepository)
            val animalService = AnimalService(animalRepository)
            val familyService = AnimalFamilyService(animalRepository)
            val ownershipService = OwnershipService(animalRepository)
            val breedingService = AnimalBreedingService(animalRepository, familyService, humanRepository)

            val owner = humanService.createPerson("민수")
            val father = animalService.createRandomDog("아빠")
            val mother = animalService.createRandomDog("엄마")

            ownershipService.relate(father, owner)
            ownershipService.relate(mother, owner)

            val result = breedingService.createChild(father, mother, "자식")
            val success = assertIs<BreedingResult.Success>(result)

            assertNotNull(success.child.fatherId)
            assertNotNull(success.child.motherId)
            assertTrue(success.child.id in owner.petIds)
        }

        entityManager.clear()

        runInTransaction {
            val animals = animalRepository.findAll()
            assertTrue(animals.size >= 3)

            val child = animals.first { it.name == "자식" }
            assertNotNull(child.fatherId)
            assertNotNull(child.motherId)

            if (child is MixedAnimal) {
                assertEquals(100, child.breedPercentages.values.sum())
            }
        }
    }

    private fun runInTransaction(block: () -> Unit) {
        val transaction = entityManager.transaction
        transaction.begin()

        try {
            block()
            transaction.commit()
        } catch (error: RuntimeException) {
            if (transaction.isActive) {
                transaction.rollback()
            }

            throw error
        }
    }
}
