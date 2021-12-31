import { useState } from 'react'
import Button from '../pieces/button/button'
import TextArea from '../pieces/textarea/textarea'
import TextField from '../pieces/textfield/textfiled'
import styles from './entryform.module.css'

export const isEmptyOrBlank = (str: string) => str.match(/^\s*$/) !== null

export default function EntryForm({
  onSubmit,
  onCancel,
}: {
  onSubmit: (title: string, body: string) => void
  onCancel: () => void
}): JSX.Element {
  const [title, updateTitle] = useState('')
  const [body, updateBody] = useState('')
  const submittable = !isEmptyOrBlank(title) && !isEmptyOrBlank(body)

  return (
    <div className={styles.container}>
      <TextField
        label="Title"
        assistiveText={null}
        value={title}
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
          onClick={() => onSubmit(title, body)}
          disabled={!submittable}
        />
        <Button text="CANCEL" onClick={onCancel} />
      </div>
    </div>
  )
}
