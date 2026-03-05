package com.example.whatiknow.domain.model.notes

data class PersistentNote(
    val id: Int,
    val path: String,
    var content: String
)
