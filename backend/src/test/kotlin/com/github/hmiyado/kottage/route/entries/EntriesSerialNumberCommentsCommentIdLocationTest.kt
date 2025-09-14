package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.application.contentNegotiation
import com.github.hmiyado.kottage.application.plugins.statuspages.ErrorFactory
import com.github.hmiyado.kottage.application.plugins.statuspages.OpenApiStatusPageRouter
import com.github.hmiyado.kottage.helper.AuthorizationHelper
import com.github.hmiyado.kottage.helper.authorizeAsUserAndAdmin
import com.github.hmiyado.kottage.helper.shouldHaveStatus
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.route.assignPathParams
import com.github.hmiyado.kottage.service.entries.EntriesCommentsService
import com.github.hmiyado.kottage.service.users.UsersService
import com.github.hmiyado.kottage.service.users.admins.AdminsService
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.ktor.client.request.delete
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.routing.routing
import io.ktor.server.sessions.SessionStorage
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify

class EntriesSerialNumberCommentsCommentIdLocationTest : DescribeSpec() {
    @MockK
    lateinit var entriesCommentsService: EntriesCommentsService

    @MockK
    lateinit var usersService: UsersService

    @MockK
    lateinit var sessionStorage: SessionStorage

    @MockK
    lateinit var adminsService: AdminsService

    lateinit var authorizationHelper: AuthorizationHelper

    override suspend fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        MockKAnnotations.init(this@EntriesSerialNumberCommentsCommentIdLocationTest)
        authorizationHelper = AuthorizationHelper(usersService, sessionStorage, adminsService)
    }

    private fun ApplicationTestBuilder.init() {
        application {
            contentNegotiation()
            authorizationHelper.installSessionAuthentication(this)
            routing {
                EntriesSerialNumberCommentsCommentIdLocation(entriesCommentsService).addRoute(this)
            }
            install(StatusPages) {
                OpenApiStatusPageRouter.addStatusPage(this)
            }
        }
    }

    init {
        describe("DELETE ${Paths.entriesSerialNumberCommentsCommentIdDelete}") {
            it("should delete a comment") {
                testApplication {
                    init()
                    val user = User()
                    every { entriesCommentsService.removeComment(1, 10, user) } just Runs

                    val response =
                        client.delete(Paths.entriesSerialNumberCommentsCommentIdDelete.assignPathParams(1, 10)) {
                            authorizeAsUserAndAdmin(authorizationHelper, user)
                        }
                    response shouldHaveStatus HttpStatusCode.OK
                    verify { entriesCommentsService.removeComment(1, 10, user) }
                }
            }
            it("should be BadRequest when serial number is invalid") {
                testApplication {
                    init()
                    val user = User()
                    every {
                        entriesCommentsService.removeComment(
                            any(),
                            any(),
                            user,
                        )
                    } throws IllegalStateException()

                    val response =
                        client.delete(Paths.entriesSerialNumberCommentsCommentIdDelete.assignPathParams("text", 10)) {
                            authorizeAsUserAndAdmin(authorizationHelper, user)
                        }
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400("path parameter is not valid")
                }
            }
            it("should be BadRequest when comment id is invalid") {
                testApplication {
                    init()
                    val user = User()
                    every {
                        entriesCommentsService.removeComment(
                            any(),
                            any(),
                            user,
                        )
                    } throws IllegalStateException()

                    val response =
                        client.delete(Paths.entriesSerialNumberCommentsCommentIdDelete.assignPathParams(1, "text")) {
                            authorizeAsUserAndAdmin(authorizationHelper, user)
                        }
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400("path parameter is not valid")
                }
            }
            it("should be Unauthorized") {
                testApplication {
                    init()
                    val response =
                        client.delete(Paths.entriesSerialNumberCommentsCommentIdDelete.assignPathParams(1, 10))
                    response shouldHaveStatus HttpStatusCode.Unauthorized
                }
            }
            it("should be Forbidden") {
                testApplication {
                    init()
                    val user = User()
                    every {
                        entriesCommentsService.removeComment(
                            1,
                            10,
                            user,
                        )
                    } throws EntriesCommentsService.ForbiddenOperationException(1, 10, user.id)

                    val response =
                        client.delete(Paths.entriesSerialNumberCommentsCommentIdDelete.assignPathParams(1, 10)) {
                            authorizeAsUserAndAdmin(authorizationHelper, user)
                        }
                    response shouldHaveStatus HttpStatusCode.Forbidden
                }
            }
            it("should be NotFound") {
                testApplication {
                    init()
                    val user = User()
                    every {
                        entriesCommentsService.removeComment(
                            1,
                            10,
                            user,
                        )
                    } throws EntriesCommentsService.NoSuchCommentException(10)

                    val response =
                        client.delete(Paths.entriesSerialNumberCommentsCommentIdDelete.assignPathParams(1, 10)) {
                            authorizeAsUserAndAdmin(authorizationHelper, user)
                        }
                    response shouldHaveStatus HttpStatusCode.NotFound
                }
            }
        }
    }
}
