package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.application.plugins.statuspages.ErrorFactory
import com.github.hmiyado.kottage.helper.KtorApplicationTest
import com.github.hmiyado.kottage.helper.KtorApplicationTestDelegate
import com.github.hmiyado.kottage.helper.get
import com.github.hmiyado.kottage.helper.kottageJson
import com.github.hmiyado.kottage.helper.post
import com.github.hmiyado.kottage.helper.routing
import com.github.hmiyado.kottage.helper.shouldHaveHeader
import com.github.hmiyado.kottage.helper.shouldHaveStatus
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.Entry
import com.github.hmiyado.kottage.model.Page
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.service.entries.EntriesService
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.http.HttpStatusCode
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import org.koin.test.KoinTest

class EntriesLocationTest : DescribeSpec(), KoinTest, KtorApplicationTest by KtorApplicationTestDelegate() {
    @MockK
    lateinit var entriesService: EntriesService

    override fun listeners(): List<TestListener> = listOf(listener)

    init {
        MockKAnnotations.init(this@EntriesLocationTest)
        routing {
            EntriesLocation(entriesService).addRoute(this)
        }

        describe("GET ${Paths.entriesGet}") {
            it("should return empty entries") {
                every { entriesService.getEntries() } returns Page()
                get(Paths.entriesGet).run {
                    response shouldHaveStatus HttpStatusCode.OK
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
                get(Paths.entriesGet).run {
                    response shouldHaveStatus HttpStatusCode.OK
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
                get("${Paths.entriesGet}?limit=20&offset=10").run {
                    response shouldHaveStatus HttpStatusCode.OK
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
                val user = User(id = 99, screenName = "entry_creator")
                val entry = Entry(serialNumber = 1, requestTitle, requestBody, author = user)
                every { entriesService.createEntry(requestTitle, requestBody, user.id) } returns entry

                post(Paths.entriesPost, {
                    put("title", requestTitle)
                    put("body", requestBody)
                }) {
                    authorizeAsAdmin(user)
                }.run {
                    response shouldHaveStatus HttpStatusCode.Created

                    response.shouldHaveHeader("Location", "http://localhost/api/v1/entries/1")
                    response shouldMatchAsJson entry
                }
            }

            it("should return Bad Request") {
                post(Paths.entriesPost, "") {
                    val user = User(id = 1)
                    authorizeAsAdmin(user)
                }.run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400("request body is not valid")
                }
            }

            it("should return Unauthorized") {
                post(Paths.entriesPost).run {
                    response shouldHaveStatus HttpStatusCode.Unauthorized
                }
            }
        }
    }
}
