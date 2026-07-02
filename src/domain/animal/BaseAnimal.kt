package domain.animal

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.JoinColumn

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "animal_type")
abstract class BaseAnimal : Animal {
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Int = 0

    override var name: String = ""
    override var ownerId: Int? = null
    override var fatherId: Int? = null
    override var motherId: Int? = null

    @field:ElementCollection
    @field:CollectionTable(name = "animal_children", joinColumns = [JoinColumn(name = "animal_id")])
    @field:Column(name = "child_id")
    override val childIds: MutableSet<Int> = mutableSetOf()
}
