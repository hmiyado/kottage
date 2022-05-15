import UserContext from 'context/user'
import { useContext, useEffect, useState } from 'react'
import useSWR, { useSWRConfig } from 'swr'
import UserRepository from 'repository/user/userRepository'
import SignInForm from './signinform/signinform'
import SignOutForm from './signoutform/signoutform'

type NextAction =
  | {
      type: 'signIn' | 'signUp'
      id: string
      password: string
    }
  | { type: 'signOut' }
  | { type: 'currentUser' }

export default function UserForm(): JSX.Element {
  const [nextAction, setNextAction] = useState<NextAction>({
    type: 'currentUser',
  })
  const shouldSignUp = nextAction.type === 'signUp'
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
  const { data: signUpUser, error: signUpError } = useSWR(
    shouldSignUp ? nextAction : null,
    ({ id, password }) => UserRepository.signUp(id, password),
    {
      shouldRetryOnError: false,
    }
  )
  useEffect(() => {
    if (signUpUser && !signUpError) {
      setNextAction({ type: 'currentUser' })
      mutate('currentUser', signUpUser)
      updateUser(signUpUser)
      return
    }
    if (signInUser && !signInError) {
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
    signUpUser,
    signUpError,
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
      onSignUpClicked={(id, password) =>
        setNextAction({ type: 'signUp', id, password })
      }
    />
  )
}
