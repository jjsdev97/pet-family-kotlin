import repository.jpa.JpaAnimalRepository
import repository.jpa.JpaHumanRepository
import repository.jpa.JpaUtil
import service.AnimalBreedingService
import service.AnimalFamilyService
import service.AnimalService
import service.HumanService
import service.OwnershipService

fun main() {
    val entityManager = JpaUtil.entityManagerFactory.createEntityManager()
    val transaction = entityManager.transaction

    transaction.begin()

    try {
        val humanRepository = JpaHumanRepository(entityManager)
        val animalRepository = JpaAnimalRepository(entityManager)
        val humanService = HumanService(humanRepository)
        val animalService = AnimalService(animalRepository)
        val ownershipService = OwnershipService(animalRepository)
        val animalFamilyService = AnimalFamilyService(animalRepository)
        val animalBreedingService = AnimalBreedingService(animalRepository, animalFamilyService, humanRepository)

        val minsu = humanService.createPerson("민수")
        val fatherDog = animalService.createRandomDog("아빠개")
        val motherDog = animalService.createRandomDog("엄마개")

        ownershipService.relate(fatherDog, minsu)
        ownershipService.relate(motherDog, minsu)

        animalBreedingService.createChild(fatherDog, motherDog, "강아지")

        transaction.commit()
    } catch (error: RuntimeException) {
        transaction.rollback()
        throw error
    } finally {
        entityManager.close()
        JpaUtil.entityManagerFactory.close()
    }
}
