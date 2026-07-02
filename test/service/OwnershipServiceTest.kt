package service

import domain.animal.InMemoryAnimalRepository
import domain.animal.dog.Retriever
import domain.human.InMemoryHumanRepository
import domain.human.Person
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OwnershipServiceTest {
    private val animalRepository = InMemoryAnimalRepository()
    private val humanRepository = InMemoryHumanRepository()
    private val ownershipService = OwnershipService(animalRepository)

    @Test
    fun transferMovesPetIdAndOwnerId() {
        val animal = animalRepository.save(Retriever())
        val from = humanRepository.save(Person())
        val to = humanRepository.save(Person())

        ownershipService.relate(animal, from)

        assertEquals(OwnershipResult.Success, ownershipService.transfer(animal.id, from, to))
        assertEquals(to.id, animal.ownerId)
        assertFalse(animal.id in from.petIds)
        assertTrue(animal.id in to.petIds)
    }

    @Test
    fun relateFailsWhenAnimalAlreadyHasDifferentOwner() {
        val animal = animalRepository.save(Retriever())
        val firstOwner = humanRepository.save(Person())
        val secondOwner = humanRepository.save(Person())

        ownershipService.relate(animal, firstOwner)

        assertEquals(OwnershipResult.AlreadyOwnedByOther, ownershipService.relate(animal, secondOwner))
    }

    @Test
    fun unrelateClearsOwnerIdAndPetId() {
        val animal = animalRepository.save(Retriever())
        val owner = humanRepository.save(Person())

        ownershipService.relate(animal, owner)

        assertEquals(OwnershipResult.Success, ownershipService.unrelate(animal, owner))
        assertEquals(null, animal.ownerId)
        assertFalse(animal.id in owner.petIds)
    }

    @Test
    fun transferFailsWhenAnimalDoesNotExist() {
        val from = humanRepository.save(Person())
        val to = humanRepository.save(Person())

        assertEquals(OwnershipResult.AnimalNotFound, ownershipService.transfer(999, from, to))
    }

    @Test
    fun transferFailsWhenFromIsNotOwner() {
        val animal = animalRepository.save(Retriever())
        val owner = humanRepository.save(Person())
        val notOwner = humanRepository.save(Person())
        val to = humanRepository.save(Person())

        ownershipService.relate(animal, owner)

        assertEquals(OwnershipResult.NotOwner, ownershipService.transfer(animal.id, notOwner, to))
    }

    @Test
    fun relateFailsForUnsavedAnimal() {
        val owner = humanRepository.save(Person())

        assertEquals(OwnershipResult.UnsavedAnimal, ownershipService.relate(Retriever(), owner))
    }

    @Test
    fun relateFailsForUnsavedHuman() {
        val animal = animalRepository.save(Retriever())

        assertEquals(OwnershipResult.UnsavedHuman, ownershipService.relate(animal, Person()))
    }
}
