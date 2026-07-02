package ui

import domain.animal.Animal
import domain.animal.AnimalRepository
import domain.animal.InMemoryAnimalRepository
import domain.animal.mix.MixedAnimal
import domain.human.Human
import domain.human.HumanRepository
import domain.human.InMemoryHumanRepository
import service.AnimalBreedingService
import service.AnimalFamilyService
import service.AnimalService
import service.BreedingResult
import service.HumanService
import service.OwnershipResult
import service.OwnershipService

class ConsoleApp(
    private val humanRepository: HumanRepository = InMemoryHumanRepository(),
    private val animalRepository: AnimalRepository = InMemoryAnimalRepository(),
    private val runAction: ((() -> Unit) -> Unit)? = null
) {
    private val humanService = HumanService(humanRepository)
    private val animalService = AnimalService(animalRepository)
    private val ownershipService = OwnershipService(animalRepository)
    private val animalFamilyService = AnimalFamilyService(animalRepository)
    private val animalBreedingService = AnimalBreedingService(animalRepository, animalFamilyService, humanRepository)

    fun run() {
        while (true) {
            printMenu()

            when (readlnOrNull()?.trim()) {
                "1" -> executeAction { createPerson() }
                "2" -> executeAction { adoptRandomDog() }
                "3" -> executeAction { adoptRandomCat() }
                "4" -> executeAction { touchAnimal() }
                "5" -> executeAction { transferAnimal() }
                "6" -> executeAction { breedAnimal() }
                "7" -> executeAction { listHumans() }
                "8" -> executeAction { listAnimals() }
                "0" -> return
                else -> println("알 수 없는 메뉴입니다.")
            }
        }
    }

    private fun executeAction(action: () -> Unit) {
        if (runAction == null) {
            action()
            return
        }

        runAction.invoke(action)
    }

    private fun printMenu() {
        println()
        println("==== 동물 관리 CLI ====")
        println("1. 사람 생성")
        println("2. 랜덤 개 입양")
        println("3. 랜덤 고양이 입양")
        println("4. 동물 만지기")
        println("5. 동물 양도")
        println("6. 부/모로 자식 생성")
        println("7. 사람 목록 보기")
        println("8. 동물 목록 보기")
        println("0. 종료")
        print("선택: ")
    }

    private fun createPerson() {
        val name = readText("사람 이름: ")
        val person = humanService.createPerson(name)
        println("사람이 생성됐습니다. ${describeHuman(person)}")
    }

    private fun adoptRandomDog() {
        val owner = readHuman("입양할 사람 id: ") ?: return
        val name = readText("동물 이름: ")
        val dog = animalService.createRandomDog(name)
        val result = ownershipService.relate(dog, owner)
        printOwnershipResult(result, successMessage = "랜덤 개를 입양했습니다. ${describeAnimal(dog)}")
    }

    private fun adoptRandomCat() {
        val owner = readHuman("입양할 사람 id: ") ?: return
        val name = readText("동물 이름: ")
        val cat = animalService.createRandomCat(name)
        val result = ownershipService.relate(cat, owner)
        printOwnershipResult(result, successMessage = "랜덤 고양이를 입양했습니다. ${describeAnimal(cat)}")
    }

    private fun touchAnimal() {
        val human = readHuman("만질 사람 id: ") ?: return
        val animal = readAnimal("만질 동물 id: ") ?: return
        human.touch(animal)
    }

    private fun transferAnimal() {
        val animal = readAnimal("양도할 동물 id: ") ?: return
        val from = readHuman("현재 주인 id: ") ?: return
        val to = readHuman("새 주인 id: ") ?: return

        printOwnershipResult(ownershipService.transfer(animal.id, from, to), successMessage = "양도했습니다.")
    }

    private fun breedAnimal() {
        val father = readAnimal("아빠 동물 id: ") ?: return
        val mother = readAnimal("엄마 동물 id: ") ?: return
        val name = readText("자식 이름: ")
        val result = animalBreedingService.createChild(father, mother, name)

        when (result) {
            is BreedingResult.Success -> println("자식이 태어났습니다. ${describeAnimal(result.child)}")
            BreedingResult.SameAnimal -> println("자식을 생성하지 못했습니다. 부/모가 같은 동물입니다.")
            BreedingResult.DifferentFamily -> println("자식을 생성하지 못했습니다. 부/모 계열이 다릅니다.")
            BreedingResult.UnsupportedFamily -> println("자식을 생성하지 못했습니다. 지원하지 않는 계열입니다.")
            BreedingResult.UnsavedParent -> println("자식을 생성하지 못했습니다. 저장되지 않은 부모입니다.")
        }
    }

    private fun listHumans() {
        val humans = humanService.findAll()

        if (humans.isEmpty()) {
            println("사람이 없습니다.")
            return
        }

        humans.forEach { human ->
            println(describeHuman(human))
        }
    }

    private fun printOwnershipResult(result: OwnershipResult, successMessage: String) {
        when (result) {
            OwnershipResult.Success -> println(successMessage)
            OwnershipResult.AnimalNotFound -> println("실패했습니다. 동물을 찾을 수 없습니다.")
            OwnershipResult.AlreadyOwnedByOther -> println("실패했습니다. 이미 다른 주인이 있습니다.")
            OwnershipResult.NotOwner -> println("실패했습니다. 현재 주인이 아닙니다.")
            OwnershipResult.UnsavedAnimal -> println("실패했습니다. 저장되지 않은 동물입니다.")
            OwnershipResult.UnsavedHuman -> println("실패했습니다. 저장되지 않은 사람입니다.")
        }
    }

    private fun listAnimals() {
        val animals = animalService.findAll()

        if (animals.isEmpty()) {
            println("동물이 없습니다.")
            return
        }

        animals.forEach { animal ->
            println(describeAnimal(animal))
        }
    }

    private fun readHuman(prompt: String): Human? {
        val id = readInt(prompt) ?: return null
        val human = humanService.findById(id)

        if (human == null) {
            println("해당 사람이 없습니다.")
        }

        return human
    }

    private fun readAnimal(prompt: String): Animal? {
        val id = readInt(prompt) ?: return null
        val animal = animalService.findById(id)

        if (animal == null) {
            println("해당 동물이 없습니다.")
        }

        return animal
    }

    private fun readInt(prompt: String): Int? {
        print(prompt)
        val value = readlnOrNull()?.trim()?.toIntOrNull()

        if (value == null) {
            println("숫자를 입력해야 합니다.")
        }

        return value
    }

    private fun readText(prompt: String): String {
        print(prompt)
        return readlnOrNull()?.trim().orEmpty()
    }

    private fun describeHuman(human: Human): String {
        return "Human(id=${human.id}, name=${human.name}, petIds=${human.petIds})"
    }

    private fun describeAnimal(animal: Animal): String {
        val mixInfo = if (animal is MixedAnimal) {
            ", mix=${animal.breedPercentages}"
        } else {
            ""
        }

        return "${animal.javaClass.simpleName}(id=${animal.id}, name=${animal.name}, ownerId=${animal.ownerId}, fatherId=${animal.fatherId}, motherId=${animal.motherId}, childIds=${animal.childIds}$mixInfo)"
    }
}
