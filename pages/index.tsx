import Layout from '../components/layout/layout'
import styles from './rootpage.module.css'
import Profile from '../components/sidemenu/profile/profile'
import UserContext, { User } from '../context/user'
import UserForm from '../components/sidemenu/userform/userform'
import UserRepository, { Sign } from '../api/user/userRepository'
import { useContext, useEffect } from 'react'
import Button from '../components/atoms/button/button'
import Plus from '../components/atoms/button/plus.svg'

export default function RootPage() {
  const { user, updateUser } = useContext(UserContext)
  useEffect(() => {
    UserRepository.current()
      .then((currentUser) => updateUser(currentUser))
      .catch(() => updateUser(null))
  }, [])

  return (
    <Layout>
      <div className={styles.container}>
        <div className={styles.mainColumn}>
          <Button text="Entry" Icon={Plus} />
        </div>
        <div className={styles.sideColumn}>
          <Profile />

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
        </div>
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
