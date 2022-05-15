import UserContext from 'context/user'
import { useContext, useEffect, useState } from 'react'
import useSWR, { useSWRConfig } from 'swr'
import UserRepository from 'repository/user/userRepository'
import SignInForm from './signinform/signinform'
import SignOutForm from './signoutform/signoutform'

export type UserFormProps = {
  onSignUpClicked: (id: string, password: string) => void
}

type NextAction =
  | {
      type: 'signIn'
      id: string
      password: string
    }
  | { type: 'signOut' }
  | { type: 'currentUser' }

export default function UserForm({
  onSignUpClicked,
}: UserFormProps): JSX.Element {
  const [nextAction, setNextAction] = useState<NextAction>({
    type: 'currentUser',
  })
  const shouldSignIn = nextAction.type === 'signIn'
  const shouldSignOut = nextAction.type === 'signOut'
  const { user, updateUser } = useContext(UserContext)
  const { mutate } = useSWRConfig()
  const { error: signOutError } = useSWR(
    shouldSignOut ? 'signOut' : null,
    UserRepository.signOut
  )
  const { data: currentUser } = useSWR(
    nextAction.type === 'currentUser' ? 'currentUser' : null,
    UserRepository.current,
    {
      shouldRetryOnError: false,
      fallbackData: null,
    }
  )
  const { data: signInUser, error: signInError } = useSWR(
    shouldSignIn ? nextAction : null,
    ({ id, password }) => UserRepository.signIn(id, password),
    {
      shouldRetryOnError: false,
    }
  )
  useEffect(() => {
    console.log(`nextAction: ${JSON.stringify(nextAction)}`)
    if (signInUser && !signInError) {
      console.log(`${signInUser}`)
      setNextAction({ type: 'currentUser' })
      mutate('currentUser', signInUser)
      updateUser(signInUser)
      return
    }
    if (shouldSignOut && !signOutError) {
      setNextAction({ type: 'currentUser' })
      mutate('currentUser', null)
      updateUser(null)
      return
    }
    updateUser(currentUser || null)
  }, [
    shouldSignOut,
    mutate,
    updateUser,
    currentUser,
    signOutError,
    signInUser,
    nextAction,
    signInError,
  ])

  return user?.screenName ? (
    <SignOutForm
      screenName={user.screenName}
      onSignOutClicked={() => setNextAction({ type: 'signOut' })}
    />
  ) : (
    <SignInForm
      onSignInClicked={(id, password) =>
        setNextAction({ type: 'signIn', id, password })
      }
      onSignUpClicked={onSignUpClicked}
    />
  )
}
