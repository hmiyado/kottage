import React from 'react'

import SignOutForm from './signoutform'
import { Meta, StoryFn } from '@storybook/react'

export default {
  title: 'sidemenu/userform/SignOutForm',
  component: SignOutForm,
  argTypes: {
    onSignOutClicked: {
      action: 'clicked',
    },
  },
} as Meta<typeof SignOutForm>

export const Primary = {
  args: {
    screenName: 'ScreenName',
    accountLinks: [],
  },
}

export const GoogleLinking = {
  args: {
    screenName: 'ScreenName',
    accountLinks: [
      {
        service: 'google',
        linking: true,
      },
    ],
  },
}

export const GoogleNotLinking = {
  args: {
    screenName: 'ScreenName',
    accountLinks: [
      {
        service: 'google',
        linking: false,
      },
    ],
  },
}
