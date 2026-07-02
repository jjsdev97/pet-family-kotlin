package service

import domain.animal.Animal

sealed interface BreedingResult {
    data class Success(val child: Animal) : BreedingResult
    data object SameAnimal : BreedingResult
    data object DifferentFamily : BreedingResult
    data object UnsupportedFamily : BreedingResult
    data object UnsavedParent : BreedingResult
}
