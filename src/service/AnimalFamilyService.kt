package service

import domain.animal.Animal
import domain.animal.AnimalRepository

class AnimalFamilyService(private val animalRepository: AnimalRepository) {
    fun relateFatherChild(father: Animal, child: Animal): Boolean {
        if (father.id == 0 || child.id == 0) {
            return false
        }

        if (father.id == child.id) {
            return false
        }

        if (child.fatherId != null && child.fatherId != father.id) {
            return false
        }

        child.fatherId = father.id
        father.childIds.add(child.id)

        return true
    }

    fun relateMotherChild(mother: Animal, child: Animal): Boolean {
        if (mother.id == 0 || child.id == 0) {
            return false
        }

        if (mother.id == child.id) {
            return false
        }

        if (child.motherId != null && child.motherId != mother.id) {
            return false
        }

        child.motherId = mother.id
        mother.childIds.add(child.id)

        return true
    }

    fun unrelateFatherChild(father: Animal, child: Animal): Boolean {
        if (father.id == 0 || child.id == 0) {
            return false
        }

        if (child.fatherId != father.id) {
            return false
        }

        child.fatherId = null
        father.childIds.remove(child.id)

        return true
    }

    fun unrelateMotherChild(mother: Animal, child: Animal): Boolean {
        if (mother.id == 0 || child.id == 0) {
            return false
        }

        if (child.motherId != mother.id) {
            return false
        }

        child.motherId = null
        mother.childIds.remove(child.id)

        return true
    }

    fun findFather(child: Animal): Animal? {
        val fatherId = child.fatherId ?: return null
        return animalRepository.findById(fatherId)
    }

    fun findMother(child: Animal): Animal? {
        val motherId = child.motherId ?: return null
        return animalRepository.findById(motherId)
    }

    fun findChildren(parent: Animal): List<Animal> {
        return parent.childIds.mapNotNull { animalRepository.findById(it) }
    }
}
