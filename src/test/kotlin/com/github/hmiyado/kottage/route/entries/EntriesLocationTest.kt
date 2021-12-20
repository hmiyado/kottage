package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.helper.AuthorizationHelper
import com.github.hmiyado.kottage.helper.KtorApplicationTestListener
import com.github.hmiyado.kottage.helper.RoutingTestHelper
import com.github.hmiyado.kottage.helper.kottageJson
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.Entry
import com.github.hmiyado.kottage.model.Page
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.service.entries.EntriesService
import com.github.hmiyado.kottage.service.users.UsersService
import com.github.hmiyado.kottage.service.users.admins.AdminsService
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
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import org.koin.test.KoinTest

class EntriesLocationTest : DescribeSpec(), KoinTest {
    private val ktorListener = KtorApplicationTestListener(beforeSpec = {
        MockKAnnotations.init(this@EntriesLocationTest)
        authorizationHelper = AuthorizationHelper(usersService, sessionStorage, adminsService)

        RoutingTestHelper.setupRouting(application, {
            authorizationHelper.installSessionAuthentication(it)
        }) {
            EntriesLocation(entriesService).addRoute(this)
        }
    })

    lateinit var authorizationHelper: AuthorizationHelper

    @MockK
    lateinit var entriesService: EntriesService

    @MockK
    lateinit var usersService: UsersService

    @MockK
    lateinit var adminsService: AdminsService

    @MockK
    lateinit var sessionStorage: SessionStorage

    override fun listeners(): List<TestListener> = listOf(ktorListener)

    init {
        describe("GET ${Paths.entriesGet}") {
            it("should return empty entries") {
                every { entriesService.getEntries() } returns Page()
                ktorListener.handleRequest(HttpMethod.Get, Paths.entriesGet).run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response.shouldHaveContentType(ContentType.Application.Json.withCharset(Charset.forName("UTF-8")))
                    response shouldMatchAsJson buildJsonObject {
                        putJsonArray("items") {}
                        put("totalCount", 0)
                    }
                }
            }
            it("should return entries") {
                val entries = (1L..10).map { Entry(serialNumber = it) }
                every { entriesService.getEntries() } returns Page(
                    totalCount = entries.size.toLong(),
                    items = entries
                )
                ktorListener.handleRequest(HttpMethod.Get, Paths.entriesGet).run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response.shouldHaveContentType(ContentType.Application.Json.withCharset(Charset.forName("UTF-8")))
                    response shouldMatchAsJson buildJsonObject {
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
                val entries = (1L..10).map { Entry(serialNumber = it) }
                every { entriesService.getEntries(limit = 20, offset = 10) } returns Page(
                    totalCount = entries.size.toLong(),
                    items = entries
                )
                ktorListener.handleRequest(HttpMethod.Get, "${Paths.entriesGet}?limit=20&offset=10").run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response.shouldHaveContentType(ContentType.Application.Json.withCharset(Charset.forName("UTF-8")))
                    response shouldMatchAsJson buildJsonObject {
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
                val requestTitle = "title1"
                val requestBody = "body1"
                val request = buildJsonObject {
                    put("title", requestTitle)
                    put("body", requestBody)
                }
                val user = User(id = 99, screenName = "entry_creator")
                val entry = Entry(serialNumber = 1, requestTitle, requestBody, author = user)
                every { entriesService.createEntry(requestTitle, requestBody, user.id) } returns entry

                ktorListener.handleJsonRequest(HttpMethod.Post, Paths.entriesPost) {
                    authorizationHelper.authorizeAsUserAndAdmin(this, user)
                    setBody(request.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.Created
                    response.shouldHaveContentType(ContentType.Application.Json.withCharset(Charset.forName("UTF-8")))
                    response.shouldHaveHeader("Location", "http://localhost/api/v1/entries/1")
                    response shouldMatchAsJson entry
                }
            }

            it("should return Bad Request") {
                ktorListener.handleJsonRequest(HttpMethod.Post, Paths.entriesPost) {
                    val user = User(id = 1)
                    authorizationHelper.authorizeAsUserAndAdmin(this, user)
                    setBody("")
                }.run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                }
            }

            it("should return Unauthorized") {
                ktorListener.handleJsonRequest(HttpMethod.Post, Paths.entriesPost) {
                    setBody("")
                }.run {
                    response shouldHaveStatus HttpStatusCode.Unauthorized
                }
            }
        }
    }
}
