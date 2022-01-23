import '../pages/globals.css'
import { useState } from 'react'
import UserContext from '../context/user'
import * as nextImage from 'next/image'
import { initialize, mswDecorator } from 'msw-storybook-addon'
import { addDecorator } from '@storybook/react'

// Initialize MSW
initialize()

// Provide the MSW addon decorator globally
export const decorators = [mswDecorator]

Object.defineProperty(nextImage, 'default', {
  configurable: true,
  value: (props) => <img {...props} />,
})

export const parameters = {
  actions: { argTypesRegex: '^on[A-Z].*' },
  controls: {
    matchers: {
      color: /(background|color)$/i,
      date: /Date$/,
    },
  },
}

const decorator = (storyFn) => {
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
}
addDecorator(decorator)
