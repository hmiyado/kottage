import SegmentedButton from 'components/pieces/segmentedbutton/segmentedbutton'
import Sentence from 'components/pieces/sentence/sentence'
import { useState } from 'react'
import Button from '../../pieces/button/button'
import TextArea from '../../pieces/textarea/textarea'
import TextField from '../../pieces/textfield/textfiled'

export const isEmptyOrBlank = (str: string) => str.match(/^\s*$/) !== null

const styles = {
  container: ['flex flex-wrap flex-col space-y-0.5'].join(' '),
  footer: ['flex flex-row flex-wrap justify-between'].join(' '),
  previewArea: [
    'bg-background-light-surface-overlay dark:bg-background-dark-surface-overlay',
    'rounded-t',
    'px-1.0',
  ].join(' '),
}

type EditMode =
  | {
      id: 'edit'
      label: '編集'
    }
  | {
      id: 'preview'
      label: 'プレビュー'
    }

export default function EntryForm({
  onSubmit,
  onCancel,
}: {
  onSubmit: (title: string, body: string) => void
  onCancel: () => void
}): React.JSX.Element {
  const [title, updateTitle] = useState('')
  const [body, updateBody] = useState('')
  const [editMode, updateEditMode] = useState<EditMode>({
    id: 'edit',
    label: '編集',
  })
  const submittable = !isEmptyOrBlank(title) && !isEmptyOrBlank(body)

  const editEntry = () => {
    return (
      <>
        <TextField
          label="タイトル"
          assistiveText={null}
          value={title}
          onChange={(e) => {
            const newTitle = e.target.value
            updateTitle(newTitle)
          }}
        />
        <TextArea
          label="本文"
          value={body}
          onChange={(e) => {
            const newBody = e.currentTarget.value
            updateBody(newBody)
          }}
        />
      </>
    )
  }
  const previewEntry = () => {
    return (
      <div className={styles.previewArea}>
        <Sentence title={title}>{body}</Sentence>
      </div>
    )
  }

  return (
    <div className={styles.container}>
      <SegmentedButton<EditMode>
        name={'editMode'}
        segments={[
          { id: 'edit', label: '編集' },
          { id: 'preview', label: 'プレビュー' },
        ]}
        defaultSegmentId={'edit'}
        onSelectedSegment={(segment) => updateEditMode(segment)}
      />
      {editMode.id === 'edit' ? editEntry() : previewEntry()}
      <div className={styles.footer}>
        <Button
          text="投稿する"
          onClick={() => onSubmit(title, body)}
          disabled={!submittable}
        />
        <Button text="キャンセル" onClick={onCancel} />
      </div>
    </div>
  )
}
