package service

import domain.animal.Animal
import domain.animal.AnimalRepository
import domain.human.Human

class OwnershipService(private val animalRepository: AnimalRepository) {
    fun relate(pet: Animal, owner: Human): OwnershipResult {
        if (pet.id == 0) {
            return OwnershipResult.UnsavedAnimal
        }

        if (owner.id == 0) {
            return OwnershipResult.UnsavedHuman
        }

        if (pet.ownerId != null && pet.ownerId != owner.id) {
            return OwnershipResult.AlreadyOwnedByOther
        }

        pet.ownerId = owner.id
        owner.petIds.add(pet.id)

        return OwnershipResult.Success
    }

    fun unrelate(pet: Animal, owner: Human): OwnershipResult {
        if (pet.id == 0) {
            return OwnershipResult.UnsavedAnimal
        }

        if (owner.id == 0) {
            return OwnershipResult.UnsavedHuman
        }

        if (pet.ownerId != owner.id) {
            return OwnershipResult.NotOwner
        }

        pet.ownerId = null
        owner.petIds.remove(pet.id)

        return OwnershipResult.Success
    }

    fun transfer(petId: Int, from: Human, to: Human): OwnershipResult {
        if (from.id == 0 || to.id == 0) {
            return OwnershipResult.UnsavedHuman
        }

        val pet = animalRepository.findById(petId) ?: return OwnershipResult.AnimalNotFound

        if (pet.ownerId != from.id) {
            return OwnershipResult.NotOwner
        }

        from.petIds.remove(pet.id)
        to.petIds.add(pet.id)
        pet.ownerId = to.id

        return OwnershipResult.Success
    }
}
