package domain.animal.dog

import jakarta.persistence.Entity

@Entity
class Poodle : Dog() {
    override fun bark() {
        println("yip")
    }
}
