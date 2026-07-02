package domain.animal.dog

import jakarta.persistence.Entity

@Entity
class Bulldog : Dog() {
    override fun bark() {
        println("grrr")
    }
}
