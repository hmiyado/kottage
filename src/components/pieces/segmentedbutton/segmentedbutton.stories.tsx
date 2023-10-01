import React from 'react'

import SegmentedButton from './segmentedbutton'
import { Meta, StoryFn } from '@storybook/react'

export default {
  title: 'pieces/SegmentedButton',
  component: SegmentedButton,
  argTypes: {
    onSelectedSegment: {
      action: 'onSelectedSegment',
    },
  },
} as Meta<typeof SegmentedButton>

export const Item2 = {
  args: {
    name: 'item2',
    segments: [
      {
        id: 'first',
        label: '一番目',
      },
      {
        id: 'second',
        label: '二番目',
      },
    ],
  },
}

export const Item7 = {
  args: {
    name: 'item7',
    segments: [
      {
        id: 'sunday',
        label: 'Sunday',
      },
      {
        id: 'monday',
        label: 'Monday',
      },
      {
        id: 'tuesday',
        label: 'Tuesday',
      },
      {
        id: 'wednesday',
        label: 'Wednesday',
      },
      {
        id: 'thursday',
        label: 'Thursday',
      },
      {
        id: 'friday',
        label: 'Friday',
      },
      {
        id: 'saturday',
        label: 'Saturday',
      },
    ],
  },
}
