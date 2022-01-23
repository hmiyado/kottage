import UserContext from 'context/user'
import { useContext, useEffect } from 'react'
import { UserFromJSONTyped } from 'repository/openapi/generated'
import UserRepository from 'repository/user/userRepository'
import SignInForm, { SignInFormProps } from './signinform/signinform'
import SignOutForm, { SignOutFormProps } from './signoutform/signoutform'

export type UserFormProps =
  | (SignInFormProps & SignOutFormProps)
  | (SignInFormProps & OptionalSignOutProps)

type OptionalSignOutProps = {
  onSignOutClicked: () => Promise<void>
}

export default function UserForm({
  onSignUpClicked,
  onSignInClicked,
  onSignOutClicked,
}: UserFormProps): JSX.Element {
  const { user, updateUser } = useContext(UserContext)

  useEffect(() => {
    UserRepository.current()
      .then((currentUser) => {
        updateUser(currentUser)
      })
      .catch((e) => {
        updateUser(null)
      })
  }, [])

  return user?.screenName ? (
    <SignOutForm
      screenName={user.screenName}
      onSignOutClicked={onSignOutClicked}
    />
  ) : (
    <SignInForm
      onSignInClicked={onSignInClicked}
      onSignUpClicked={onSignUpClicked}
    />
  )
}
