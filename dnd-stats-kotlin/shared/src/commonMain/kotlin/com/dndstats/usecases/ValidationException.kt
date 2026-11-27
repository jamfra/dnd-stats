package com.dndstats.usecases

class ValidationException(val errors: List<String>) : RuntimeException(errors.joinToString(", "))
