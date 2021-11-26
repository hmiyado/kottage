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

  test('can get an entry', async () => {
    const actual = await EntryRepository.fetchEntry(1)

    expect(actual).toStrictEqual({
      serialNumber: 1,
      title: 'title',
      body: 'body',
      dateTime: actual.dateTime,
      author: {
        id: 1,
        screenName: 'test',
      },
    })
  })
})
