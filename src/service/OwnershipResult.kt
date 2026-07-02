package service

sealed interface OwnershipResult {
    data object Success : OwnershipResult
    data object AnimalNotFound : OwnershipResult
    data object AlreadyOwnedByOther : OwnershipResult
    data object NotOwner : OwnershipResult
    data object UnsavedAnimal : OwnershipResult
    data object UnsavedHuman : OwnershipResult
}
