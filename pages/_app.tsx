import './globals.css'
import type { AppProps } from 'next/app'
import UserContext, { User } from '../context/user'
import { useState } from 'react'

export default function MyApp({ Component, pageProps }: AppProps) {
  const [user, updateUser] = useState(null as User)
  return (
    <UserContext.Provider
      value={{
        user,
        updateUser: (newUser) => {
          updateUser(newUser)
        },
      }}
    >
      <Component {...pageProps} />
    </UserContext.Provider>
  )
}
