import React from 'react'

import UserForm from './userform'
import { Meta, StoryFn } from '@storybook/react'
import { compose, rest } from 'msw'
import { SWRConfig } from 'swr'
import { Constants } from 'util/constants'

export default {
  title: 'sidemenu/UserForm',
  component: UserForm,
  argTypes: {
    onSignInClicked: {
      action: 'clicked',
    },
    onSignUpClicked: {
      action: 'clicked',
    },
    onSignOutClicked: {
      action: 'clicked',
    },
  },
} as Meta<typeof UserForm>

const Template: StoryFn<typeof UserForm> = (args) => {
  return (
    <SWRConfig value={{ provider: () => new Map() }}>
      <UserForm />
    </SWRConfig>
  )
}

export const SignIn = {
  render: Template,

  parameters: {
    msw: {
      handlers: [
        rest.get(
          `${Constants.backendUrl}/api/v1/users/current`,
          (req, res, ctx) => {
            return res(compose(ctx.status(401)), ctx.json({}))
          },
        ),
      ],
    },
  },
}

export const SignOut = {
  render: Template,

  parameters: {
    msw: {
      handlers: [
        rest.get(
          `${Constants.backendUrl}/api/v1/users/current`,
          (req, res, ctx) => {
            return res(
              ctx.json({
                id: 1,
                screenName: 'users-current',
              }),
            )
          },
        ),
      ],
    },
  },
}
