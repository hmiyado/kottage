package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.application.plugins.statuspages.ErrorFactory
import com.github.hmiyado.kottage.helper.KtorApplicationTest
import com.github.hmiyado.kottage.helper.KtorApplicationTestDelegate
import com.github.hmiyado.kottage.helper.get
import com.github.hmiyado.kottage.helper.post
import com.github.hmiyado.kottage.helper.routing
import com.github.hmiyado.kottage.helper.shouldHaveStatus
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.Comment
import com.github.hmiyado.kottage.model.Page
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.route.assignPathParams
import com.github.hmiyado.kottage.service.entries.EntriesCommentsService
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.http.HttpStatusCode
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.serialization.json.put

class EntriesSerialNumberCommentsLocationTest : DescribeSpec(), KtorApplicationTest by KtorApplicationTestDelegate() {

    @MockK
    lateinit var entriesCommentsService: EntriesCommentsService

    override fun listeners(): List<TestListener> = listOf(listener)

    init {
        MockKAnnotations.init(this)
        routing {
            EntriesSerialNumberCommentsLocation(usersService, entriesCommentsService).addRoute(this)
        }

        describe("GET ${Paths.entriesSerialNumberCommentsGet}") {
            it("should return comments") {
                val comments = (1..5).map { Comment(it.toLong(), name = "comment_${it}th") }
                val page = Page(
                    comments.size.toLong(),
                    comments
                )
                every { entriesCommentsService.getComments(1, null, null) } returns page
                get(Paths.entriesSerialNumberCommentsGet.assignPathParams(1)).run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson page.toOpenApiComments()
                }
            }
            it("should return comments with limit and offset") {
                val comments = (1..5).map { Comment(it.toLong(), name = "comment_${it}th") }
                val page = Page(
                    comments.size.toLong(),
                    comments
                )
                val limit = 5L
                val offset = 5L
                every { entriesCommentsService.getComments(1, limit, offset) } returns page
                get("${Paths.entriesSerialNumberCommentsGet.assignPathParams(1)}?limit=$limit&offset=$offset").run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson page.toOpenApiComments()
                }
            }
        }

        describe("POST ${Paths.entriesSerialNumberCommentsPost}") {
            it("should return a created comment") {
                val name = "name"
                val body = "body"
                val user = User(id = 1)
                val comment = Comment(id = 1, body = body, author = user)
                every { entriesCommentsService.addComment(1, name, body, user) } returns comment
                post(
                    Paths.entriesSerialNumberCommentsPost.assignPathParams(1),
                    {
                        put("name", name)
                        put("body", body)
                    }
                ) {
                    authorizeAsAdmin(user)
                }.run {
                    response shouldHaveStatus HttpStatusCode.Created
                    response shouldMatchAsJson comment.toOpenApiComment()
                }
            }

            it("should return a created comment when no user session") {
                val name = "name"
                val body = "body"
                val comment = Comment(id = 1, body = body, author = null)
                every { entriesCommentsService.addComment(1, name, body, null) } returns comment
                post(
                    Paths.entriesSerialNumberCommentsPost.assignPathParams(1),
                    {
                        put("name", "name")
                        put("body", "body")
                    }
                ).run {
                    response shouldHaveStatus HttpStatusCode.Created
                }
            }
            it("should return BadRequest") {
                post(Paths.entriesSerialNumberCommentsPost.assignPathParams(1)) {
                    authorizeAsAdmin(User(id = 1))
                }.run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400("request body is not valid")
                }
            }
        }
    }
}
