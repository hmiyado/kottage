import { useContext, useEffect, useState } from 'react'
import UserContext, { User } from '../../../context/user'
import styles from './twocolumn.module.css'
import Layout from '../layout/layout'
import Profile from '../../sidemenu/profile/profile'
import UserForm from '../../sidemenu/userform/userform'
import ServiceReference from '../../sidemenu/servicereference/servicereference'
import UserRepository, { Sign } from 'api/user/userRepository'

export default function TwoColumn({ children }: { children: JSX.Element }) {
  const { user, updateUser } = useContext(UserContext)
  const [showUserForm, updateShowUserForm] = useState(false)
  const [theme, setTheme] = useState<'light' | 'dark'>('light')

  useEffect(() => {
    UserRepository.current()
      .then((currentUser) => updateUser(currentUser))
      .catch(() => updateUser(null))
  }, [])

  useEffect(() => {
    const initialThemeMediaQuery = window.matchMedia(
      '(prefers-color-scheme: dark)'
    )
    const setThemeByMediaQuery = (isDark: boolean) => {
      setTheme(isDark ? 'dark' : 'light')
    }
    initialThemeMediaQuery.addEventListener('change', (e) => {
      console.log(e)

      setThemeByMediaQuery(e.matches)
    })
    setThemeByMediaQuery(initialThemeMediaQuery.matches)
  }, [setTheme])

  return (
    <Layout>
      <div className={styles.container}>
        <main className={styles.mainColumn}>{children}</main>
        <aside className={styles.sideColumn}>
          <div onClick={() => updateShowUserForm((pre) => !pre)}>
            <Profile />
          </div>

          {showUserForm ? (
            <UserForm
              screenName={user?.screenName}
              onSignUpClicked={signAndUpdateUser(
                UserRepository.signUp,
                updateUser
              )}
              onSignInClicked={signAndUpdateUser(
                UserRepository.signIn,
                updateUser
              )}
              onSignOutClicked={signOut(updateUser)}
            />
          ) : null}

          <ServiceReference theme={theme} />
        </aside>
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
