import React from 'react'

import Component from './commentloading'
import { ComponentMeta, ComponentStory } from '@storybook/react'

export default {
  title: 'comment/CommentLoading',
  component: Component,
  argTypes: {},
} as ComponentMeta<typeof Component>

const Templaate: ComponentStory<typeof Component> = (args) => <Component />

export const Primary = Templaate.bind({})
Primary.args = {}
