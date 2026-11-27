package com.dndstats.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator
import com.lemonappdev.konsist.api.architecture.Layer
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.jupiter.api.Test

class SharedHexagonalArchitectureTest {

    @Test
    fun `hexagonal architecture layers in shared follow strict dependency rules`() {
        with(KoArchitectureCreator) {
            Konsist.scopeFromProduction().assertArchitecture {
                val domain = Layer("Domain", "com.dndstats.domain..")
                val useCases = Layer("UseCases", "com.dndstats.usecases..")
                val gateways = Layer("Gateways", "com.dndstats.gateways..")

                domain.dependsOnNothing()
                useCases.dependsOn(domain)
                gateways.dependsOn(domain, useCases)
            }
        }
    }

    @Test
    fun `domain has no dependencies on usecases, gateways, or frameworks`() {
        Konsist
            .scopeFromProduction()
            .files
            .withPackage("com.dndstats.domain..")
            .assertFalse { file ->
                file.hasImport { import ->
                    import.hasNameStartingWith("com.dndstats.usecases") ||
                        import.hasNameStartingWith("com.dndstats.gateways") ||
                        import.hasNameStartingWith("com.dndstats.presentation") ||
                        import.hasNameStartingWith("io.ktor") ||
                        import.hasNameStartingWith("kotlinx.html")
                }
            }
    }

    @Test
    fun `usecases has no dependencies on concrete gateway adapters or frameworks`() {
        Konsist
            .scopeFromProduction()
            .files
            .withPackage("com.dndstats.usecases..")
            .assertFalse { file ->
                file.hasImport { import ->
                    import.hasNameStartingWith("com.dndstats.presentation") ||
                        import.hasNameStartingWith("com.dndstats.gateways") ||
                        import.hasNameStartingWith("io.ktor") ||
                        import.hasNameStartingWith("kotlinx.html")
                }
            }
    }

    @Test
    fun `zero framework imports in domain and usecases`() {
        Konsist
            .scopeFromProduction()
            .files
            .withPackage("com.dndstats.domain..", "com.dndstats.usecases..")
            .assertFalse { file ->
                file.hasImport { import ->
                    import.hasNameStartingWith("io.ktor") ||
                        import.hasNameStartingWith("kotlinx.html")
                }
            }
    }

    @Test
    fun `all use cases have corresponding unit tests`() {
        Konsist
            .scopeFromProduction()
            .classes()
            .withPackage("com.dndstats.usecases..")
            .filter { it.hasFunction { func -> func.name == "execute" } }
            .assertTrue { it.hasTestClass { true } }
    }
}
