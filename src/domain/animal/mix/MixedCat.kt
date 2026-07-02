package domain.animal.mix

import domain.animal.cat.Cat
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapKeyColumn

@Entity
class MixedCat(
    @field:ElementCollection
    @field:CollectionTable(name = "mixed_cat_breeds", joinColumns = [JoinColumn(name = "animal_id")])
    @field:MapKeyColumn(name = "breed")
    @field:Column(name = "percentage")
    override var breedPercentages: MutableMap<String, Int> = mutableMapOf()
) : Cat(), MixedAnimal {
    override fun bark() {
        println("mixed meow")
    }
}
