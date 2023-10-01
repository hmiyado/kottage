import React from 'react'

import Component from './commentloading'
import { Meta, StoryFn } from '@storybook/react'

export default {
  title: 'comment/CommentLoading',
  component: Component,
  argTypes: {},
} as Meta<typeof Component>

const Templaate: StoryFn<typeof Component> = (args) => <Component />

export const Primary = {
  render: Templaate,
  args: {},
}
