package com.github.hmiyado.kottage.service.entries

import com.github.hmiyado.kottage.model.Comment
import com.github.hmiyado.kottage.model.Page
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.repository.entries.EntryCommentRepository
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK

class EntriesCommentsServiceImplTest : DescribeSpec() {
    @MockK
    lateinit var entryCommentRepository: EntryCommentRepository
    private lateinit var service: EntriesCommentsService

    override fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        MockKAnnotations.init(this, relaxUnitFun = true)
        service = EntriesCommentsServiceImpl(entryCommentRepository)
    }

    init {
        describe("getComments") {
            it("should return comments") {
                val totalCount = 100L
                val comments = (1..20).map { Comment(id = it.toLong()) }
                every { entryCommentRepository.getTotalComments(1) } returns totalCount
                every { entryCommentRepository.getComments(1, any(), any()) } returns comments
                val actual = service.getComments(1, null, null)
                actual shouldBe Page(totalCount, comments)
            }
            it("should return comments with limit and offset") {
                val totalCount = 100L
                val limit = 10L
                val offset = 5L
                val comments = (1..limit).map { Comment(id = it + offset) }
                every { entryCommentRepository.getTotalComments(1) } returns totalCount
                every { entryCommentRepository.getComments(1, limit, offset) } returns comments
                val actual = service.getComments(1, limit, offset)
                actual shouldBe Page(totalCount, comments)
            }
            it("should return comments with max limit") {
                val totalCount = 100L
                val limit = 10000L
                val offset = 5L
                val comments = (1..20).map { Comment(id = it + offset) }
                every { entryCommentRepository.getTotalComments(1) } returns totalCount
                every {
                    entryCommentRepository.getComments(
                        1,
                        EntriesCommentsService.maxLimit,
                        offset
                    )
                } returns comments
                val actual = service.getComments(1, limit, offset)
                actual shouldBe Page(totalCount, comments)
            }
        }

        describe("addComment") {
            it("should add comment") {
                val body = "body"
                val user = User()
                val expected = Comment(body = body, author = user)
                every { entryCommentRepository.createComment(1, body, user.id) } returns expected
                val actual = service.addComment(1, body, user)
                actual shouldBe expected
            }
        }
    }
}
