package com.dndstats.presentation.views

import kotlinx.html.*
import java.util.UUID

fun HTML.deleteConfirmationPage(id: UUID, name: String) {
    body {
        h1 { +"Delete Character" }
        p { +"Delete $name? This cannot be undone." }

        form(action = "/character/$id/delete", method = FormMethod.post) {
            submitInput { value = "Confirm Delete" }
        }

        p {
            a(href = "/") { +"Cancel" }
        }
    }
}
