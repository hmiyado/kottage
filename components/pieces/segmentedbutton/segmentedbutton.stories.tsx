import React from 'react'

import SegmentedButton from './segmentedbutton'
import { ComponentMeta, ComponentStory } from '@storybook/react'

export default {
  title: 'pieces/SegmentedButton',
  component: SegmentedButton,
  argTypes: {},
} as ComponentMeta<typeof SegmentedButton>

const Template: ComponentStory<typeof SegmentedButton> = (args) => (
  <SegmentedButton {...args} />
)

export const Item2 = Template.bind({})
Item2.args = {
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
}

export const Item7 = Template.bind({})
Item7.args = {
  name: 'item5',
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
}
