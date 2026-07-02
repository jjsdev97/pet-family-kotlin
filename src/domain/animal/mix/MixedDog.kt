package domain.animal.mix

import domain.animal.dog.Dog
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapKeyColumn

@Entity
class MixedDog(
    @field:ElementCollection
    @field:CollectionTable(name = "mixed_dog_breeds", joinColumns = [JoinColumn(name = "animal_id")])
    @field:MapKeyColumn(name = "breed")
    @field:Column(name = "percentage")
    override var breedPercentages: MutableMap<String, Int> = mutableMapOf()
) : Dog(), MixedAnimal {
    override fun bark() {
        println("mixed woof")
    }
}
