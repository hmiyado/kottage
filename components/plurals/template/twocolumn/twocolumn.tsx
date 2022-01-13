import { useContext, useEffect, useState } from 'react'
import UserContext, { User } from '../../../../context/user'
import styles from './twocolumn.module.css'
import Layout from '../layout/layout'
import UserRepository, { Sign } from 'repository/user/userRepository'
import SideMenu from 'components/plurals/sidemenu/sidemenu'

export default function TwoColumn({
  mainColumnClassName,
  children,
}: {
  mainColumnClassName?: string
  children: JSX.Element
}) {
  const { user, updateUser } = useContext(UserContext)

  useEffect(() => {
    UserRepository.current()
      .then((currentUser) => updateUser(currentUser))
      .catch(() => updateUser(null))
  }, [])

  return (
    <Layout>
      <div className={styles.container}>
        <main className={styles.mainColumn + ' ' + mainColumnClassName}>
          {children}
        </main>
        <SideMenu
          className={styles.sideColumn}
          user={user}
          onSignInClicked={signAndUpdateUser(UserRepository.signIn, updateUser)}
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
