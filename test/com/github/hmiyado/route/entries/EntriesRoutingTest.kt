package com.github.hmiyado.route.entries

import com.github.hmiyado.helper.AuthorizationHelper
import com.github.hmiyado.helper.KtorApplicationTestListener
import com.github.hmiyado.model.Entry
import com.github.hmiyado.route.entries
import com.github.hmiyado.service.entries.EntriesService
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.assertions.ktor.shouldHaveContentType
import io.kotest.assertions.ktor.shouldHaveHeader
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
import io.mockk.every
import io.mockk.impl.annotations.MockK
import java.nio.charset.Charset
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.test.KoinTest

class EntriesRoutingTest : DescribeSpec(), KoinTest {
    private val ktorListener = KtorApplicationTestListener(beforeSpec = {
        MockKAnnotations.init(this@EntriesRoutingTest)
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
                entries(entriesService)
            }
        }

    })

    @MockK
    lateinit var entriesService: EntriesService

    override fun listeners(): List<TestListener> = listOf(ktorListener)

    init {
        describe("GET /entries") {
            it("should return entries") {
                every { entriesService.getEntries() } returns listOf()
                ktorListener.handleRequest(HttpMethod.Get, "/entries").run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response.shouldHaveContentType(ContentType.Application.Json.withCharset(Charset.forName("UTF-8")))
                    response.content shouldMatchJson "[]"
                }
            }
        }

        describe("POST /entries") {
            it("should return new article") {
                val requestArticleTitle = "title1"
                val requestArticleBody = "body1"
                val request = buildJsonObject {
                    put("title", requestArticleTitle)
                    put("body", requestArticleBody)
                }
                val article = Entry(serialNumber = 1, requestArticleTitle, requestArticleBody)
                every { entriesService.createEntry(requestArticleTitle, requestArticleBody) } returns article

                ktorListener.handleRequest(HttpMethod.Post, "/entries") {
                    AuthorizationHelper.authorizeAsAdmin(this)
                    setBody(request.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.Created
                    response.shouldHaveContentType(ContentType.Application.Json.withCharset(Charset.forName("UTF-8")))
                    response.shouldHaveHeader("Location", "http://localhost/entries/1")
                    response.content shouldMatchJson """
                            {"serialNumber":1,"title":"title1","body":"body1","dateTime":"1970-01-01T09:00:00+09:00[Asia/Tokyo]"}
                        """.trimIndent()
                }
            }

            it("should return Bad Request") {
                ktorListener.handleRequest(HttpMethod.Post, "/entries") {
                    AuthorizationHelper.authorizeAsAdmin(this)
                    setBody("")
                }.run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                }
            }

            it("should return Unauthorized") {
                ktorListener.handleRequest(HttpMethod.Post, "/entries") {
                    setBody("")
                }.run {
                    response shouldHaveStatus HttpStatusCode.Unauthorized
                }
            }
        }
    }
}
