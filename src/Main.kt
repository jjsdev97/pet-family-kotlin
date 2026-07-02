import domain.animal.InMemoryAnimalRepository
import domain.human.InMemoryHumanRepository
import repository.jpa.JpaAnimalRepository
import repository.jpa.JpaHumanRepository
import repository.jpa.JpaUtil
import ui.ConsoleApp

fun main() {
    println("실행 모드를 선택하세요.")
    println("1. 메모리 모드")
    println("2. JPA/H2 모드")
    print("선택: ")

    when (readlnOrNull()?.trim()) {
        "2" -> runJpaConsole()
        else -> ConsoleApp(InMemoryHumanRepository(), InMemoryAnimalRepository()).run()
    }
}

private fun runJpaConsole() {
    val entityManager = JpaUtil.entityManagerFactory.createEntityManager()

    try {
        ConsoleApp(
            humanRepository = JpaHumanRepository(entityManager),
            animalRepository = JpaAnimalRepository(entityManager),
            runAction = { action ->
                val transaction = entityManager.transaction
                transaction.begin()

                try {
                    action()
                    transaction.commit()
                } catch (error: RuntimeException) {
                    if (transaction.isActive) {
                        transaction.rollback()
                    }

                    throw error
                }
            }
        ).run()
    } catch (error: RuntimeException) {
        throw error
    } finally {
        entityManager.close()
        JpaUtil.entityManagerFactory.close()
    }
}
