package domain.human

import domain.animal.Animal
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn

@Entity
class Person : Human {
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Int = 0

    override var name: String = ""

    @field:ElementCollection
    @field:CollectionTable(name = "person_pets", joinColumns = [JoinColumn(name = "person_id")])
    @field:Column(name = "pet_id")
    override val petIds: MutableSet<Int> = mutableSetOf()

    override fun touch(animal: Animal) {
        animal.touchedBy(this)
    }
}
