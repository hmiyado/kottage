import React from 'react'

import SignInForm from './signinform'
import { Meta, StoryFn } from '@storybook/react'

export default {
  title: 'sidemenu/userform/SignInForm',
  component: SignInForm,
  argTypes: {
    onSignInClicked: {
      action: 'clicked',
    },
    onSignUpClicked: {
      action: 'clicked',
    },
  },
} as Meta<typeof SignInForm>

export const Primary = {
  args: {},
}
