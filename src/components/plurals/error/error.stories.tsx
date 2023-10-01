import React from 'react'

import Error from './error'
import { Meta, StoryFn } from '@storybook/react'

export default {
  title: 'Error',
  component: Error,
  argTypes: {},
} as Meta<typeof Error>

export const Primary = {
  args: {
    title: '404 - Not Found',
    description: 'お探しのページは見つかりませんでした',
  },
}
