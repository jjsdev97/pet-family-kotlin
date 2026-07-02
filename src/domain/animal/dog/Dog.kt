package domain.animal.dog

import domain.animal.BaseAnimal
import domain.human.Human
import jakarta.persistence.Entity

@Entity
abstract class Dog : BaseAnimal() {
    override fun touchedBy(human: Human) {
        if (ownerId != human.id) {
            bark()
        }
    }

    fun wagTail() {
        println("wag")
    }
}
