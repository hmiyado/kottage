import React from 'react'

import PageNavigation from './pagenavigation'
import { Meta, StoryFn } from '@storybook/react'

export default {
  title: 'page/pagenavigation/PageNavigation',
  component: PageNavigation,
  argTypes: {},
} as Meta<typeof PageNavigation>

export const Primary = {
  args: {
    totalPages: 10,
    currentPage: 5,
  },
}

export const First = {
  args: {
    totalPages: 10,
    currentPage: 1,
  },
}

export const LastPage = {
  args: {
    totalPages: 10,
    currentPage: 10,
  },
}
