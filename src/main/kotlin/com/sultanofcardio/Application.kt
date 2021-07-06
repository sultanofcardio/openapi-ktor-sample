package com.sultanofcardio

import com.sultanofcardio.openapi.components.securityscheme.ApiKeyLocation
import com.sultanofcardio.openapi.components.securityscheme.apiKey
import com.sultanofcardio.openapi.components.securityscheme.basicAuth
import com.sultanofcardio.openapi.components.securityscheme.oauth2
import com.sultanofcardio.openapi.models.HtmlContent
import com.sultanofcardio.openapi.models.JsonContent
import com.sultanofcardio.openapi.models.RequestBody
import com.sultanofcardio.openapi.models.Tag
import com.sultanofcardio.openapi.openapi
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

/**
 * Please note that you can use any other name instead of *module*.
 * Also note that you can have more then one modules in your application.
 * */
@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    routing {
        openapi {
            info {
                version = "1.0.5"
                title = "Swagger Petstore - OpenAPI 3.0"
                description = """
                    This is a sample Pet Store Server based on the OpenAPI 3.0 specification. You can find out more about Swagger at [http://swagger.io](http://swagger.io). In the third iteration of the pet store, we've switched to the design first approach! You can now help us improve the API whether it's by making changes to the definition itself or to the code. That way, with time, we can improve the API in general, and expose some of the new features in OAS3.
                    
                    Some useful links:
                    - [The Pet Store repository](https://github.com/swagger-api/swagger-petstore)
                    - [The source API definition for the Pet Store](https://github.com/swagger-api/swagger-petstore/blob/master/src/main/resources/openapi.yaml)
                """.trimIndent()
                termsOfService = "http://swagger.io/terms/"
                contact { email = "apiteam@swagger.io" }
                license("Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0.html")
            }
            externalDocs("http://swagger.io", "Find out more about Swagger")

            tag("store", "Operations about user")
            tag("user") {
                description = "Access to Petstore orders"
                externalDocs("http://swagger.io", "Find out more about our store")
            }
            val petTag = Tag("pet").apply {
                description = "Everything about your Pets"
                externalDocs("http://swagger.io", "Find out more")
            }
            tags(petTag)

            val petStoreAuth = oauth2("petstore_auth") { flows ->
                val url = "https://petstore3.swagger.io/oauth/"
                client = HttpClient(Apache)
                providerLookup = {
                    OAuthServerSettings.OAuth2ServerSettings(
                        name = "github",
                        authorizeUrl = "$url/authorize",
                        accessTokenUrl = "$url/access_token",
                        clientId = "***",
                        clientSecret = "***"
                    )
                }
                urlProvider = { "" }
                flows.implicit {
                    authorizationUrl = "$url/authorize"
                    scopes {
                        "write:pets" to "modify pets in your account"
                        "read:pets" to "read your pets"
                    }
                }
            }
            val apiKey = apiKey("api_key", ApiKeyLocation.header)

            val sampleBasicAuth = basicAuth("sample") {
                validate {
                    object : Principal {}
                }
            }

            authenticate(sampleBasicAuth) {
                swaggerUI()
            }

            server("/api/v3")
            undocumentedRoute("/api/v3") {
                authenticate(petStoreAuth, apiKey) {
                    route("/pet") {
                        put {
                            tags(petTag)
                            summary = "Update an existing pet"
                            description = "Update an existing pet by Id"
                            requestBody = RequestBody(
                                required = true,
                                description = "Update an existent pet in the store",
                                content = JsonContent(samplePet)
                            )
                            responses = {
                                response {
                                    description = "Successful operation"
                                    content = JsonContent(samplePet)
                                }

                                response(HttpStatusCode.BadRequest) {
                                    description = "Invalid ID supplied"
                                }

                                response(HttpStatusCode.NotFound) {
                                    description = "Pet not found"
                                }

                                response(HttpStatusCode.MethodNotAllowed) {
                                    description = "Validation exception"
                                }
                            }
                            handle { call.respond(samplePet) }
                        }
                        post {
                            summary = "Add a new pet to the store"
                            tags(petTag)
                        }

                        get("/findByStatus") {
                            tags(petTag)
                            handle {
                                call.respondText { "Hello world" }
                            }
                        }
                    }
                }
            }
        }
    }
}

