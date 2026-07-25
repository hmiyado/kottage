import KottageClient from '../kottageClient'

const createEntry = async (title: string, body: string) => {
  return KottageClient.entriesPost({
    entriesPostRequest: {
      title,
      body,
    },
  })
}

const getEntries = async (limit: number = 20, offset: number = 0) => {
  return KottageClient.entriesGet({
    limit,
    offset,
  })
}

const fetchEntry = async (serialNumber: number) => {
  return KottageClient.entriesSerialNumberGet({ serialNumber })
}

const deleteEntry = async (serialNumber: number) => {
  return KottageClient.entriesSerialNumberDelete({ serialNumber })
}

const createComment = async (
  serialNumber: number,
  name: string,
  body: string
) => {
  return KottageClient.entriesSerialNumberCommentsPost({
    serialNumber,
    commentsPostRequest: {
      name,
      body,
    },
  })
}

const fetchComments = async (serialNumber: number) => {
  return KottageClient.entriesSerialNumberCommentsGet({ serialNumber })
}

const EntryRepository = {
  createEntry,
  getEntries,
  fetchEntry,
  deleteEntry,
  createComment,
  fetchComments,
}

export default EntryRepository
