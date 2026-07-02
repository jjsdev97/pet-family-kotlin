package domain.animal

interface AnimalRepository {
    fun <T : Animal> save(animal: T): T
    fun findById(id: Int): Animal?
    fun findAll(): List<Animal>
}

class InMemoryAnimalRepository : AnimalRepository {
    private val animals: MutableMap<Int, Animal> = mutableMapOf()
    private var nextId: Int = 1

    override fun <T : Animal> save(animal: T): T {
        if (animal.id == 0) {
            animal.id = nextId
            nextId += 1
        }

        animals[animal.id] = animal
        return animal
    }

    override fun findById(id: Int): Animal? {
        return animals[id]
    }

    override fun findAll(): List<Animal> {
        return animals.values.toList()
    }
}
