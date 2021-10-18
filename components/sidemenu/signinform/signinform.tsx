import { useState } from 'react'
import Button from '../../atoms/button/button'
import TextField from '../../atoms/textfield/textfiled'
import styles from './signinform.module.css'

export default function SignInForm({
  onSignInClicked,
}: {
  onSignInClicked: (id: string, password: string) => void
}): JSX.Element {
  const [id, setId] = useState('')
  const [password, setPassword] = useState('')
  return (
    <div className={styles.container}>
      <TextField
        label="ID"
        Icon={null}
        assistiveText={null}
        value={id}
        onChange={(e) => {
          setId(e.target.value)
        }}
      />
      <TextField
        label="Password"
        Icon={null}
        assistiveText={null}
        type="password"
        value={password}
        onChange={(e) => {
          setPassword(e.target.value)
        }}
      />
      <Button
        text="SIGN IN"
        Icon={null}
        disabled={id === '' || password === ''}
        onClick={() => {
          onSignInClicked(id, password)
        }}
      />
    </div>
  )
}
