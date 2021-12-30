import EntryRepository from 'api/entry/entryRepository'
import Button from 'components/atoms/button/button'
import Plus from '../../../components/atoms/button/plus.svg'
import CommentForm from 'components/comment/commentform/commentform'
import EntryComponent, {
  convertEntryToProps,
  EntryProps,
} from 'components/entry/entry'
import TwoColumn from 'components/template/twocolumn/twocolumn'
import { useState } from 'react'

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
  const [showCommentForm, updateShowCommentForm] = useState(false)
  const entryForm = (_showCommentForm: boolean) => {
    if (_showCommentForm) {
      return (
        <CommentForm
          onSubmit={(name, body) => {
            updateShowCommentForm(false)
          }}
          onCancel={() => updateShowCommentForm(false)}
        />
      )
    } else {
      return (
        <Button
          text="COMMENT"
          Icon={Plus}
          onClick={() => updateShowCommentForm(true)}
        />
      )
    }
  }

  return (
    <TwoColumn>
      <>
        <EntryComponent props={entry} />
        {entryForm(showCommentForm)}
      </>
    </TwoColumn>
  )
}
