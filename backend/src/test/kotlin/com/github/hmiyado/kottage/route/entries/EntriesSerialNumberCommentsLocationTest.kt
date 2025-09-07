package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.application.plugins.statuspages.ErrorFactory
import com.github.hmiyado.kottage.helper.AuthorizationHelper
import com.github.hmiyado.kottage.helper.authorizeAsUserAndAdmin
import com.github.hmiyado.kottage.helper.shouldHaveStatus
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.Comment
import com.github.hmiyado.kottage.model.Page
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.route.assignPathParams
import com.github.hmiyado.kottage.service.entries.EntriesCommentsService
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
import kotlinx.serialization.json.put

class EntriesSerialNumberCommentsLocationTest : DescribeSpec() {
    @MockK
    lateinit var entriesCommentsService: EntriesCommentsService

    @MockK
    lateinit var usersService: UsersService

    @MockK
    lateinit var sessionStorage: SessionStorage

    lateinit var authorizationHelper: AuthorizationHelper

    override suspend fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        MockKAnnotations.init(this@EntriesSerialNumberCommentsLocationTest)
        authorizationHelper = AuthorizationHelper(usersService, sessionStorage)
    }

    private val init: ApplicationTestBuilder.() -> Unit = {
        authorizationHelper.installSessionAuthentication(this)
        application {
            routing {
                EntriesSerialNumberCommentsLocation(usersService, entriesCommentsService).addRoute(this)
            }
        }
    }

    init {
        describe("GET ${Paths.entriesSerialNumberCommentsGet}") {
            it("should return comments") {
                testApplication {
                    init()
                    val comments = (1..5).map { Comment(it.toLong(), name = "comment_${it}th") }
                    val page = Page(
                        comments.size.toLong(),
                        comments,
                    )
                    every { entriesCommentsService.getComments(1, null, null) } returns page
                    
                    val response = client.get(Paths.entriesSerialNumberCommentsGet.assignPathParams(1))
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson page.toOpenApiComments()
                }
            }
            it("should return comments with limit and offset") {
                testApplication {
                    init()
                    val comments = (1..5).map { Comment(it.toLong(), name = "comment_${it}th") }
                    val page = Page(
                        comments.size.toLong(),
                        comments,
                    )
                    val limit = 5L
                    val offset = 5L
                    every { entriesCommentsService.getComments(1, limit, offset) } returns page
                    
                    val response = client.get("${Paths.entriesSerialNumberCommentsGet.assignPathParams(1)}?limit=$limit&offset=$offset")
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson page.toOpenApiComments()
                }
            }
        }

        describe("POST ${Paths.entriesSerialNumberCommentsPost}") {
            it("should return a created comment") {
                testApplication {
                    init()
                    val name = "name"
                    val body = "body"
                    val user = User(id = 1)
                    val comment = Comment(id = 1, body = body, author = user)
                    every { entriesCommentsService.addComment(1, name, body, user) } returns comment
                    
                    val response = client.post(Paths.entriesSerialNumberCommentsPost.assignPathParams(1)) {
                        header("Content-Type", ContentType.Application.Json)
                        setBody(buildJsonObject {
                            put("name", name)
                            put("body", body)
                        }.toString())
                        authorizeAsUserAndAdmin(authorizationHelper, user)
                    }
                    response shouldHaveStatus HttpStatusCode.Created
                    response shouldMatchAsJson comment.toOpenApiComment()
                }
            }

            it("should return a created comment when no user session") {
                testApplication {
                    init()
                    val name = "name"
                    val body = "body"
                    val comment = Comment(id = 1, body = body, author = null)
                    every { entriesCommentsService.addComment(1, name, body, null) } returns comment
                    
                    val response = client.post(Paths.entriesSerialNumberCommentsPost.assignPathParams(1)) {
                        header("Content-Type", ContentType.Application.Json)
                        setBody(buildJsonObject {
                            put("name", "name")
                            put("body", "body")
                        }.toString())
                    }
                    response shouldHaveStatus HttpStatusCode.Created
                }
            }
            it("should return BadRequest") {
                testApplication {
                    init()
                    val response = client.post(Paths.entriesSerialNumberCommentsPost.assignPathParams(1)) {
                        authorizeAsUserAndAdmin(authorizationHelper, User(id = 1))
                    }
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400("request body is not valid")
                }
            }
        }
    }
}
