package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.application.plugins.statuspages.ErrorFactory
import com.github.hmiyado.kottage.helper.AuthorizationHelper
import com.github.hmiyado.kottage.helper.authorizeAsUserAndAdmin
import com.github.hmiyado.kottage.helper.kottageJson
import com.github.hmiyado.kottage.helper.shouldHaveHeader
import com.github.hmiyado.kottage.helper.shouldHaveStatus
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.Entry
import com.github.hmiyado.kottage.model.Page
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.service.entries.EntriesService
import com.github.hmiyado.kottage.service.users.UsersService
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import io.ktor.server.sessions.SessionStorage
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import org.koin.test.KoinTest

class EntriesLocationTest :
    DescribeSpec(),
    KoinTest {
    @MockK
    lateinit var entriesService: EntriesService

    @MockK
    lateinit var usersService: UsersService

    @MockK
    lateinit var sessionStorage: SessionStorage

    lateinit var authorizationHelper: AuthorizationHelper

    override suspend fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        MockKAnnotations.init(this@EntriesLocationTest)
        authorizationHelper = AuthorizationHelper(usersService, sessionStorage)
    }

    private val init: ApplicationTestBuilder.() -> Unit = {
        application {
            authorizationHelper.installSessionAuthentication(this)
            routing {
                EntriesLocation(entriesService).addRoute(this)
            }
        }
    }

    init {
        describe("GET ${Paths.entriesGet}") {
            it("should return empty entries") {
                testApplication {
                    init()
                    every { entriesService.getEntries() } returns Page()
                    val response = client.get(Paths.entriesGet)
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson
                            buildJsonObject {
                                putJsonArray("items") {}
                                put("totalCount", 0)
                            }
                }
            }
            it("should return entries") {
                testApplication {
                    init()
                    val entries = (1L..10).map { Entry(serialNumber = it) }
                    every { entriesService.getEntries() } returns
                            Page(
                                totalCount = entries.size.toLong(),
                                items = entries,
                            )
                    val response = client.get(Paths.entriesGet)
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson
                            buildJsonObject {
                                putJsonArray("items") {
                                    for (entry in entries) {
                                        add(kottageJson.encodeToJsonElement(entry))
                                    }
                                }
                                put("totalCount", 10)
                            }
                }
            }
            it("should return entries with offset and limit") {
                testApplication {
                    init()
                    val entries = (1L..10).map { Entry(serialNumber = it) }
                    every { entriesService.getEntries(limit = 20, offset = 10) } returns
                            Page(
                                totalCount = entries.size.toLong(),
                                items = entries,
                            )
                    val response = client.get("${Paths.entriesGet}?limit=20&offset=10")
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson
                            buildJsonObject {
                                putJsonArray("items") {
                                    for (entry in entries) {
                                        add(kottageJson.encodeToJsonElement(entry))
                                    }
                                }
                                put("totalCount", 10)
                            }
                }
            }
        }

        describe("POST ${Paths.entriesPost}") {
            it("should return new entry") {
                testApplication {
                    init()
                    val requestTitle = "title1"
                    val requestBody = "body1"
                    val user = User(id = 99, screenName = "entry_creator")
                    val entry = Entry(serialNumber = 1, requestTitle, requestBody, author = user)
                    every { entriesService.createEntry(requestTitle, requestBody, user.id) } returns entry

                    val response =
                        client.post(Paths.entriesPost) {
                            header("Content-Type", ContentType.Application.Json)
                            setBody(
                                buildJsonObject {
                                    put("title", requestTitle)
                                    put("body", requestBody)
                                }.toString(),
                            )
                            authorizeAsUserAndAdmin(authorizationHelper, user)
                        }
                    response shouldHaveStatus HttpStatusCode.Created
                    response.shouldHaveHeader("Location", "http://localhost/api/v1/entries/1")
                    response shouldMatchAsJson entry
                }
            }

            it("should return Bad Request") {
                testApplication {
                    init()
                    val response =
                        client.post(Paths.entriesPost) {
                            header("Content-Type", ContentType.Application.Json)
                            setBody("")
                            authorizeAsUserAndAdmin(authorizationHelper, User(id = 1))
                        }
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400("request body is not valid")
                }
            }

            it("should return Unauthorized") {
                testApplication {
                    init()
                    val response = client.post(Paths.entriesPost)
                    response shouldHaveStatus HttpStatusCode.Unauthorized
                }
            }
        }
    }
}
