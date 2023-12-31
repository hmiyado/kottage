import React from 'react'

import ServiceReference from './servicereference'
import { Meta, StoryFn } from '@storybook/react'

export default {
  title: 'sidemenu/ServiceReference',
  component: ServiceReference,
  argTypes: {},
} as Meta<typeof ServiceReference>

export const Light = {
  args: {
    theme: 'light',
  },
}

export const Dark = {
  args: {
    theme: 'dark',
  },
}
