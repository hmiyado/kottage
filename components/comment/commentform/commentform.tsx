import { isEmptyOrBlank } from 'components/entryform/entryform'
import { useState } from 'react'
import Button from '../../pieces/button/button'
import TextArea from '../../pieces/textarea/textarea'
import TextField from '../../pieces/textfield/textfiled'
import styles from './commentform.module.css'

export default function CommentForm({
  onSubmit,
  onCancel,
}: {
  onSubmit: (name: string, body: string) => void
  onCancel: () => void
}): JSX.Element {
  const [name, updateTitle] = useState('')
  const [body, updateBody] = useState('')
  const submittable = !isEmptyOrBlank(name) && !isEmptyOrBlank(body)

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
          onClick={() => onSubmit(name, body)}
          disabled={!submittable}
        />
        <Button text="CANCEL" onClick={onCancel} />
      </div>
    </div>
  )
}
