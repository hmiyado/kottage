import { useState } from 'react'
import Button from '../../../atoms/button/button'
import TextField from '../../../atoms/textfield/textfiled'
import styles from './signinform.module.css'

export type SignInFormProps = {
  onSignInClicked: (id: string, password: string) => void
  onSignUpClicked: (id: string, password: string) => void
}

export default function SignInForm({
  onSignInClicked,
  onSignUpClicked,
}: SignInFormProps): JSX.Element {
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
