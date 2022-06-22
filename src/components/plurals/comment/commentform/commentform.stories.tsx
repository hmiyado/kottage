import React from 'react'

import CommentForm from './commentform'
import { ComponentMeta, ComponentStory } from '@storybook/react'

export default {
  title: 'comment/CommentForm',
  component: CommentForm,
  argTypes: {
    onSubmit: {
      action: 'clicked',
    },
    onCancel: {
      action: 'clicked',
    },
  },
} as ComponentMeta<typeof CommentForm>

const Template: ComponentStory<typeof CommentForm> = (args) => (
  <CommentForm {...args} />
)

export const Primary = Template.bind({})
Primary.args = {}
