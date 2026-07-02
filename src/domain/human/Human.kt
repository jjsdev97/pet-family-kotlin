package domain.human

import domain.animal.Animal

interface Human {
    var id: Int
    var name: String
    val petIds: MutableSet<Int>
    fun touch(animal: Animal)
}
