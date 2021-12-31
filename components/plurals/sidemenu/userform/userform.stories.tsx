import React from 'react'

import UserForm from './userform'
import { ComponentMeta, ComponentStory } from '@storybook/react'

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
} as ComponentMeta<typeof UserForm>

const Template: ComponentStory<typeof UserForm> = (args) => (
  <UserForm {...args} />
)

export const SignIn = Template.bind({})
SignIn.args = {}

export const SignOut = Template.bind({})
SignOut.args = {
  screenName: 'ScreenName',
}
