import React from 'react'

import Component from './template'
import { ComponentMeta, ComponentStory } from '@storybook/react'

export default {
  title: 'template/Component',
  component: Component,
  argTypes: {},
} as ComponentMeta<typeof Component>

const Template: ComponentStory<typeof Component> = (args) => (
  <Component {...args} />
)

export const Primary = Template.bind({})
Primary.args = {}
