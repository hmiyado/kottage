import React from 'react'

import CommentForm from './commentform'
import { Meta, StoryFn } from '@storybook/react'

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
} as Meta<typeof CommentForm>

export const Primary = {
  args: {},
}
