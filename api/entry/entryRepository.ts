import KottageClient from '../kottageClient'

const createEntry = async (title: string, body: string) => {
  return KottageClient.entriesPost({
    entriesPostRequest: {
      title,
      body,
    },
  })
}

const getEntries = async () => {
  return KottageClient.entriesGet()
}

const EntryRepository = {
  createEntry,
  getEntries,
}

export default EntryRepository
