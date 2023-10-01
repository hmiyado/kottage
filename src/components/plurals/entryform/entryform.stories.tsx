import React from 'react'

import EntryForm from './entryform'
import { Meta, StoryFn } from '@storybook/react'

export default {
  title: 'EntryForm',
  component: EntryForm,
  argTypes: {
    onSubmit: {
      action: 'clicked',
    },
    onCancel: {
      action: 'clicked',
    },
  },
} as Meta<typeof EntryForm>

export const Primary = {
  args: {},
}
