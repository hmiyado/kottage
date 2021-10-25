import { fireEvent, render, screen } from '@testing-library/react'
import SignInForm from './signinform'

test.each`
  id      | password      | disabled
  ${''}   | ${''}         | ${true}
  ${'id'} | ${''}         | ${true}
  ${''}   | ${'password'} | ${true}
  ${'id'} | ${'password'} | ${false}
`(
  'should be disabled=$disabled when id=$id password=$password',
  ({ id, password, disabled }) => {
    render(
      <SignInForm
        onSignInClicked={() => {
          /* empty */
        }}
        onSignUpClicked={() => {
          /* empty */
        }}
      />
    )
    fireEvent.change(screen.getByLabelText('ID'), { target: { value: id } })
    fireEvent.change(screen.getByLabelText('Password'), {
      target: { value: password },
    })

    if (disabled) {
      expect(screen.getByText('SIGN IN')).toBeDisabled
      expect(screen.getByText('SIGN UP')).toBeDisabled
    } else {
      expect(screen.getByText('SIGN IN')).toBeEnabled
      expect(screen.getByText('SIGN UP')).toBeEnabled
    }
  }
)

test('should get id and password when sign in', () => {
  const mockSignInClicked = jest.fn((id: string, password: string): string => {
    return `sign in ${id} ${password}`
  })
  const mockSignUpClicked = jest.fn((id: string, password: string): string => {
    return `sign up ${id} ${password}`
  })
  render(
    <SignInForm
      onSignInClicked={mockSignInClicked}
      onSignUpClicked={mockSignUpClicked}
    />
  )
  fireEvent.change(screen.getByLabelText('ID'), { target: { value: 'id' } })
  fireEvent.change(screen.getByLabelText('Password'), {
    target: { value: 'password' },
  })

  fireEvent.click(screen.getByText('SIGN IN'))
  expect(mockSignInClicked).toHaveReturnedWith('sign in id password')

  fireEvent.click(screen.getByText('SIGN UP'))
  expect(mockSignUpClicked).toHaveReturnedWith('sign up id password')
})
