import { useContext, useEffect, useState } from 'react'
import UserContext, { User } from '../../../context/user'
import styles from './twocolumn.module.css'
import Layout from '../layout/layout'
import UserRepository, { Sign } from 'api/user/userRepository'
import SideMenu from 'components/sidemenu/sidemenu'

export default function TwoColumn({ children }: { children: JSX.Element }) {
  const { user, updateUser } = useContext(UserContext)

  useEffect(() => {
    UserRepository.current()
      .then((currentUser) => updateUser(currentUser))
      .catch(() => updateUser(null))
  }, [])

  return (
    <Layout>
      <div className={styles.container}>
        <main className={styles.mainColumn}>{children}</main>
        <SideMenu
          className={styles.sideColumn}
          user={user}
          onSignInClicked={signAndUpdateUser(UserRepository.signUp, updateUser)}
          onSignUpClicked={signAndUpdateUser(UserRepository.signUp, updateUser)}
          onSignOutClicked={signOut(updateUser)}
        />
      </div>
    </Layout>
  )
}

const signAndUpdateUser = (
  sign: Sign,
  updateUser: (newUser: User) => void
): ((id: string, password: string) => Promise<void>) => {
  return async (id, password) => {
    try {
      const user = await sign(id, password)
      updateUser(user)
    } catch {
      updateUser(null)
    }
  }
}

const signOut = (
  updateUser: (newUser: User) => void
): (() => Promise<void>) => {
  return async () => {
    await UserRepository.signOut()
    updateUser(null)
  }
}
