package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.helper.AuthorizationHelper
import com.github.hmiyado.kottage.helper.KtorApplicationTestListener
import com.github.hmiyado.kottage.helper.RoutingTestHelper
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.Entry
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.route.assignPathParams
import com.github.hmiyado.kottage.service.entries.EntriesService
import com.github.hmiyado.kottage.service.users.UsersService
import com.github.hmiyado.kottage.service.users.admins.AdminsService
import io.kotest.assertions.ktor.shouldHaveContentType
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.withCharset
import io.ktor.server.testing.setBody
import io.ktor.sessions.SessionStorage
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import java.nio.charset.Charset
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class EntriesSerialNumberLocationTest : DescribeSpec() {
    private val ktorListener = KtorApplicationTestListener(beforeSpec = {
        MockKAnnotations.init(this@EntriesSerialNumberLocationTest)
        authorizationHelper = AuthorizationHelper(usersService, sessionStorage, adminsService)
        RoutingTestHelper.setupRouting(application, {
            authorizationHelper.installSessionAuthentication(it)
        }) {
            EntriesSerialNumberLocation.addRoute(this, entriesService)
        }
    })

    lateinit var authorizationHelper: AuthorizationHelper

    @MockK
    lateinit var entriesService: EntriesService

    @MockK
    lateinit var usersService: UsersService

    @MockK
    lateinit var sessionStorage: SessionStorage

    @MockK
    lateinit var adminsService: AdminsService

    override fun listeners(): List<TestListener> = listOf(ktorListener)

    init {
        describe("DELETE ${Paths.entriesSerialNumberDelete}") {
            it("should return OK") {
                every { entriesService.deleteEntry(1, userId = 99) } just Runs
                ktorListener
                    .handleJsonRequest(
                        HttpMethod.Delete,
                        Paths.entriesSerialNumberDelete.assignPathParams("serialNumber" to 1)
                    ) {
                        authorizationHelper.authorizeAsUserAndAdmin(
                            this,
                            User(id = 99)
                        )
                    }
                    .run {
                        response shouldHaveStatus HttpStatusCode.OK
                        verify {
                            entriesService.deleteEntry(1, 99)
                        }
                    }
            }

            it("should return Unauthorized") {
                ktorListener
                    .handleJsonRequest(
                        HttpMethod.Delete,
                        Paths.entriesSerialNumberDelete.assignPathParams("serialNumber" to 1)
                    ).run {
                        response shouldHaveStatus HttpStatusCode.Unauthorized
                    }
            }

            it("should return Bad Request") {
                ktorListener
                    .handleJsonRequest(
                        HttpMethod.Delete,
                        Paths.entriesSerialNumberDelete.assignPathParams("serialNumber" to "string")
                    ) {
                        authorizationHelper.authorizeAsUserAndAdmin(
                            this,
                            User(id = 99)
                        )
                    }
                    .run {
                        response shouldHaveStatus HttpStatusCode.BadRequest
                    }
            }

            it("should return Forbidden") {
                every {
                    entriesService.deleteEntry(
                        1,
                        userId = 99
                    )
                } throws EntriesService.ForbiddenOperationException(1, 99)
                ktorListener
                    .handleJsonRequest(
                        HttpMethod.Delete,
                        Paths.entriesSerialNumberDelete.assignPathParams("serialNumber" to 1)
                    ) {
                        authorizationHelper.authorizeAsUserAndAdmin(
                            this,
                            User(id = 99)
                        )
                    }
                    .run {
                        response shouldHaveStatus HttpStatusCode.Forbidden
                    }
            }
        }

        describe("PATCH ${Paths.entriesSerialNumberPatch}") {
            it("should return OK") {
                val user = User(id = 99)
                val expected = Entry(1, "title 1", author = user)
                every { entriesService.updateEntry(expected.serialNumber, user.id, "title 1", null) } returns expected
                ktorListener
                    .handleJsonRequest(
                        HttpMethod.Patch,
                        Paths.entriesSerialNumberPatch.assignPathParams("serialNumber" to expected.serialNumber)
                    ) {
                        authorizationHelper.authorizeAsUserAndAdmin(
                            this,
                            user
                        )
                        setBody(buildJsonObject {
                            put("title", "title 1")
                        }.toString())
                    }
                    .run {
                        response shouldHaveStatus HttpStatusCode.OK
                        response shouldMatchAsJson expected
                    }
            }

            it("should return Bad Request") {
                ktorListener
                    .handleJsonRequest(
                        HttpMethod.Patch,
                        Paths.entriesSerialNumberPatch.assignPathParams("serialNumber" to "string")
                    ) {
                        authorizationHelper.authorizeAsUserAndAdmin(
                            this,
                            User(id = 1)
                        )
                    }
                    .run {
                        response shouldHaveStatus HttpStatusCode.BadRequest
                    }
            }

            it("should return Unauthorized when no session") {
                ktorListener
                    .handleJsonRequest(
                        HttpMethod.Patch,
                        Paths.entriesSerialNumberPatch.assignPathParams("serialNumber" to 1)
                    ).run {
                        response shouldHaveStatus HttpStatusCode.Unauthorized
                    }
            }

            it("should return Forbidden") {
                every {
                    entriesService.updateEntry(
                        any(),
                        any(),
                        any(),
                        any()
                    )
                } throws EntriesService.ForbiddenOperationException(1, 1)
                ktorListener
                    .handleJsonRequest(
                        HttpMethod.Patch,
                        Paths.entriesSerialNumberPatch.assignPathParams("serialNumber" to 1)
                    ) {
                        authorizationHelper.authorizeAsUserAndAdmin(
                            this,
                            User(id = 1)
                        )
                        setBody(buildJsonObject {}.toString())
                    }.run {
                        response shouldHaveStatus HttpStatusCode.Forbidden
                    }
            }

            it("should return NotFound") {
                every {
                    entriesService.updateEntry(
                        any(),
                        any(),
                        any(),
                        any()
                    )
                } throws EntriesService.NoSuchEntryException(999)
                ktorListener
                    .handleJsonRequest(
                        HttpMethod.Patch,
                        Paths.entriesSerialNumberPatch.assignPathParams("serialNumber" to "999")
                    ) {
                        authorizationHelper.authorizeAsUserAndAdmin(
                            this,
                            User(id = 1)
                        )
                        setBody(buildJsonObject {}.toString())
                    }.run {
                        response shouldHaveStatus HttpStatusCode.NotFound
                    }
            }
        }

        describe("GET ${Paths.entriesSerialNumberGet}") {
            it("should return an entry") {
                val entry = Entry(serialNumber = 1)
                every { entriesService.getEntry(any()) } returns entry
                ktorListener
                    .handleJsonRequest(HttpMethod.Get, Paths.entriesSerialNumberGet.assignPathParams(1)).run {
                        response shouldHaveStatus HttpStatusCode.OK
                        response.shouldHaveContentType(ContentType.Application.Json.withCharset(Charset.forName("UTF-8")))
                        response shouldMatchAsJson entry
                    }
            }

            it("should return Bad Request when serialNumber is not long") {
                ktorListener
                    .handleJsonRequest(HttpMethod.Get, Paths.entriesSerialNumberGet.assignPathParams("string")).run {
                        response shouldHaveStatus HttpStatusCode.BadRequest
                    }
            }

            it("should return Not Found when there is no entry that matches serialNumber") {
                every { entriesService.getEntry(any()) } returns null
                ktorListener
                    .handleJsonRequest(HttpMethod.Get, Paths.entriesSerialNumberGet.assignPathParams(999)).run {
                        response shouldHaveStatus HttpStatusCode.NotFound
                    }
            }
        }
    }
}
