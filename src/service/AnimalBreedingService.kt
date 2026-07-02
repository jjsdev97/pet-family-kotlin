package service

import domain.animal.Animal
import domain.animal.AnimalRepository
import domain.animal.cat.Cat
import domain.animal.dog.Dog
import domain.animal.mix.MixedAnimal
import domain.animal.mix.MixedCat
import domain.animal.mix.MixedDog
import domain.human.HumanRepository
import java.lang.reflect.Modifier
import kotlin.random.Random

class AnimalBreedingService(
    private val animalRepository: AnimalRepository,
    private val animalFamilyService: AnimalFamilyService,
    private val humanRepository: HumanRepository? = null
) {
    fun createChild(father: Animal, mother: Animal, name: String = ""): BreedingResult {
        if (father.id == 0 || mother.id == 0) {
            return BreedingResult.UnsavedParent
        }

        if (father.id == mother.id) {
            return BreedingResult.SameAnimal
        }

        if (!hasSameAbstractParent(father, mother)) {
            return BreedingResult.DifferentFamily
        }

        val child = createChildByParents(father, mother) ?: return BreedingResult.UnsupportedFamily
        child.name = name
        child.ownerId = mother.ownerId
        animalRepository.save(child)
        if (!animalFamilyService.relateFatherChild(father, child)) {
            return BreedingResult.UnsupportedFamily
        }

        if (!animalFamilyService.relateMotherChild(mother, child)) {
            return BreedingResult.UnsupportedFamily
        }

        addChildToMotherOwner(child, mother)

        return BreedingResult.Success(child)
    }

    private fun addChildToMotherOwner(child: Animal, mother: Animal) {
        val ownerId = mother.ownerId ?: return
        humanRepository?.findById(ownerId)?.petIds?.add(child.id)
    }

    private fun createChildByParents(father: Animal, mother: Animal): Animal? {
        if (father.javaClass == mother.javaClass) {
            return createSameClassChild(father, mother)
        }

        val breedPercentages = createMixedBreedPercentages(father, mother)

        return when (father.javaClass.superclass) {
            Dog::class.java -> MixedDog(breedPercentages.toMutableMap())
            Cat::class.java -> MixedCat(breedPercentages.toMutableMap())
            else -> null
        }
    }

    private fun createSameClassChild(father: Animal, mother: Animal): Animal? {
        if (father is MixedAnimal) {
            val breedPercentages = createMixedBreedPercentages(father, mother)

            return when (father.javaClass.superclass) {
                Dog::class.java -> MixedDog(breedPercentages.toMutableMap())
                Cat::class.java -> MixedCat(breedPercentages.toMutableMap())
                else -> null
            }
        }

        return try {
            val constructor = father.javaClass.getDeclaredConstructor()
            constructor.newInstance() as? Animal
        } catch (error: ReflectiveOperationException) {
            null
        }
    }

    private fun createMixedBreedPercentages(father: Animal, mother: Animal): Map<String, Int> {
        val fatherRate = Random.nextInt(1, 100)
        val motherRate = 100 - fatherRate
        val breedPercentages = mutableMapOf<String, Int>()

        addWeightedPercentages(breedPercentages, extractBreedPercentages(father), fatherRate)
        addWeightedPercentages(breedPercentages, extractBreedPercentages(mother), motherRate)

        return normalizePercentages(breedPercentages)
    }

    private fun extractBreedPercentages(animal: Animal): Map<String, Int> {
        if (animal is MixedAnimal) {
            return animal.breedPercentages
        }

        return mapOf(animal.javaClass.simpleName to 100)
    }

    private fun addWeightedPercentages(
        target: MutableMap<String, Int>,
        source: Map<String, Int>,
        weight: Int
    ) {
        source.forEach { (breed, percentage) ->
            val weightedPercentage = percentage * weight
            target[breed] = (target[breed] ?: 0) + weightedPercentage
        }
    }

    private fun normalizePercentages(rawPercentages: Map<String, Int>): Map<String, Int> {
        val normalizedPercentages = rawPercentages.mapValues { it.value / 100 }.toMutableMap()
        val remainder = 100 - normalizedPercentages.values.sum()
        val largestBreed = rawPercentages.maxByOrNull { it.value }?.key

        if (largestBreed != null) {
            normalizedPercentages[largestBreed] = (normalizedPercentages[largestBreed] ?: 0) + remainder
        }

        return normalizedPercentages
    }

    private fun hasSameAbstractParent(father: Animal, mother: Animal): Boolean {
        val fatherParentClass = father.javaClass.superclass
        val motherParentClass = mother.javaClass.superclass

        return fatherParentClass == motherParentClass && Modifier.isAbstract(fatherParentClass.modifiers)
    }
}
