package com.github.hmiyado.kottage.route.comments

import com.github.hmiyado.kottage.application.contentNegotiation
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.Comment
import com.github.hmiyado.kottage.model.Page
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.route.entries.toOpenApiComments
import com.github.hmiyado.kottage.service.entries.EntriesCommentsService
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.client.request.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK

class CommentsLocationTest : DescribeSpec() {
    @MockK
    lateinit var entriesCommentsService: EntriesCommentsService

    init {
        MockKAnnotations.init(this)
        val init: ApplicationTestBuilder.() -> Unit = {
            application {
                contentNegotiation()
                routing {
                    CommentsLocation(entriesCommentsService).addRoute(this)
                }
            }

        }
        describe("GET ${Paths.commentsGet}") {
            it("should return comments") {
                testApplication {
                    init()
                    val expected = Page(0, emptyList<Comment>())
                    every { entriesCommentsService.getComments(null, any(), any()) } returns expected
                    val response = client.get(Paths.commentsGet)
                    response shouldMatchAsJson expected.toOpenApiComments()
                }
            }
        }
    }
}
