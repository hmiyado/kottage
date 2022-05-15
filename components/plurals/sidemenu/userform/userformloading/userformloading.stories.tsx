import React from 'react'

import UserFormLoading from './userformloading'
import { ComponentMeta, ComponentStory } from '@storybook/react'

export default {
  title: 'sidemenu/userform/userformloading',
  component: UserFormLoading,
  argTypes: {},
} as ComponentMeta<typeof UserFormLoading>

const Template: ComponentStory<typeof UserFormLoading> = (args) => (
  <UserFormLoading {...args} />
)

export const Primary = Template.bind({})
Primary.args = {}
