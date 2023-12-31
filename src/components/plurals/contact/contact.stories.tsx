import React from 'react'

import Contact from './contact'
import { Meta, StoryFn } from '@storybook/react'

export default {
  title: 'Contact',
  component: Contact,
  argTypes: {},
} as Meta<typeof Contact>

const Template: StoryFn<typeof Contact> = () => <Contact />

export const Primary = {
  render: Template,
  args: {},
}
