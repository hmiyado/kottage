package com.github.hmiyado.route.entries

import com.github.hmiyado.helper.AuthorizationHelper
import com.github.hmiyado.helper.KtorApplicationTestListener
import com.github.hmiyado.helper.shouldMatchAsJson
import com.github.hmiyado.model.Entry
import com.github.hmiyado.service.entries.EntriesService
import io.kotest.assertions.ktor.shouldHaveContentType
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.withCharset
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.testing.setBody
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import java.nio.charset.Charset
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.test.KoinTest

class EntriesSerialNumberRoutingTest : DescribeSpec(), KoinTest {
    private val ktorListener = KtorApplicationTestListener(beforeSpec = {
        MockKAnnotations.init(this@EntriesSerialNumberRoutingTest)
        with(application) {
            install(ContentNegotiation) {
                // this must be first because this becomes default ContentType
                json(contentType = ContentType.Application.Json)
                json(contentType = ContentType.Any)
                json(contentType = ContentType.Text.Any)
                json(contentType = ContentType.Text.Plain)
            }
            AuthorizationHelper.installAuthentication(this)
            routing {
                entriesSerialNumber(entriesService)
            }
        }

    })

    @MockK
    lateinit var entriesService: EntriesService

    override fun listeners(): List<TestListener> = listOf(ktorListener)

    init {
        describe("DELETE /entries/{serialNumber}") {
            it("should return OK") {
                every { entriesService.deleteEntry(1) } just Runs
                ktorListener
                    .handleRequest(HttpMethod.Delete, "/entries/1") {
                        AuthorizationHelper.authorizeAsAdmin(this)
                    }
                    .run {
                        response shouldHaveStatus HttpStatusCode.OK
                        verify {
                            entriesService.deleteEntry(1)
                        }
                    }
            }

            it("should return Unauthorized") {
                ktorListener
                    .handleRequest(HttpMethod.Delete, "/entries/1").run {
                        response shouldHaveStatus HttpStatusCode.Unauthorized
                    }
            }

            it("should return Bad Request") {
                ktorListener
                    .handleRequest(HttpMethod.Delete, "/entries/string") {
                        AuthorizationHelper.authorizeAsAdmin(this)
                    }
                    .run {
                        response shouldHaveStatus HttpStatusCode.BadRequest
                    }
            }
        }

        describe("PATCH /entries/{serialNumber}") {
            it("should return OK") {
                val expected = Entry(1, "title 1")
                every { entriesService.updateEntry(1, "title 1", null) } returns expected
                ktorListener
                    .handleRequest(HttpMethod.Patch, "/entries/1") {
                        AuthorizationHelper.authorizeAsAdmin(this)
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
                    .handleRequest(HttpMethod.Patch, "/entries/string") {
                        AuthorizationHelper.authorizeAsAdmin(this)
                    }
                    .run {
                        response shouldHaveStatus HttpStatusCode.BadRequest
                    }
            }

            it("should return Unauthorized") {
                ktorListener
                    .handleRequest(HttpMethod.Patch, "/entries/1").run {
                        response shouldHaveStatus HttpStatusCode.Unauthorized
                    }
            }

            it("should return NotFound") {
                every { entriesService.updateEntry(any(), any(), any()) } returns null
                ktorListener
                    .handleRequest(HttpMethod.Patch, "/entries/999") {
                        AuthorizationHelper.authorizeAsAdmin(this)
                    }.run {
                        response shouldHaveStatus HttpStatusCode.NotFound
                    }
            }
        }

        describe("GET /entries/{serialNumber}") {
            it("should return an entry") {
                val entry = Entry(serialNumber = 1)
                every { entriesService.getEntry(any()) } returns entry
                ktorListener
                    .handleRequest(HttpMethod.Get, "/entries/1").run {
                        response shouldHaveStatus HttpStatusCode.OK
                        response.shouldHaveContentType(ContentType.Application.Json.withCharset(Charset.forName("UTF-8")))
                        response shouldMatchAsJson entry
                    }
            }

            it("should return Bad Request when serialNumber is not long") {
                ktorListener
                    .handleRequest(HttpMethod.Get, "/entries/string").run {
                        response shouldHaveStatus HttpStatusCode.BadRequest
                    }
            }

            it("should return Not Found when there is no entry that matches serialNumber") {
                every { entriesService.getEntry(any()) } returns null
                ktorListener
                    .handleRequest(HttpMethod.Get, "/entries/999").run {
                        response shouldHaveStatus HttpStatusCode.NotFound
                    }
            }
        }
    }
}
