import EntryRepository from './entryRepository'

describe('entryRepository', () => {
  test('can create entry', async () => {
    const actual = await EntryRepository.createEntry('title', 'body')

    expect(actual.title).toBe('title')
    expect(actual.body).toBe('body')
  })

  test('can get entries', async () => {
    const actual = await EntryRepository.getEntries()

    expect(actual.items?.length).toBeGreaterThan(1)
  })
})
