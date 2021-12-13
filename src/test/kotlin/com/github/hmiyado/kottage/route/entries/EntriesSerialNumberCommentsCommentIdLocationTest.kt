package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.helper.AuthorizationHelper
import com.github.hmiyado.kottage.helper.KtorApplicationTestListener
import com.github.hmiyado.kottage.helper.RoutingTestHelper
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
import io.ktor.sessions.SessionStorage
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify

class EntriesSerialNumberCommentsCommentIdLocationTest : DescribeSpec() {
    private val ktorListener = KtorApplicationTestListener(beforeSpec = {
        MockKAnnotations.init(this@EntriesSerialNumberCommentsCommentIdLocationTest)
        authorizationHelper = AuthorizationHelper(usersService, sessionStorage)
        RoutingTestHelper.setupRouting(application, {
            authorizationHelper.installSessionAuthentication(it)
        }) {
            EntriesSerialNumberCommentsCommentIdLocation(entriesCommentsService).addRoute(this)
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
        describe("DELETE ${Paths.entriesSerialNumberCommentsCommentIdDelete}") {
            it("should delete a comment") {
                val user = User()
                every { entriesCommentsService.removeComment(1, 10, user) } just Runs
                ktorListener.handleJsonRequest(
                    HttpMethod.Delete,
                    Paths.entriesSerialNumberCommentsCommentIdDelete.assignPathParams(1, 10),
                ) {
                    authorizationHelper.authorizeAsUser(this, user)
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
                        user
                    )
                } throws IllegalStateException()
                ktorListener.handleJsonRequest(
                    HttpMethod.Delete,
                    Paths.entriesSerialNumberCommentsCommentIdDelete.assignPathParams("text", 10),
                ) {
                    authorizationHelper.authorizeAsUser(this, user)
                }.run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                }
            }
            it("should be BadRequest when comment id is invalid") {
                val user = User()
                every {
                    entriesCommentsService.removeComment(
                        any(),
                        any(),
                        user
                    )
                } throws IllegalStateException()
                ktorListener.handleJsonRequest(
                    HttpMethod.Delete,
                    Paths.entriesSerialNumberCommentsCommentIdDelete.assignPathParams(1, "text"),
                ) {
                    authorizationHelper.authorizeAsUser(this, user)
                }.run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                }
            }
            it("should be Unauthorized") {
                ktorListener.handleJsonRequest(
                    HttpMethod.Delete,
                    Paths.entriesSerialNumberCommentsCommentIdDelete.assignPathParams(1, 10),
                ) {
                }.run {
                    response shouldHaveStatus HttpStatusCode.Unauthorized
                }
            }
            it("should be Forbidden") {
                val user = User()
                every {
                    entriesCommentsService.removeComment(
                        1,
                        10,
                        user
                    )
                } throws EntriesCommentsService.ForbiddenOperationException(1, 10, user.id)
                ktorListener.handleJsonRequest(
                    HttpMethod.Delete,
                    Paths.entriesSerialNumberCommentsCommentIdDelete.assignPathParams(1, 10),
                ) {
                    authorizationHelper.authorizeAsUser(this, user)
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
                        user
                    )
                } throws EntriesCommentsService.NoSuchCommentException(10)
                ktorListener.handleJsonRequest(
                    HttpMethod.Delete,
                    Paths.entriesSerialNumberCommentsCommentIdDelete.assignPathParams(1, 10),
                ) {
                    authorizationHelper.authorizeAsUser(this, user)
                }.run {
                    response shouldHaveStatus HttpStatusCode.NotFound
                }
            }
        }
    }

}
