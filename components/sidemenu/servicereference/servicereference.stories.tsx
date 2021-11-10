import React from 'react'

import ServiceReference from './servicereference'
import { ComponentMeta, ComponentStory } from '@storybook/react'

export default {
  title: 'sidemenu/ServiceReference',
  component: ServiceReference,
  argTypes: {},
} as ComponentMeta<typeof ServiceReference>

const SERVICEREFERENCE: ComponentStory<typeof ServiceReference> = () => (
  <ServiceReference />
)

export const Primary = SERVICEREFERENCE.bind({})
Primary.args = {}
