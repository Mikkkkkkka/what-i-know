package com.example.whatiknow.domain.model.notes

data class WorkspaceNote(
    val id: Int,
    val path: String,
    var content: String
)
