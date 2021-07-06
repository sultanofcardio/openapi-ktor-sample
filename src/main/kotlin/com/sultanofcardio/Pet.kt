package com.sultanofcardio

import org.json.JSONArray
import org.json.JSONObject

data class Pet(
    val id: Int,
    val name: String,
    val category: PetCategory,
    val photoUrls: List<String>,
    val tags: List<PetTag>,
    val status: String
): Model {
    override fun json(): JSONObject = json {
        "id" to id
        "name" to name
        "category" to category.json()
        "photoUrls" to JSONArray(photoUrls)
        "tags" to JSONArray(tags.map { it.json() })
        "status" to status
    }
}


data class PetCategory(val id: Int, val name: String): Model {
    override fun json(): JSONObject = json {
        "id" to id
        "name" to name
    }
}

data class PetTag(val id: Int, val name: String): Model {
    override fun json(): JSONObject = json {
        "id" to id
        "name" to name
    }
}

val samplePet = Pet(
    id = 10,
    name = "doggie",
    category = PetCategory(0, "Dogs"),
    photoUrls = listOf("string"),
    tags = listOf(PetTag(0, "String")),
    status = "available"
)
