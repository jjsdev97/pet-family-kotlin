package domain.animal.cat

import jakarta.persistence.Entity

@Entity
class PersianCat : Cat() {
    override fun bark() {
        println("purr meow")
    }
}
