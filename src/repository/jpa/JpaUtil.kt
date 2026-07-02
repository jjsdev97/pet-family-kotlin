package repository.jpa

import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence

object JpaUtil {
    val entityManagerFactory: EntityManagerFactory =
        Persistence.createEntityManagerFactory("kotlin-prac")
}
