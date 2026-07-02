package domain.animal

import domain.human.Human

interface Animal {
    var id: Int
    var name: String
    var ownerId: Int?
    var fatherId: Int?
    var motherId: Int?
    val childIds: MutableSet<Int>
    fun bark()
    fun touchedBy(human: Human)
}
