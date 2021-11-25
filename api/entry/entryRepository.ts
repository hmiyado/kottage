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

const fetchEntry = async (serialNumber: number) => {
  return KottageClient.entriesSerialNumberGet({serialNumber})
}

const EntryRepository = {
  createEntry,
  getEntries,
  fetchEntry,
}

export default EntryRepository
