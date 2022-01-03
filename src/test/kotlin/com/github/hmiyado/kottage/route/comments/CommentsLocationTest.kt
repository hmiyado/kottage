package com.github.hmiyado.kottage.route.comments

import com.github.hmiyado.kottage.helper.KtorApplicationTest
import com.github.hmiyado.kottage.helper.KtorApplicationTestDelegate
import com.github.hmiyado.kottage.helper.get
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.Comment
import com.github.hmiyado.kottage.model.Page
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.route.entries.toOpenApiComments
import com.github.hmiyado.kottage.service.entries.EntriesCommentsService
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK

class CommentsLocationTest : DescribeSpec(), KtorApplicationTest by KtorApplicationTestDelegate() {
    @MockK
    lateinit var entriesCommentsService: EntriesCommentsService

    override fun listeners(): List<TestListener> = listOf(listener)

    init {
        MockKAnnotations.init(this)
        routing {
            CommentsLocation(entriesCommentsService).addRoute(this)
        }
        describe("GET ${Paths.commentsGet}") {
            it("should return comments") {
                val expected = Page(0, emptyList<Comment>())
                every { entriesCommentsService.getComments(null, any(), any()) } returns expected
                get(Paths.commentsGet).run {
                    response shouldMatchAsJson expected.toOpenApiComments()
                }
            }
        }
    }
}
