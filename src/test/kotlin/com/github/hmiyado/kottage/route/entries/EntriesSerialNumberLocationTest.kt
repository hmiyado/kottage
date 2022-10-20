package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.application.plugins.statuspages.ErrorFactory
import com.github.hmiyado.kottage.helper.KtorApplicationTest
import com.github.hmiyado.kottage.helper.KtorApplicationTestDelegate
import com.github.hmiyado.kottage.helper.delete
import com.github.hmiyado.kottage.helper.get
import com.github.hmiyado.kottage.helper.patch
import com.github.hmiyado.kottage.helper.routing
import com.github.hmiyado.kottage.helper.shouldHaveStatus
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.Entry
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.route.assignPathParams
import com.github.hmiyado.kottage.service.entries.EntriesService
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.setBody
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class EntriesSerialNumberLocationTest : DescribeSpec(), KtorApplicationTest by KtorApplicationTestDelegate() {
    @MockK
    lateinit var entriesService: EntriesService

    override fun listeners(): List<TestListener> = listOf(listener)

    init {
        MockKAnnotations.init(this)
        routing {
            EntriesSerialNumberLocation(entriesService).addRoute(this)
        }

        describe("DELETE ${Paths.entriesSerialNumberDelete}") {
            it("should return OK") {
                every { entriesService.deleteEntry(1, userId = 99) } just Runs
                delete(Paths.entriesSerialNumberDelete.assignPathParams("serialNumber" to 1)) {
                    authorizeAsAdmin(User(id = 99))
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                    verify {
                        entriesService.deleteEntry(1, 99)
                    }
                }
            }

            it("should return Unauthorized") {
                delete(Paths.entriesSerialNumberDelete.assignPathParams("serialNumber" to 1)).run {
                    response shouldHaveStatus HttpStatusCode.Unauthorized
                }
            }

            it("should return Bad Request") {
                delete(Paths.entriesSerialNumberDelete.assignPathParams("serialNumber" to "string")) {
                    authorizeAsAdmin(User(id = 99))
                }.run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400("path parameter is not valid")
                }
            }

            it("should return Forbidden") {
                every {
                    entriesService.deleteEntry(
                        1,
                        userId = 99
                    )
                } throws EntriesService.ForbiddenOperationException(1, 99)
                delete(
                    Paths.entriesSerialNumberDelete.assignPathParams("serialNumber" to 1)
                ) {
                    authorizeAsAdmin(User(id = 99))
                }.run {
                    response shouldHaveStatus HttpStatusCode.Forbidden
                }
            }
        }

        describe("PATCH ${Paths.entriesSerialNumberPatch}") {
            it("should return OK") {
                val user = User(id = 99)
                val expected = Entry(1, "title 1", author = user)
                every { entriesService.updateEntry(expected.serialNumber, user.id, "title 1", null) } returns expected
                patch(Paths.entriesSerialNumberPatch.assignPathParams("serialNumber" to expected.serialNumber)) {
                    authorizeAsAdmin(user)
                    setBody(
                        buildJsonObject {
                            put("title", "title 1")
                        }.toString(),
                    )
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson expected
                }
            }

            it("should return Bad Request") {
                patch(Paths.entriesSerialNumberPatch.assignPathParams("serialNumber" to "string")) {
                    authorizeAsAdmin(User(id = 1))
                }.run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400("path parameter is not valid")
                }
            }

            it("should return Unauthorized when no session") {
                patch(Paths.entriesSerialNumberPatch.assignPathParams("serialNumber" to 1)).run {
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
                patch(Paths.entriesSerialNumberPatch.assignPathParams("serialNumber" to 1)) {
                    authorizeAsAdmin(User(id = 1))
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
                patch(Paths.entriesSerialNumberPatch.assignPathParams("serialNumber" to "999")) {
                    authorizeAsAdmin(User(id = 1))
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
                get(Paths.entriesSerialNumberGet.assignPathParams(1)).run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson entry
                }
            }

            it("should return Bad Request when serialNumber is not long") {
                get(Paths.entriesSerialNumberGet.assignPathParams("string")).run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400("path parameter is not valid")
                }
            }

            it("should return Not Found when there is no entry that matches serialNumber") {
                every { entriesService.getEntry(any()) } returns null
                get(Paths.entriesSerialNumberGet.assignPathParams(999)).run {
                    response shouldHaveStatus HttpStatusCode.NotFound
                }
            }
        }
    }
}
