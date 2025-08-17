import { afterEach, expect, test, vi } from 'vitest'
import { cleanup, fireEvent, render, screen } from '@testing-library/react'
import EntryForm, { isEmptyOrBlank } from './entryform'

const titleForm = () => screen.getByLabelText('タイトル')
const bodyForm = () => screen.getByLabelText('本文')
const submitButton = () => screen.getByText('投稿する')
const cancelButton = () => screen.getByText('キャンセル')
const editSegmentedButton = () => screen.getByText('編集')
const previewSegmentedButton = () => screen.getByText('プレビュー')

afterEach(() => {
  cleanup()
})

test('should empty or blank', () => {
  expect(isEmptyOrBlank('')).toBe(true)
  expect(isEmptyOrBlank(' ')).toBe(true)
  expect(isEmptyOrBlank('\t')).toBe(true)
  expect(isEmptyOrBlank('aaa')).toBe(false)
})

test('should let submitButton disabled initially', () => {
  const mockSubmit = vi.fn(() => {
    // empty
  })
  const mockCancel = vi.fn(() => {
    // empty
  })
  render(<EntryForm onSubmit={mockSubmit} onCancel={mockCancel} />)
  expect(submitButton()).toBeDisabled()
})

test.each`
  title      | body      | submittable
  ${''}      | ${''}     | ${false}
  ${' '}     | ${' '}    | ${false}
  ${'title'} | ${''}     | ${false}
  ${'title'} | ${' '}    | ${false}
  ${''}      | ${'body'} | ${false}
  ${' '}     | ${'body'} | ${false}
  ${'title'} | ${'body'} | ${true}
`(
  'should be submittalbe=$submittable when title=$title body=$body',
  ({ title, body, submittable }) => {
    render(
      <EntryForm
        onSubmit={function () {
          // empty
        }}
        onCancel={function () {
          // empty
        }}
      />,
    )
    fireEvent.change(titleForm(), { target: { value: title } })
    fireEvent.change(bodyForm(), { target: { value: body } })

    if (submittable) {
      expect(submitButton()).toBeEnabled()
    } else {
      expect(submitButton()).toBeDisabled()
    }
  },
)

test('should get title and body when submit', () => {
  const mockSubmit = vi.fn((title: string, body: string): string => {
    return `submit with ${title} ${body}`
  })
  const mockCancel = vi.fn((): string => {
    return `cancel`
  })
  render(<EntryForm onSubmit={mockSubmit} onCancel={mockCancel} />)
  fireEvent.change(titleForm(), { target: { value: 'title' } })
  fireEvent.change(bodyForm(), { target: { value: 'body' } })

  fireEvent.click(cancelButton())
  expect(mockCancel).toBeCalled()
  fireEvent.click(submitButton())
  expect(mockSubmit).toHaveReturnedWith('submit with title body')
})

test('should be edit mode by default', () => {
  render(
    <EntryForm
      onSubmit={function () {
        // empty
      }}
      onCancel={function () {
        // empty
      }}
    />,
  )
  expect(editSegmentedButton()).toBeEnabled()
})

test('should switch to preview mode', () => {
  const { container } = render(
    <EntryForm
      onSubmit={function () {
        // empty
      }}
      onCancel={function () {
        // empty
      }}
    />,
  )
  fireEvent.change(titleForm(), { target: { value: 'title' } })
  fireEvent.change(bodyForm(), { target: { value: 'body' } })

  fireEvent.click(previewSegmentedButton())
  expect(previewSegmentedButton()).toBeEnabled()
  expect(screen.findByText('title')).toBeTruthy()
  expect(screen.findByText('body')).toBeTruthy()
})
