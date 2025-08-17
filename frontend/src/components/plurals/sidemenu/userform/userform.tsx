import UserContext from 'context/user'
import { useContext, useState } from 'react'
import SignInForm from './signinform/signinform'
import SignOutForm from './signoutform/signoutform'
import {
  useCurrentUser,
  UserHookAction,
  useSignIn,
  useSignOut,
  useSignUp,
} from './userformHooks'

export default function UserForm(): React.JSX.Element {
  const [nextAction, setNextAction] = useState<UserHookAction>({
    type: 'currentUser',
  })
  const { user } = useContext(UserContext)
  useCurrentUser(nextAction)
  useSignUp(nextAction, setNextAction)
  useSignIn(nextAction, setNextAction)
  useSignOut(nextAction, setNextAction)

  return user?.screenName ? (
    <SignOutForm
      screenName={user.screenName}
      accountLinks={user.accountLinks}
      onSignOutClicked={() => setNextAction({ type: 'signOut' })}
    />
  ) : (
    <SignInForm
      onSignInClicked={(id, password) =>
        setNextAction({ type: 'signIn', id, password })
      }
      onSignUpClicked={(id, password) =>
        setNextAction({ type: 'signUp', id, password })
      }
    />
  )
}
