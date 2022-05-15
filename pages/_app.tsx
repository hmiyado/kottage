import './globals.css'
import type { AppProps } from 'next/app'
import UserContext, { User } from '../context/user'
import { useState } from 'react'
import { SWRConfig } from 'swr'

export default function MyApp({ Component, pageProps }: AppProps) {
  const [user, updateUser] = useState(null as User)
  return (
    <SWRConfig value={{ suspense: true }}>
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
    </SWRConfig>
  )
}
