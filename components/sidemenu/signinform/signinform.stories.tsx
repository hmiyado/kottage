import React from 'react'

import SignInForm from './signinform'
import { ComponentMeta, ComponentStory } from '@storybook/react'

export default {
  title: 'sidemenu/SignInForm',
  component: SignInForm,
  argTypes: {
    onSignInClicked: {
      action: 'clicked',
    },
  },
} as ComponentMeta<typeof SignInForm>

const Template: ComponentStory<typeof SignInForm> = (args) => (
  <SignInForm {...args} />
)

export const Primary = Template.bind({})
Primary.args = {}
