package com.github.hmiyado.service.articles

import com.github.hmiyado.model.Entry
import com.github.hmiyado.repository.articles.EntryRepository
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify

class EntriesServiceImplTest : DescribeSpec() {
    @MockK
    lateinit var entryRepository: EntryRepository
    private lateinit var service: EntriesService

    override fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        MockKAnnotations.init(this, relaxUnitFun = true)
        service = EntriesServiceImpl(entryRepository)
    }

    init {

        describe("getArticles") {
            it("should return articles") {
                val articles = listOf(Entry(1, "title 1", "body 1"))
                every { entryRepository.getEntries() } returns articles
                service.getEntries() shouldBe articles
            }
        }

        describe("createArticle") {
            it("should create an article") {
                val article = Entry(1, "title 1", "body 1")
                every { entryRepository.createEntry(any(), any()) } returns article
                val createdArticle = service.createEntry("title 1", "body 1")
                createdArticle shouldBe article
            }
        }

        describe("getArticle") {
            it("should return Article") {
                val article = Entry(1, "title 1", "body 1")
                every { entryRepository.getEntry(1) } returns article
                val actual = service.getEntry(1)
                actual shouldBe article
            }
            it("should return null") {
                every { entryRepository.getEntry(any()) } returns null
                val actual = service.getEntry(1)
                actual shouldBe null
            }
        }

        describe("updateArticle") {
            it("should return Article") {
                val article = Entry(1, "title 1", "body 1")
                every { entryRepository.updateEntry(1, "title 1", "body 1") } returns article
                val actual = service.updateEntry(1, "title 1", "body 1")
                actual shouldBe article
            }
            it("should return null") {
                every { entryRepository.updateEntry(any(), any(), any()) } returns null
                val actual = service.updateEntry(1, "title1", "body1")
                actual shouldBe null
            }
        }

        describe("deleteArticle") {
            it("should delete an article") {
                every { entryRepository.deleteEntry(1) } just Runs
                service.deleteEntry(1)
                verify { entryRepository.deleteEntry(1) }
            }
        }
    }

}
