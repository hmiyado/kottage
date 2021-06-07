package com.github.hmiyado.kottage.service.entries

import com.github.hmiyado.kottage.model.Entry
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.repository.entries.EntryRepository
import io.kotest.assertions.throwables.shouldThrow
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

        describe("getEntries") {
            it("should return entries") {
                val entries = listOf(Entry(1, "title 1", "body 1"))
                every { entryRepository.getEntries() } returns entries
                service.getEntries() shouldBe entries
            }
        }

        describe("createEntry") {
            it("should create an entry") {
                val entry = Entry(1, "title 1", "body 1")
                every { entryRepository.createEntry(any(), any(), any()) } returns entry
                val createdEntry = service.createEntry("title 1", "body 1", 1)
                createdEntry shouldBe entry
            }
        }

        describe("getEntry") {
            it("should return an entry") {
                val entry = Entry(1, "title 1", "body 1")
                every { entryRepository.getEntry(1) } returns entry
                val actual = service.getEntry(1)
                actual shouldBe entry
            }
            it("should return null") {
                every { entryRepository.getEntry(any()) } returns null
                val actual = service.getEntry(1)
                actual shouldBe null
            }
        }

        describe("updateEntry") {
            it("should return an entry") {
                val user = User(id = 99)
                val entry = Entry(1, "title 1", "body 1", author = user)
                every { entryRepository.updateEntry(1, user.id, "title 1", "body 1") } returns entry
                val actual = service.updateEntry(1, user.id, "title 1", "body 1")
                actual shouldBe entry
            }
            it("should throw ${EntriesService.NoSuchEntryException::class.simpleName}") {
                every { entryRepository.updateEntry(any(), any(), any(), any()) } returns null
                every { entryRepository.getEntry(any()) } returns null
                shouldThrow<EntriesService.NoSuchEntryException> {
                    service.updateEntry(1, 1L, "title1", "body1")
                }
            }
            it("should throw ${EntriesService.ForbiddenOperationException::class.simpleName}") {
                every { entryRepository.updateEntry(any(), any(), any(), any()) } returns null
                every { entryRepository.getEntry(any()) } returns Entry()
                shouldThrow<EntriesService.ForbiddenOperationException> {
                    service.updateEntry(1, 1L, "title1", "body1")
                }
            }
        }

        describe("deleteEntry") {
            it("should delete an entry") {
                every { entryRepository.deleteEntry(1) } just Runs
                service.deleteEntry(1)
                verify { entryRepository.deleteEntry(1) }
            }
        }
    }

}
