package domain.human

interface HumanRepository {
    fun <T : Human> save(human: T): T
    fun findById(id: Int): Human?
    fun findAll(): List<Human>
}

class InMemoryHumanRepository : HumanRepository {
    private val humans: MutableMap<Int, Human> = mutableMapOf()
    private var nextId: Int = 1

    override fun <T : Human> save(human: T): T {
        if (human.id == 0) {
            human.id = nextId
            nextId += 1
        }

        humans[human.id] = human
        return human
    }

    override fun findById(id: Int): Human? {
        return humans[id]
    }

    override fun findAll(): List<Human> {
        return humans.values.toList()
    }
}
