import { isEmptyOrBlank } from 'components/plurals/entryform/entryform'
import { useState } from 'react'
import Button from '../../../pieces/button/button'
import TextArea from '../../../pieces/textarea/textarea'
import TextField from '../../../pieces/textfield/textfiled'
import styles from './commentform.module.css'

export default function CommentForm({
  onSubmit,
}: {
  onSubmit: (name: string, body: string) => Promise<void>
}): JSX.Element {
  const [name, updateTitle] = useState('')
  const [body, updateBody] = useState('')
  const [submittig, updateSubmitting] = useState(false)
  const submittable =
    !isEmptyOrBlank(name) && !isEmptyOrBlank(body) && !submittig

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
            onSubmit(name, body)
              .then(() => {
                updateBody('')
              })
              .catch(() => {
                // do nothing
              })
              .finally(() => {
                updateSubmitting(false)
              })
          }}
          disabled={!submittable}
        />
      </div>
    </div>
  )
}
