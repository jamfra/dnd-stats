package com.dndstats.presentation.views

import kotlinx.html.*
import kotlin.uuid.Uuid

fun HTML.deleteConfirmationPage(id: Uuid, name: String) {
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
