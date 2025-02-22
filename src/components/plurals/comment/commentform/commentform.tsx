import { isEmptyOrBlank } from 'components/plurals/entryform/entryform'
import { useState } from 'react'
import EntryRepository from 'repository/entry/entryRepository'
import useSWR, { useSWRConfig } from 'swr'
import Button from '../../../pieces/button/button'
import TextArea from '../../../pieces/textarea/textarea'
import TextField from '../../../pieces/textfield/textfiled'
import styles from './commentform.module.css'

export default function CommentForm({
  entrySerialNumber,
}: {
  entrySerialNumber: number
}): React.JSX.Element {
  const [name, updateTitle] = useState('')
  const [body, updateBody] = useState('')
  const [submittig, updateSubmitting] = useState(false)
  const submittable =
    !isEmptyOrBlank(name) && !isEmptyOrBlank(body) && !submittig

  const { mutate } = useSWRConfig()
  useSWR(
    submittig ? `POST entries/${entrySerialNumber}/comments` : null,
    async () => {
      try {
        await EntryRepository.createComment(entrySerialNumber, name, body)
        updateBody('')
      } catch (e: any) {
        // do nothing
      } finally {
        mutate(`GET entries/${entrySerialNumber}/comments`)
        updateSubmitting(false)
      }
    },
  )

  return (
    <div className={styles.container}>
      <TextField
        label="Name"
        assistiveText={null}
        value={name}
        onChange={(e) => {
          const newTitle = e.target.value
          updateTitle(newTitle)
        }}
      />
      <TextArea
        label="Body"
        value={body}
        onChange={(e) => {
          const newBody = e.currentTarget.value
          updateBody(newBody)
        }}
      />
      <div className={styles.footer}>
        <Button
          text="SUBMIT"
          onClick={() => {
            updateSubmitting(true)
          }}
          disabled={!submittable}
        />
      </div>
    </div>
  )
}
