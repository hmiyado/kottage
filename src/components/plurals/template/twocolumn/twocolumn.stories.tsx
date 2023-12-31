import React from 'react'

import { Meta, StoryFn } from '@storybook/react'

const Todo = ({ children }: { children: JSX.Element }) => {
  return <div>Todo</div>
}

export default {
  title: 'template/TwoColumn',
  component: Todo,
  argTypes: {},
} as Meta<typeof Todo>

export const Primary = {
  args: {},
}
