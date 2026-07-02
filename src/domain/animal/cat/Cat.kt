package domain.animal.cat

import domain.animal.BaseAnimal
import domain.human.Human
import jakarta.persistence.Entity

@Entity
abstract class Cat : BaseAnimal() {
    override fun touchedBy(human: Human) {
        if (ownerId == human.id) {
            bark()
        }
    }
}

@Entity
class StreetCat : Cat() {
    override fun bark() {
        println("meow")
    }
}
