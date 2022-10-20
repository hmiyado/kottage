package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.application.plugins.statuspages.ErrorFactory
import com.github.hmiyado.kottage.helper.KtorApplicationTest
import com.github.hmiyado.kottage.helper.KtorApplicationTestDelegate
import com.github.hmiyado.kottage.helper.delete
import com.github.hmiyado.kottage.helper.routing
import com.github.hmiyado.kottage.helper.shouldHaveStatus
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.route.assignPathParams
import com.github.hmiyado.kottage.service.entries.EntriesCommentsService
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.http.HttpStatusCode
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify

class EntriesSerialNumberCommentsCommentIdLocationTest :
    DescribeSpec(),
    KtorApplicationTest by KtorApplicationTestDelegate() {
    @MockK
    lateinit var entriesCommentsService: EntriesCommentsService

    override fun listeners(): List<TestListener> = listOf(listener)

    init {
        MockKAnnotations.init(this)
        routing {
            EntriesSerialNumberCommentsCommentIdLocation(entriesCommentsService).addRoute(this)
        }

        describe("DELETE ${Paths.entriesSerialNumberCommentsCommentIdDelete}") {
            it("should delete a comment") {
                val user = User()
                every { entriesCommentsService.removeComment(1, 10, user) } just Runs
                delete(Paths.entriesSerialNumberCommentsCommentIdDelete.assignPathParams(1, 10)) {
                    authorizeAsUser(user)
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                    verify { entriesCommentsService.removeComment(1, 10, user) }
                }
            }
            it("should be BadRequest when serial number is invalid") {
                val user = User()
                every {
                    entriesCommentsService.removeComment(
                        any(),
                        any(),
                        user,
                    )
                } throws IllegalStateException()
                delete(Paths.entriesSerialNumberCommentsCommentIdDelete.assignPathParams("text", 10)) {
                    authorizeAsUser(user)
                }.run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400("path parameter is not valid")
                }
            }
            it("should be BadRequest when comment id is invalid") {
                val user = User()
                every {
                    entriesCommentsService.removeComment(
                        any(),
                        any(),
                        user,
                    )
                } throws IllegalStateException()
                delete(Paths.entriesSerialNumberCommentsCommentIdDelete.assignPathParams(1, "text")) {
                    authorizeAsUser(user)
                }.run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400("path parameter is not valid")
                }
            }
            it("should be Unauthorized") {
                delete(Paths.entriesSerialNumberCommentsCommentIdDelete.assignPathParams(1, 10)).run {
                    response shouldHaveStatus HttpStatusCode.Unauthorized
                }
            }
            it("should be Forbidden") {
                val user = User()
                every {
                    entriesCommentsService.removeComment(
                        1,
                        10,
                        user,
                    )
                } throws EntriesCommentsService.ForbiddenOperationException(1, 10, user.id)
                delete(Paths.entriesSerialNumberCommentsCommentIdDelete.assignPathParams(1, 10)) {
                    authorizeAsUser(user)
                }.run {
                    response shouldHaveStatus HttpStatusCode.Forbidden
                }
            }
            it("should be NotFound") {
                val user = User()
                every {
                    entriesCommentsService.removeComment(
                        1,
                        10,
                        user,
                    )
                } throws EntriesCommentsService.NoSuchCommentException(10)
                delete(Paths.entriesSerialNumberCommentsCommentIdDelete.assignPathParams(1, 10)) {
                    authorizeAsUser(user)
                }.run {
                    response shouldHaveStatus HttpStatusCode.NotFound
                }
            }
        }
    }
}
