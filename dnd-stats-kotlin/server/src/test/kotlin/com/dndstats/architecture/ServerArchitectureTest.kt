package com.dndstats.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertFalse
import org.junit.jupiter.api.Test

class ServerArchitectureTest {

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
}
