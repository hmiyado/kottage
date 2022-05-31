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

export const Primary = Template.bind({})
Primary.args = {
  name: 'primary',
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
