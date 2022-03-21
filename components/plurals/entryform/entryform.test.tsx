import { fireEvent, render, screen } from '@testing-library/react'
import EntryForm, { isEmptyOrBlank } from './entryform'

const titleForm = () => screen.getByLabelText('Title')
const bodyForm = () => screen.getByLabelText('Body')
const submitButton = () => screen.getByText('SUBMIT')
const cancelButton = () => screen.getByText('CANCEL')

test('should empty or blank', () => {
  expect(isEmptyOrBlank('')).toBe(true)
  expect(isEmptyOrBlank(' ')).toBe(true)
  expect(isEmptyOrBlank('\t')).toBe(true)
  expect(isEmptyOrBlank('aaa')).toBe(false)
})

test('should let submitButton disabled initially', () => {
  const mockSubmit = jest.fn(() => {
    // empty
  })
  const mockCancel = jest.fn(() => {
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
      />
    )
    fireEvent.change(titleForm(), { target: { value: title } })
    fireEvent.change(bodyForm(), { target: { value: body } })

    if (submittable) {
      expect(submitButton()).toBeEnabled()
    } else {
      expect(submitButton()).toBeDisabled()
    }
  }
)

test('should get title and body when submit', () => {
  const mockSubmit = jest.fn((title: string, body: string): string => {
    return `submit with ${title} ${body}`
  })
  const mockCancel = jest.fn((): string => {
    return `cancel`
  })
  render(<EntryForm onSubmit={mockSubmit} onCancel={mockCancel} />)
  fireEvent.change(titleForm(), { target: { value: 'title' } })
  fireEvent.change(bodyForm(), { target: { value: 'body' } })

  fireEvent.click(cancelButton())
  expect(mockCancel).toHaveReturnedWith('cancel')
  fireEvent.click(submitButton())
  expect(mockSubmit).toHaveReturnedWith('submit with title body')
})
