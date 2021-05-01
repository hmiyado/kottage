package com.github.hmiyado.route

import com.github.hmiyado.helper.AuthorizationHelper
import com.github.hmiyado.model.Article
import com.github.hmiyado.service.articles.ArticlesService
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.routing.routing
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import java.time.ZonedDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.test.KoinTest

class ArticlesRoutingKtTest : DescribeSpec(), KoinTest {
    @MockK
    lateinit var articlesService: ArticlesService
    private lateinit var testApplicationEngine: TestApplicationEngine

    override fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        MockKAnnotations.init(this)

        testApplicationEngine = TestApplicationEngine()
        testApplicationEngine.start()
        with(testApplicationEngine) {
            with(application) {
                AuthorizationHelper.installAuthentication(this)
                routing {
                    articles(articlesService)
                }
            }
        }
    }

    override fun afterSpec(spec: Spec) {
        super.afterSpec(spec)
        testApplicationEngine.stop(0L, 0L)
    }

    init {
        describe("route /articles") {
            it("should return articles") {
                with(testApplicationEngine) {
                    every { articlesService.getArticles() } returns listOf()
                    with(handleRequest(HttpMethod.Get, "/articles")) {
                        response shouldHaveStatus HttpStatusCode.OK
                    }
                }
            }
        }

        describe("route POST /articles") {
            it("should return new article") {
                with(testApplicationEngine) {
                    val requestArticleTitle = "title1"
                    val requestArticleBody = "body1"
                    val request = buildJsonObject {
                        put("title", requestArticleTitle)
                        put("body", requestArticleBody)
                    }
                    val article = Article(requestArticleTitle, requestArticleBody, ZonedDateTime.now())
                    every { articlesService.createArticle(requestArticleTitle, requestArticleBody) } returns article

                    with(handleRequest(HttpMethod.Post, "/articles") {
                        AuthorizationHelper.authorizeAsAdmin(this)
                        setBody(request.toString())
                    }) {
                        response shouldHaveStatus HttpStatusCode.OK
                        response.content shouldMatchJson Json.encodeToString(article)
                    }
                }
            }

            it("should return Bad Request") {
                with(testApplicationEngine) {
                    with(handleRequest(HttpMethod.Post, "/articles") {
                        AuthorizationHelper.authorizeAsAdmin(this)
                        setBody("")
                    }) {
                        response shouldHaveStatus HttpStatusCode.BadRequest
                    }
                }
            }

            it("should return Unauthorized") {
                with(testApplicationEngine) {
                    with(handleRequest(HttpMethod.Post, "/articles") {
                        setBody("")
                    }) {
                        response shouldHaveStatus HttpStatusCode.Unauthorized
                    }
                }
            }
        }
    }
}
