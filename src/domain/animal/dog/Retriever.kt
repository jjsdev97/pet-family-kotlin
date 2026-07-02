package domain.animal.dog

import jakarta.persistence.Entity

@Entity
class Retriever : Dog() {
    override fun bark() {
        println("woof")
    }
}
