import Layout from '../components/layout/layout'
import SignInForm from '../components/sidemenu/signinform/signinform'
import styles from './rootpage.module.css'
import Profile from '../components/sidemenu/profile/profile'
import UserContext, { User } from '../context/user'

export default function RootPage() {
  return (
    <UserContext.Consumer>
      {({ user, updateUser }) => (
        <Layout>
          <div className={styles.container}>
            <div className={styles.mainColumn}>temporally content</div>
            <div className={styles.sideColumn}>
              <Profile />

              {user ? (
                <div>
                  signed in as {user.id} {user.screenName}
                </div>
              ) : (
                <SignInForm
                  onSignInClicked={signAndUpdateUser(signIn, updateUser)}
                  onSignUpClicked={signAndUpdateUser(signUp, updateUser)}
                />
              )}
            </div>
          </div>
        </Layout>
      )}
    </UserContext.Consumer>
  )
}

const post = (endpoint: string, body: object): Promise<any> => {
  const request = new Request(`http://localhost:8080/${endpoint}`, {
    method: 'POST',
    headers: new Headers({
      'Content-Type': 'application/json',
    }),
    body: JSON.stringify(body),
  })
  return fetch(request).then((response) => {
    return response.json()
  })
}

type Sign = (id: string, password: string) => Promise<any>

const signIn: Sign = (id, password) => {
  return post('api/v1/signIn', {
    screenName: id,
    password,
  })
}

const signUp: Sign = (id, password) => {
  return post('api/v1/users', {
    screenName: id,
    password,
  })
}

const signAndUpdateUser = (
  sign: Sign,
  updateUser: (newUser: User) => void
): Sign => {
  return (id, password) => {
    return sign(id, password)
      .then((json) => {
        console.log(json)
        updateUser({
          id: json['id'],
          screenName: json['screenName'],
        })
      })
      .catch(() => {
        updateUser(null)
      })
  }
}
