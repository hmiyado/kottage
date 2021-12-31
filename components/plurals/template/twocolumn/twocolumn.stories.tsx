import React from 'react'

import { ComponentMeta, ComponentStory } from '@storybook/react'

const Todo = ({ children }: { children: JSX.Element }) => {
  return <div>Todo</div>
}

export default {
  title: 'template/TwoColumn',
  component: Todo,
  argTypes: {},
} as ComponentMeta<typeof Todo>

const Template: ComponentStory<typeof Todo> = (args) => <Todo {...args} />

export const Primary = Template.bind({})
Primary.args = {}
