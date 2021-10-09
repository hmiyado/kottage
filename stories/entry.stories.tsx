import React from 'react'

import Entry from '../components/entry'
import { ComponentMeta, ComponentStory } from '@storybook/react'

export default {
  title: 'Entry',
  component: Entry,
} as ComponentMeta<typeof Entry>

const Template: ComponentStory<typeof Entry> = (args) => <Entry {...args} />

export const Primary = Template.bind({})
Primary.args = {
  title: 'short title',
  children: 'body',
  time: '2021-05-05T21:12',
  author: 'hmiyado',
}
