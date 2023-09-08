import '../src/pages/globals.css'
import { useState } from 'react'
import UserContext from '../src/context/user'
import { initialize, mswDecorator } from 'msw-storybook-addon'

// Initialize MSW
initialize()

export const parameters = {
  actions: { argTypesRegex: '^on[A-Z].*' },
  controls: {
    matchers: {
      color: /(background|color)$/i,
      date: /Date$/,
    },
  },
}

export const decorators = [
  (storyFn) => {
    const [user, updateUser] = useState(null)

    return (
      <UserContext.Provider
        value={{
          user,
          updateUser: (newUser) => {
            updateUser(newUser)
          },
        }}
      >
        {storyFn()}
      </UserContext.Provider>
    )
  },
  mswDecorator,
]
