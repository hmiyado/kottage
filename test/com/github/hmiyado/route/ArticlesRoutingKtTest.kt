package com.github.hmiyado.route

import com.github.hmiyado.helper.AuthorizationHelper
import com.github.hmiyado.model.Article
import com.github.hmiyado.service.articles.ArticlesService
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.assertions.ktor.shouldHaveContentType
import io.kotest.assertions.ktor.shouldHaveHeader
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.withCharset
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import java.nio.charset.Charset
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
                install(ContentNegotiation) {
                    json()
                }
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
                        response.shouldHaveContentType(ContentType.Application.Json.withCharset(Charset.forName("UTF-8")))
                        response.content shouldMatchJson "[]"
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
                    val article = Article(serialNumber = 1, requestArticleTitle, requestArticleBody)
                    every { articlesService.createArticle(requestArticleTitle, requestArticleBody) } returns article

                    with(handleRequest(HttpMethod.Post, "/articles") {
                        AuthorizationHelper.authorizeAsAdmin(this)
                        setBody(request.toString())
                    }) {
                        response shouldHaveStatus HttpStatusCode.Created
                        response.shouldHaveContentType(ContentType.Application.Json.withCharset(Charset.forName("UTF-8")))
                        response.shouldHaveHeader("Location", "http://localhost/articles/1")
                        response.content shouldMatchJson """
                            {"serialNumber":1,"title":"title1","body":"body1","dateTime":"1970-01-01T09:00:00+09:00[Asia/Tokyo]"}
                        """.trimIndent()
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

        describe("route DELETE /articles/{serialNumber}") {
            it("should return OK") {
                every { articlesService.deleteArticle(1) } just Runs
                testApplicationEngine
                    .handleRequest(HttpMethod.Delete, "/articles/1") {
                        AuthorizationHelper.authorizeAsAdmin(this)
                    }
                    .run {
                        response shouldHaveStatus HttpStatusCode.OK
                        verify {
                            articlesService.deleteArticle(1)
                        }
                    }
            }

            it("should return Unauthorized") {
                testApplicationEngine.handleRequest(HttpMethod.Delete, "/articles/1").run {
                    response shouldHaveStatus HttpStatusCode.Unauthorized
                }
            }

            it("should return Bad Request") {
                testApplicationEngine
                    .handleRequest(HttpMethod.Delete, "/articles/string") {
                        AuthorizationHelper.authorizeAsAdmin(this)
                    }
                    .run {
                        response shouldHaveStatus HttpStatusCode.BadRequest
                    }
            }
        }

        describe("route GET /articles/{serialNumber}") {
            it("should return an article") {
                val article = Article(serialNumber = 1)
                every { articlesService.getArticle(any()) } returns article
                testApplicationEngine.handleRequest(HttpMethod.Get, "/articles/1").run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response.shouldHaveContentType(ContentType.Application.Json.withCharset(Charset.forName("UTF-8")))
                    response.content shouldMatchJson """
                        {"serialNumber":1,"title":"No title","body":"","dateTime":"1970-01-01T09:00:00+09:00[Asia/Tokyo]"}
                    """.trimIndent()
                }
            }

            it("should return Bad Request when serialNumber is not long") {
                testApplicationEngine.handleRequest(HttpMethod.Get, "/articles/string").run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                }
            }

            it("should return Not Found when there is no article that matches serialNumber") {
                every { articlesService.getArticle(any()) } returns null
                testApplicationEngine.handleRequest(HttpMethod.Get, "/articles/999").run {
                    response shouldHaveStatus HttpStatusCode.NotFound
                }
            }
        }
    }
}
