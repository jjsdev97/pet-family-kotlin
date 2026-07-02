package service

import domain.animal.Animal
import domain.animal.AnimalRepository
import domain.animal.cat.Cat
import domain.animal.cat.PersianCat
import domain.animal.cat.SiameseCat
import domain.animal.cat.StreetCat
import domain.animal.dog.Bulldog
import domain.animal.dog.Dog
import domain.animal.dog.Poodle
import domain.animal.dog.Retriever
import kotlin.random.Random

class AnimalService(private val animalRepository: AnimalRepository) {
    fun createRetriever(name: String = ""): Retriever {
        val retriever = Retriever()
        retriever.name = name
        return animalRepository.save(retriever)
    }

    fun createRandomDog(name: String = ""): Dog {
        val dog = when (Random.nextInt(3)) {
            0 -> Retriever()
            1 -> Poodle()
            else -> Bulldog()
        }

        dog.name = name
        return animalRepository.save(dog)
    }

    fun createCat(name: String = ""): Cat {
        val cat = StreetCat()
        cat.name = name
        return animalRepository.save(cat)
    }

    fun createRandomCat(name: String = ""): Cat {
        val cat = when (Random.nextInt(3)) {
            0 -> StreetCat()
            1 -> PersianCat()
            else -> SiameseCat()
        }

        cat.name = name
        return animalRepository.save(cat)
    }

    fun findById(id: Int): Animal? {
        return animalRepository.findById(id)
    }

    fun findAll(): List<Animal> {
        return animalRepository.findAll()
    }
}
