import EntryRepository from 'api/entry/entryRepository'
import EntryComponent, {
  convertEntryToProps,
  EntryProps,
} from 'components/plurals/entry/entry'
import TwoColumn from 'components/plurals/template/twocolumn/twocolumn'
import { useEffect, useState } from 'react'
import { Comments as OpenApiComments } from 'api/openapi/generated'
import commentsStyle from './comments.module.css'
import CommentList from 'components/plurals/comment/commentlist/commentlist'

export async function getStaticPaths() {
  const entries = await EntryRepository.getEntries()
  const serialNumbers = entries.items?.map((v) => {
    return { params: { serialNumber: v.serialNumber.toString() } }
  })

  return {
    paths: serialNumbers ? serialNumbers : [],
    fallback: false,
  }
}

export async function getStaticProps({
  params,
}: {
  params: { serialNumber: number }
}) {
  const entry = await EntryRepository.fetchEntry(params.serialNumber)

  return {
    props: {
      entry: convertEntryToProps(entry),
    },
  }
}

export default function EntriesSerialNumberPage({
  entry,
}: {
  entry: EntryProps
}) {
  const [comments, updateComments] = useState<OpenApiComments>({
    totalCount: 0,
    items: [],
  })

  useEffect(() => {
    EntryRepository.fetchComments(entry.serialNumber)
      .then((fetchedComments) => {
        updateComments(fetchedComments)
      })
      .catch(() => {
        /* do nothing */
      })
  }, [entry.serialNumber, updateComments])

  const onSubmit = (name: string, body: string) => {
    EntryRepository.createComment(entry.serialNumber, name, body)
      .then((comment) =>
        updateComments({
          totalCount: comments.totalCount + 1,
          items: comments.items.concat([comment]),
        })
      )
      .catch(() => {
        /* do nothing */
      })
  }

  return (
    <TwoColumn>
      <>
        <EntryComponent props={{ ...entry, className: commentsStyle.entry }} />
        <CommentList comments={comments} onSubmit={onSubmit} />
      </>
    </TwoColumn>
  )
}
