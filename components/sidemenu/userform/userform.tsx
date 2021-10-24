import SignInForm from '../signinform/signinform'
import SignOutForm from '../signoutform/signoutform'

export default function UserForm({
  screenName,
  onSignUpClicked,
  onSignInClicked,
  onSignOutClicked,
}: {
  screenName?: string
  onSignUpClicked: (id: string, password: string) => void
  onSignInClicked: (id: string, password: string) => void
  onSignOutClicked: () => void
}): JSX.Element {
  return screenName ? (
    <SignOutForm screenName={screenName} onSignOutClicked={onSignOutClicked} />
  ) : (
    <SignInForm
      onSignInClicked={onSignInClicked}
      onSignUpClicked={onSignUpClicked}
    />
  )
}
