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
          <SignInForm onSignInClicked={signIn} onSignUpClicked={signUp} />
        </div>
      </div>
    </Layout>
  )
}

const post = (endpoint: string, body: object) => {
  const request = new Request(`http://localhost:8080/${endpoint}`, {
    method: 'POST',
    headers: new Headers({
      'Content-Type': 'application/json',
    }),
    body: JSON.stringify(body),
  })
  return fetch(request)
    .then((response) => {
      return response.json()
    })
    .then((json) => {
      console.log(json)
    })
}

const signIn = (id: string, password: string) => {
  return post('api/v1/signIn', {
    screenName: id,
    password,
  })
}

const signUp = (id: string, password: string) => {
  return post('api/v1/users', {
    screenName: id,
    password,
  })
}
