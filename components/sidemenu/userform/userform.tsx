import SignInForm, { SignInFormProps } from './signinform/signinform'
import SignOutForm, { SignOutFormProps } from './signoutform/signoutform'

type OptionalSignOutProps = {
  screenName?: string
  onSignOutClicked: () => void
}

export default function UserForm({
  screenName,
  onSignUpClicked,
  onSignInClicked,
  onSignOutClicked,
}:
  | (SignInFormProps & SignOutFormProps)
  | (SignInFormProps & OptionalSignOutProps)): JSX.Element {
  return screenName ? (
    <SignOutForm screenName={screenName} onSignOutClicked={onSignOutClicked} />
  ) : (
    <SignInForm
      onSignInClicked={onSignInClicked}
      onSignUpClicked={onSignUpClicked}
    />
  )
}
