package service

import domain.human.Human
import domain.human.HumanRepository
import domain.human.Person

class HumanService(private val humanRepository: HumanRepository) {
    fun createPerson(name: String = ""): Person {
        val person = Person()
        person.name = name
        return humanRepository.save(person)
    }

    fun findById(id: Int): Human? {
        return humanRepository.findById(id)
    }

    fun findAll(): List<Human> {
        return humanRepository.findAll()
    }
}
