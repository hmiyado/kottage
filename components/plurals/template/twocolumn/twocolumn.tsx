import { useContext } from 'react'
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
  const { updateUser } = useContext(UserContext)

  return (
    <Layout>
      <div className={styles.container}>
        <main className={styles.mainColumn + ' ' + mainColumnClassName}>
          {children}
        </main>
        <SideMenu
          className={styles.sideColumn}
          onSignUpClicked={signAndUpdateUser(UserRepository.signUp, updateUser)}
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
