package domain.animal.cat

import jakarta.persistence.Entity

@Entity
class SiameseCat : Cat() {
    override fun bark() {
        println("nyaa")
    }
}
