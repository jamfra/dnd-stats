package com.dndstats.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator
import com.lemonappdev.konsist.api.architecture.Layer
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.jupiter.api.Test

class StrictHexagonalArchitectureTest {

    @Test
    fun `hexagonal architecture layers follow strict dependency rules`() {
        with(KoArchitectureCreator) {
            Konsist.scopeFromProduction().assertArchitecture {
                val domain = Layer("Domain", "com.dndstats.domain..")
                val useCases = Layer("UseCases", "com.dndstats.usecases..")
                val presentation = Layer("Presentation", "com.dndstats.presentation..")
                val gateways = Layer("Gateways", "com.dndstats.gateways..")

                domain.dependsOnNothing()
                useCases.dependsOn(domain)
                presentation.dependsOn(useCases)
                gateways.dependsOn(domain, useCases)
            }
        }
    }

    @Test
    fun `domain has no dependencies on usecases, gateways, presentation, or frameworks`() {
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
    fun `usecases has no dependencies on presentation, concrete gateway adapters, or frameworks`() {
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
    fun `presentation has no dependencies on domain`() {
        Konsist
            .scopeFromProduction()
            .files
            .withPackage("com.dndstats.presentation..")
            .assertFalse { file ->
                file.hasImport { import ->
                    import.hasNameStartingWith("com.dndstats.domain")
                }
            }
    }

    @Test
    fun `presentation has no dependencies on gateways`() {
        Konsist
            .scopeFromProduction()
            .files
            .withPackage("com.dndstats.presentation..")
            .assertFalse { file ->
                file.hasImport { import ->
                    import.hasNameStartingWith("com.dndstats.gateways")
                }
            }
    }

    @Test
    fun `gateways has no dependencies on presentation`() {
        Konsist
            .scopeFromProduction()
            .files
            .withPackage("com.dndstats.gateways..")
            .assertFalse { file ->
                file.hasImport { import ->
                    import.hasNameStartingWith("com.dndstats.presentation")
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
