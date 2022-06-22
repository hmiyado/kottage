import UserContext from 'context/user'
import { useContext, useEffect } from 'react'
import UserRepository from 'repository/user/userRepository'
import useSWR, { useSWRConfig } from 'swr'

export type UserHookAction =
  | { type: 'currentUser' }
  | {
      type: 'signIn' | 'signUp'
      id: string
      password: string
    }
  | { type: 'signOut' }

export function useCurrentUser(action: UserHookAction) {
  const { data: currentUser } = useSWR(
    action.type === 'currentUser' ? 'currentUser' : null,
    UserRepository.current,
    {
      shouldRetryOnError: false,
      fallbackData: null,
    }
  )

  const { updateUser } = useContext(UserContext)
  useEffect(() => {
    updateUser(currentUser || null)
  })
}

export function useSignUp(
  action: UserHookAction,
  setNextAction: (action: UserHookAction) => void
) {
  const { data: signUpUser, error: signUpError } = useSWR(
    action.type === 'signUp' ? action : null,
    ({ id, password }) => UserRepository.signUp(id, password),
    {
      shouldRetryOnError: false,
    }
  )

  const { updateUser } = useContext(UserContext)
  const { mutate } = useSWRConfig()
  useEffect(() => {
    if (signUpUser && !signUpError) {
      setNextAction({ type: 'currentUser' })
      mutate('currentUser', signUpUser)
      updateUser(signUpUser)
    }
  })
}

export function useSignIn(
  action: UserHookAction,
  setNextAction: (action: UserHookAction) => void
) {
  const { data: signInUser, error: signInError } = useSWR(
    action.type === 'signIn' ? action : null,
    ({ id, password }) => UserRepository.signIn(id, password),
    {
      shouldRetryOnError: false,
    }
  )
  const { updateUser } = useContext(UserContext)
  const { mutate } = useSWRConfig()
  useEffect(() => {
    if (signInUser && !signInError) {
      setNextAction({ type: 'currentUser' })
      mutate('currentUser', signInUser)
      updateUser(signInUser)
    }
  })
}

export function useSignOut(
  action: UserHookAction,
  setNextAction: (action: UserHookAction) => void
) {
  const { data, error } = useSWR(
    action.type === 'signOut' ? 'signOut' : null,
    () =>
      UserRepository.signOut().then(() => {
        return {}
      })
  )
  const { updateUser } = useContext(UserContext)
  const { mutate } = useSWRConfig()
  useEffect(() => {
    if (data && !error) {
      setNextAction({ type: 'currentUser' })
      mutate('currentUser', null)
      updateUser(null)
    }
  })
}
