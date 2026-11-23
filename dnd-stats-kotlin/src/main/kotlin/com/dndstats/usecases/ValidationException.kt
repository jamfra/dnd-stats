package com.dndstats.usecases

class ValidationException(val errors: List<String>) : IllegalArgumentException(errors.joinToString("; "))
