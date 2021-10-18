import Layout from '../components/layout/layout'
import SignInForm from '../components/sidemenu/signinform/signinform'
import styles from './rootpage.module.css'
import Profile from '../components/sidemenu/profile/profile'

export default function RootPage() {
  return (
    <Layout>
      <div className={styles.container}>
        <div className={styles.mainColumn}>temporally content</div>
        <div className={styles.sideColumn}>
          <Profile />
          <SignInForm onSignInClicked={() => {}} />
        </div>
      </div>
    </Layout>
  )
}
