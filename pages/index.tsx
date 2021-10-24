import Layout from '../components/layout/layout'
import styles from './rootpage.module.css'
import Profile from '../components/sidemenu/profile/profile'
import UserContext, { User } from '../context/user'
import UserForm from '../components/sidemenu/userform/userform'
import UserRepository, { Sign } from '../api/user/userRepository'

export default function RootPage() {
  return (
    <UserContext.Consumer>
      {({ user, updateUser }) => (
        <Layout>
          <div className={styles.container}>
            <div className={styles.mainColumn}>temporally content</div>
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
                onSignOutClicked={() => {}}
              />
            </div>
          </div>
        </Layout>
      )}
    </UserContext.Consumer>
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
