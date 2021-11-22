import SignInForm, { SignInFormProps } from './signinform/signinform'
import SignOutForm, { SignOutFormProps } from './signoutform/signoutform'

export type UserFormProps =
  | (SignInFormProps & SignOutFormProps)
  | (SignInFormProps & OptionalSignOutProps)

type OptionalSignOutProps = {
  screenName?: string
  onSignOutClicked: () => Promise<void>
}

export default function UserForm({
  screenName,
  onSignUpClicked,
  onSignInClicked,
  onSignOutClicked,
}: UserFormProps): JSX.Element {
  return screenName ? (
    <SignOutForm screenName={screenName} onSignOutClicked={onSignOutClicked} />
  ) : (
    <SignInForm
      onSignInClicked={onSignInClicked}
      onSignUpClicked={onSignUpClicked}
    />
  )
}
