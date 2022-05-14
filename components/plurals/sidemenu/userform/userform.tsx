import UserContext from 'context/user'
import { useContext, useEffect, useState } from 'react'
import useSWR, { useSWRConfig } from 'swr'
import UserRepository from 'repository/user/userRepository'
import SignInForm, { SignInFormProps } from './signinform/signinform'
import SignOutForm from './signoutform/signoutform'

export type UserFormProps = SignInFormProps

type NextAction = 'signOut' | null

export default function UserForm({
  onSignUpClicked,
  onSignInClicked,
}: UserFormProps): JSX.Element {
  const [nextAction, setNextAction] = useState<NextAction>(null)
  const shouldSignOut = nextAction === 'signOut'
  const { user, updateUser } = useContext(UserContext)
  const { mutate } = useSWRConfig()
  const { error } = useSWR(
    shouldSignOut ? 'signOut' : null,
    UserRepository.signOut
  )
  const { data } = useSWR(
    shouldSignOut ? null : 'currentUser',
    UserRepository.current,
    {
      shouldRetryOnError: false,
      fallbackData: null,
    }
  )
  useEffect(() => {
    if (shouldSignOut && !error) {
      setNextAction(null)
      mutate('currentUser', null)
      updateUser(null)
      return
    }
    updateUser(data || null)
  }, [error, shouldSignOut, mutate, updateUser, data])

  return user?.screenName ? (
    <SignOutForm
      screenName={user.screenName}
      onSignOutClicked={() => setNextAction('signOut')}
    />
  ) : (
    <SignInForm
      onSignInClicked={onSignInClicked}
      onSignUpClicked={onSignUpClicked}
    />
  )
}
