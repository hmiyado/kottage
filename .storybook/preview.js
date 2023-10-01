import '../src/pages/globals.css'
import { useState } from 'react'
import UserContext from '../src/context/user'
import { initialize, mswLoader } from 'msw-storybook-addon'

// Initialize MSW
initialize()

const parameters = {
  actions: { argTypesRegex: '^on[A-Z].*' },
  controls: {
    matchers: {
      color: /(background|color)$/i,
      date: /Date$/,
    },
  },
}

const decorators = [
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
]

const previews = {
  parameters,
  decorators,
  loaders: [mswLoader],
}

export default previews
