import { useState } from 'react'
import Button from '../../../../pieces/button/button'
import TextField from '../../../../pieces/textfield/textfiled'
import Link from 'next/link'
import { Constants } from '../../../../../util/constants'

const styles = {
  container: ['flex flex-col flex-wrap'].join(' '),
  buttonContainer: ['flex flex-row justify-between'].join(' '),
  oauthGoogleLink: [
    'oauth-google-link',
    "bg-[url('/components/sidemenu/signinform/google_signin_buttons/btn_google_signin_light_normal_web.png')]",
    "focus:bg-[url('/components/sidemenu/signinform/google_signin_buttons/btn_google_signin_light_focus_web.png')]",
    "visited:bg-[url('/components/sidemenu/signinform/google_signin_buttons/btn_google_signin_light_pressed_web.png')]",
    "dark:bg-[url('/components/sidemenu/signinform/google_signin_buttons/btn_google_signin_dark_normal_web.png')]",
    "dark:focus:bg-[url('/components/sidemenu/signinform/google_signin_buttons/btn_google_signin_dark_focus_web.png')]",
    "dark:visited:bg-[url('/components/sidemenu/signinform/google_signin_buttons/btn_google_signin_dark_pressed_web.png')]",
  ].join(' '),
}

export type SignInFormProps = {
  onSignInClicked: (id: string, password: string) => void
  onSignUpClicked: (id: string, password: string) => void
}

export default function SignInForm({
  onSignInClicked,
  onSignUpClicked,
}: SignInFormProps): React.JSX.Element {
  const [id, setId] = useState('')
  const [password, setPassword] = useState('')
  return (
    <div className={styles.container}>
      <TextField
        label="ID"
        assistiveText={null}
        value={id}
        onChange={(e) => {
          setId(e.target.value)
        }}
      />
      <TextField
        label="Password"
        assistiveText={null}
        type="password"
        value={password}
        onChange={(e) => {
          setPassword(e.target.value)
        }}
      />
      <Link
        href={`${Constants.backendUrl}/oauth/google/authorize`}
        className={styles.oauthGoogleLink}
      ></Link>
      <div className={styles.buttonContainer}>
        <Button
          text="SIGN IN"
          disabled={id === '' || password === ''}
          onClick={() => {
            onSignInClicked(id, password)
          }}
        />
        <Button
          text="SIGN UP"
          disabled={id === '' || password === ''}
          onClick={() => {
            onSignUpClicked(id, password)
          }}
        />
      </div>
    </div>
  )
}
