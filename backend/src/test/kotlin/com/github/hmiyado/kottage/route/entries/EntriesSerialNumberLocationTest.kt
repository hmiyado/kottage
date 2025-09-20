package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.application.contentNegotiation
import com.github.hmiyado.kottage.application.plugins.statuspages.ErrorFactory
import com.github.hmiyado.kottage.application.plugins.statuspages.OpenApiStatusPageRouter
import com.github.hmiyado.kottage.helper.AuthorizationHelper
import com.github.hmiyado.kottage.helper.authorizeAsUserAndAdmin
import com.github.hmiyado.kottage.helper.shouldHaveStatus
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.Entry
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.route.assignPathParams
import com.github.hmiyado.kottage.service.entries.EntriesService
import com.github.hmiyado.kottage.service.users.UsersService
import com.github.hmiyado.kottage.service.users.admins.AdminsService
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.routing.routing
import io.ktor.server.sessions.SessionStorage
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class EntriesSerialNumberLocationTest : DescribeSpec() {
    @MockK
    lateinit var entriesService: EntriesService

    @MockK
    lateinit var usersService: UsersService

    @MockK
    lateinit var sessionStorage: SessionStorage

    @MockK
    lateinit var adminsService: AdminsService

    lateinit var authorizationHelper: AuthorizationHelper

    override suspend fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        MockKAnnotations.init(this@EntriesSerialNumberLocationTest)
        authorizationHelper = AuthorizationHelper(usersService, sessionStorage, adminsService)
    }

    private fun ApplicationTestBuilder.init() {
        application {
            contentNegotiation()
            authorizationHelper.installSessionAuthentication(this)
            routing {
                EntriesSerialNumberLocation(entriesService).addRoute(this)
            }
            install(StatusPages) {
                EntriesSerialNumberLocation.addStatusPage(this)
                OpenApiStatusPageRouter.addStatusPage(this)
            }
        }
    }

    init {
        describe("DELETE ${Paths.entriesSerialNumberDelete}") {
            it("should return OK") {
                testApplication {
                    init()
                    every { entriesService.deleteEntry(1, userId = 99) } just Runs
                    val user = User(id = 99)

                    val response =
                        client.delete(Paths.entriesSerialNumberDelete.assignPathParams("serialNumber" to 1)) {
                            authorizeAsUserAndAdmin(authorizationHelper, user)
                        }
                    response shouldHaveStatus HttpStatusCode.OK
                    verify { entriesService.deleteEntry(1, 99) }
                }
            }

            it("should return Unauthorized") {
                testApplication {
                    init()
                    val response = client.delete(Paths.entriesSerialNumberDelete.assignPathParams("serialNumber" to 1))
                    response shouldHaveStatus HttpStatusCode.Unauthorized
                }
            }

            it("should return Bad Request") {
                testApplication {
                    init()
                    val response =
                        client.delete(Paths.entriesSerialNumberDelete.assignPathParams("serialNumber" to "string")) {
                            authorizeAsUserAndAdmin(authorizationHelper, User(id = 99))
                        }
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400("path parameter is not valid")
                }
            }

            it("should return Forbidden") {
                testApplication {
                    init()
                    every {
                        entriesService.deleteEntry(1, userId = 99)
                    } throws EntriesService.ForbiddenOperationException(1, 99)

                    val response =
                        client.delete(Paths.entriesSerialNumberDelete.assignPathParams("serialNumber" to 1)) {
                            authorizeAsUserAndAdmin(authorizationHelper, User(id = 99))
                        }
                    response shouldHaveStatus HttpStatusCode.Forbidden
                }
            }
        }

        describe("PATCH ${Paths.entriesSerialNumberPatch}") {
            it("should return OK") {
                testApplication {
                    init()
                    val user = User(id = 99)
                    val expected = Entry(1, "title 1", author = user)
                    every {
                        entriesService.updateEntry(
                            expected.serialNumber,
                            user.id,
                            "title 1",
                            null,
                        )
                    } returns expected

                    val response =
                        client.patch(Paths.entriesSerialNumberPatch.assignPathParams("serialNumber" to expected.serialNumber)) {
                            header("Content-Type", ContentType.Application.Json)
                            setBody(
                                buildJsonObject {
                                    put("title", "title 1")
                                }.toString(),
                            )
                            authorizeAsUserAndAdmin(authorizationHelper, user)
                        }
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson expected
                }
            }

            it("should return Bad Request") {
                testApplication {
                    init()
                    val response =
                        client.patch(Paths.entriesSerialNumberPatch.assignPathParams("serialNumber" to "string")) {
                            header("Content-Type", ContentType.Application.Json)
                            setBody(
                                buildJsonObject {
                                    put("title", "title 1")
                                }.toString(),
                            )
                            authorizeAsUserAndAdmin(authorizationHelper, User(id = 1))
                        }
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400("path parameter is not valid")
                }
            }

            it("should return Unauthorized when no session") {
                testApplication {
                    init()
                    val response = client.patch(Paths.entriesSerialNumberPatch.assignPathParams("serialNumber" to 1))
                    response shouldHaveStatus HttpStatusCode.Unauthorized
                }
            }

            it("should return Forbidden") {
                testApplication {
                    init()
                    every {
                        entriesService.updateEntry(any(), any(), any(), any())
                    } throws EntriesService.ForbiddenOperationException(1, 1)

                    val response =
                        client.patch(Paths.entriesSerialNumberPatch.assignPathParams("serialNumber" to 1)) {
                            header("Content-Type", ContentType.Application.Json)
                            setBody(buildJsonObject {}.toString())
                            authorizeAsUserAndAdmin(authorizationHelper, User(id = 1))
                        }
                    response shouldHaveStatus HttpStatusCode.Forbidden
                }
            }

            it("should return NotFound") {
                testApplication {
                    init()
                    every {
                        entriesService.updateEntry(any(), any(), any(), any())
                    } throws EntriesService.NoSuchEntryException(999)

                    val response =
                        client.patch(Paths.entriesSerialNumberPatch.assignPathParams("serialNumber" to "999")) {
                            header("Content-Type", ContentType.Application.Json)
                            setBody(buildJsonObject {}.toString())
                            authorizeAsUserAndAdmin(authorizationHelper, User(id = 1))
                        }
                    response shouldHaveStatus HttpStatusCode.NotFound
                }
            }
        }

        describe("GET ${Paths.entriesSerialNumberGet}") {
            it("should return an entry") {
                testApplication {
                    init()
                    val entry = Entry(serialNumber = 1)
                    every { entriesService.getEntry(any()) } returns entry

                    val response = client.get(Paths.entriesSerialNumberGet.assignPathParams(1))
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson entry
                }
            }

            it("should return Bad Request when serialNumber is not long") {
                testApplication {
                    init()
                    val response = client.get(Paths.entriesSerialNumberGet.assignPathParams("string"))
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400("path parameter is not valid")
                }
            }

            it("should return Not Found when there is no entry that matches serialNumber") {
                testApplication {
                    init()
                    every { entriesService.getEntry(any()) } returns null
                    val response = client.get(Paths.entriesSerialNumberGet.assignPathParams(999))
                    response shouldHaveStatus HttpStatusCode.NotFound
                }
            }
        }
    }
}
