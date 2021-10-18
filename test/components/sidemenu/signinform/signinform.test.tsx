import { fireEvent, render, screen } from '@testing-library/react'
import SignInForm from '../../../../components/sidemenu/signinform/signinform'

test.each`
  id      | password      | disabled
  ${''}   | ${''}         | ${true}
  ${'id'} | ${''}         | ${true}
  ${''}   | ${'password'} | ${true}
  ${'id'} | ${'password'} | ${false}
`(
  'should be disabled=$disabled when id=$id password=$password',
  ({ id, password, disabled }) => {
    render(<SignInForm onSignInClicked={() => {}} />)
    fireEvent.change(screen.getByLabelText('ID'), { target: { value: id } })
    fireEvent.change(screen.getByLabelText('Password'), {
      target: { value: password },
    })

    if (disabled) {
      expect(screen.getByText('SIGN IN')).toBeDisabled
    } else {
      expect(screen.getByText('SIGN IN')).toBeEnabled
    }
  }
)

test('should get id and password when sign in', () => {
  const mockSignInClicked = jest.fn((id: string, password: string): string => {
    console.log('fired')
    return `${id} ${password}`
  })
  render(<SignInForm onSignInClicked={mockSignInClicked} />)
  fireEvent.change(screen.getByLabelText('ID'), { target: { value: 'id' } })
  fireEvent.change(screen.getByLabelText('Password'), {
    target: { value: 'password' },
  })

  fireEvent.click(screen.getByText('SIGN IN'))

  expect(mockSignInClicked).toHaveReturnedWith('id password')
})
