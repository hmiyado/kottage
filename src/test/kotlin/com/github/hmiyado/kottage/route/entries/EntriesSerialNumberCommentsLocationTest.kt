package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.helper.AuthorizationHelper
import com.github.hmiyado.kottage.helper.KtorApplicationTestListener
import com.github.hmiyado.kottage.helper.RoutingTestHelper
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.Comment
import com.github.hmiyado.kottage.model.Page
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.route.assignPathParams
import com.github.hmiyado.kottage.service.entries.EntriesCommentsService
import com.github.hmiyado.kottage.service.users.UsersService
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.setBody
import io.ktor.sessions.SessionStorage
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class EntriesSerialNumberCommentsLocationTest : DescribeSpec() {
    private val ktorListener = KtorApplicationTestListener(beforeSpec = {
        MockKAnnotations.init(this@EntriesSerialNumberCommentsLocationTest)
        authorizationHelper = AuthorizationHelper(usersService, sessionStorage)
        RoutingTestHelper.setupRouting(application, {
            authorizationHelper.installSessionAuthentication(it)
        }) {
            EntriesSerialNumberCommentsLocation(entriesCommentsService).addRoute(this)
        }
    })

    private lateinit var authorizationHelper: AuthorizationHelper

    @MockK
    lateinit var entriesCommentsService: EntriesCommentsService

    @MockK
    lateinit var usersService: UsersService

    @MockK
    lateinit var sessionStorage: SessionStorage

    override fun listeners(): List<TestListener> = listOf(ktorListener)

    init {
        describe("GET ${Paths.entriesSerialNumberCommentsGet}") {
            it("should return comments") {
                val comments = (1..5).map { Comment(it.toLong(), "${it}th comment") }
                val page = Page(
                    comments.size.toLong(),
                    comments
                )
                every { entriesCommentsService.getComments(1, null, null) } returns page
                ktorListener.handleJsonRequest(
                    HttpMethod.Get,
                    Paths.entriesSerialNumberCommentsGet.assignPathParams(1)
                ) {
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson page.toOpenApiComments()
                }
            }
            it("should return comments with limit and offset") {
                val comments = (1..5).map { Comment(it.toLong(), "${it}th comment") }
                val page = Page(
                    comments.size.toLong(),
                    comments
                )
                val limit = 5L
                val offset = 5L
                every { entriesCommentsService.getComments(1, limit, offset) } returns page
                ktorListener.handleJsonRequest(
                    HttpMethod.Get,
                    "${Paths.entriesSerialNumberCommentsGet.assignPathParams(1)}?limit=$limit&offset=$offset"
                ) {
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson page.toOpenApiComments()
                }
            }
        }

        describe("POST ${Paths.entriesSerialNumberCommentsPost}") {
            it("should return a created comment") {
                val body = "body"
                val user = User(id = 1)
                val comment = Comment(id = 1, body = body, author = user)
                every { entriesCommentsService.addComment(1, body, user) } returns comment
                ktorListener.handleJsonRequest(
                    HttpMethod.Post,
                    Paths.entriesSerialNumberCommentsPost.assignPathParams(1)
                ) {
                    authorizationHelper.authorizeAsUserAndAdmin(
                        this,
                        user
                    )
                    setBody(buildJsonObject {
                        put("body", body)
                    }.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.Created
                    response shouldMatchAsJson comment.toOpenApiComment()
                }
            }

            it("should return Unauthorized") {
                ktorListener.handleJsonRequest(
                    HttpMethod.Post,
                    Paths.entriesSerialNumberCommentsPost.assignPathParams(1)
                ) {
                    setBody(buildJsonObject {}.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.Unauthorized
                }
            }
            it("should return BadRequest") {
                ktorListener.handleJsonRequest(
                    HttpMethod.Post,
                    Paths.entriesSerialNumberCommentsPost.assignPathParams(1)
                ) {
                    authorizationHelper.authorizeAsUserAndAdmin(
                        this,
                        User(id = 1)
                    )
                    setBody(buildJsonObject {}.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                }

            }
        }
    }
}
