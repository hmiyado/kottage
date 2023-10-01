import React from 'react'

import OneColumn from './onecolumn'
import { Meta, StoryFn } from '@storybook/react'

export default {
  title: 'template/OneColumn',
  component: OneColumn,
  argTypes: {},
} as Meta<typeof OneColumn>

export const Primary = {
  args: {},
}
