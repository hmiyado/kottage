import React from 'react'

import ServiceReference from './servicereference'
import { ComponentMeta, ComponentStory } from '@storybook/react'

export default {
  title: 'sidemenu/ServiceReference',
  component: ServiceReference,
  argTypes: {},
} as ComponentMeta<typeof ServiceReference>

const Template: ComponentStory<typeof ServiceReference> = (args) => (
  <ServiceReference {...args} />
)

export const Light = Template.bind({})
Light.args = {
  theme: 'light',
}

export const Dark = Template.bind({})
Dark.args = {
  theme: 'dark',
}
