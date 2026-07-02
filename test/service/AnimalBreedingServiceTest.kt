package service

import domain.animal.InMemoryAnimalRepository
import domain.animal.cat.StreetCat
import domain.animal.dog.Poodle
import domain.animal.dog.Retriever
import domain.animal.mix.MixedAnimal
import domain.human.InMemoryHumanRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AnimalBreedingServiceTest {
    private val animalRepository = InMemoryAnimalRepository()
    private val humanRepository = InMemoryHumanRepository()
    private val familyService = AnimalFamilyService(animalRepository)
    private val ownershipService = OwnershipService(animalRepository)
    private val breedingService = AnimalBreedingService(animalRepository, familyService, humanRepository)

    @Test
    fun sameConcreteParentsCreateSameConcreteChild() {
        val father = animalRepository.save(Retriever())
        val mother = animalRepository.save(Retriever())
        val owner = humanRepository.save(domain.human.Person())
        ownershipService.relate(mother, owner)

        val result = breedingService.createChild(father, mother, "puppy")

        val success = assertIs<BreedingResult.Success>(result)
        assertIs<Retriever>(success.child)
        assertEquals("puppy", success.child.name)
        assertEquals(mother.ownerId, success.child.ownerId)
        assertTrue(success.child.id in owner.petIds)
    }

    @Test
    fun differentDogBreedsCreateMixedDog() {
        val father = animalRepository.save(Retriever())
        val mother = animalRepository.save(Poodle())

        val result = breedingService.createChild(father, mother, "mix")

        val success = assertIs<BreedingResult.Success>(result)
        assertIs<MixedAnimal>(success.child)
        assertEquals(100, (success.child as MixedAnimal).breedPercentages.values.sum())
    }

    @Test
    fun differentFamiliesCannotBreed() {
        val father = animalRepository.save(Retriever())
        val mother = animalRepository.save(StreetCat())

        val result = breedingService.createChild(father, mother)

        assertEquals(BreedingResult.DifferentFamily, result)
    }

    @Test
    fun sameAnimalCannotBreedWithItself() {
        val animal = animalRepository.save(Retriever())

        val result = breedingService.createChild(animal, animal)

        assertEquals(BreedingResult.SameAnimal, result)
    }

    @Test
    fun unsavedParentsCannotBreed() {
        val result = breedingService.createChild(Retriever(), Retriever())

        assertEquals(BreedingResult.UnsavedParent, result)
    }
}
