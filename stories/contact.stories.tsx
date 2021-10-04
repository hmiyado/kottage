import React from 'react'

import Contact from '../components/contact'
import { ComponentMeta, ComponentStory } from '@storybook/react'

export default {
  title: 'Contact',
  component: Contact,
  argTypes: {},
} as ComponentMeta<typeof Contact>

const Template: ComponentStory<typeof Contact> = () => <Contact />

export const Primary = Template.bind({})
Primary.args = {}
