import React from 'react'

import OneColumn from './onecolumn'
import { ComponentMeta, ComponentStory } from '@storybook/react'

export default {
  title: 'template/OneColumn',
  component: OneColumn,
  argTypes: {},
} as ComponentMeta<typeof OneColumn>

const Template: ComponentStory<typeof OneColumn> = (args) => (
  <OneColumn {...args} />
)

export const Primary = Template.bind({})
Primary.args = {}
