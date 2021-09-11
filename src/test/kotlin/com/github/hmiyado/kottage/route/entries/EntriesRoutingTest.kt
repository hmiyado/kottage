package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.helper.AuthorizationHelper
import com.github.hmiyado.kottage.helper.KtorApplicationTestListener
import com.github.hmiyado.kottage.helper.RoutingTestHelper
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.Entry
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.service.entries.EntriesService
import com.github.hmiyado.kottage.service.users.UsersService
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.assertions.ktor.shouldHaveContentType
import io.kotest.assertions.ktor.shouldHaveHeader
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
import io.mockk.every
import io.mockk.impl.annotations.MockK
import java.nio.charset.Charset
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.test.KoinTest

class EntriesRoutingTest : DescribeSpec(), KoinTest {
    private val ktorListener = KtorApplicationTestListener(beforeSpec = {
        MockKAnnotations.init(this@EntriesRoutingTest)
        RoutingTestHelper.setupRouting(application, {
            AuthorizationHelper.installSessionAuthentication(it, usersService, sessionStorage)
        }) {
            entries(entriesService)
        }
    })

    @MockK
    lateinit var entriesService: EntriesService

    @MockK
    lateinit var usersService: UsersService

    @MockK
    lateinit var sessionStorage: SessionStorage

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
            it("should return new entry") {
                val requestTitle = "title1"
                val requestBody = "body1"
                val request = buildJsonObject {
                    put("title", requestTitle)
                    put("body", requestBody)
                }
                val user = User(id = 99, screenName = "entry_creator")
                val entry = Entry(serialNumber = 1, requestTitle, requestBody, author = user)
                every { entriesService.createEntry(requestTitle, requestBody, user.id) } returns entry

                ktorListener.handleJsonRequest(HttpMethod.Post, "/entries") {
                    AuthorizationHelper.authorizeAsUser(this, usersService, sessionStorage, user)
                    setBody(request.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.Created
                    response.shouldHaveContentType(ContentType.Application.Json.withCharset(Charset.forName("UTF-8")))
                    response.shouldHaveHeader("Location", "http://localhost/entries/1")
                    response shouldMatchAsJson entry
                }
            }

            it("should return Bad Request") {
                ktorListener.handleJsonRequest(HttpMethod.Post, "/entries") {
                    AuthorizationHelper.authorizeAsUser(this, usersService, sessionStorage, User(id = 1))
                    setBody("")
                }.run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                }
            }

            it("should return Unauthorized") {
                ktorListener.handleJsonRequest(HttpMethod.Post, "/entries") {
                    setBody("")
                }.run {
                    response shouldHaveStatus HttpStatusCode.Unauthorized
                }
            }
        }
    }
}
