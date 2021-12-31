package com.github.hmiyado.kottage.route.comments

import com.github.hmiyado.kottage.helper.KtorApplicationTestListener
import com.github.hmiyado.kottage.helper.RoutingTestHelper
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.Comment
import com.github.hmiyado.kottage.model.Page
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.route.entries.toOpenApiComments
import com.github.hmiyado.kottage.service.entries.EntriesCommentsService
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.http.HttpMethod
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK

class CommentsLocationTest : DescribeSpec() {
    private val ktorListener = KtorApplicationTestListener(beforeSpec = {
        MockKAnnotations.init(this@CommentsLocationTest)
        RoutingTestHelper.setupRouting(application) {
            CommentsLocation(entriesCommentsService).addRoute(this)
        }
    })

    @MockK
    lateinit var entriesCommentsService: EntriesCommentsService

    override fun listeners(): List<TestListener> = listOf(ktorListener)

    init {
        describe("GET ${Paths.commentsGet}") {
            it("should return comments") {
                val expected = Page(0, emptyList<Comment>())
                every { entriesCommentsService.getComments(null, any(), any()) } returns expected
                ktorListener.handleJsonRequest(HttpMethod.Get, Paths.commentsGet).run {
                    response shouldMatchAsJson expected.toOpenApiComments()
                }
            }
        }
    }
}
