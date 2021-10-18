import React from 'react'

import Sentence from './sentence'
import { ComponentMeta, ComponentStory } from '@storybook/react'

export default {
  title: 'atoms/Sentence',
  component: Sentence,
  argTypes: {},
} as ComponentMeta<typeof Sentence>

const Template: ComponentStory<typeof Sentence> = (args) => (
  <Sentence {...args} />
)

export const Short = Template.bind({})
Short.args = {
  title: 'short title',
}

export const Long = Template.bind({})
Long.args = {
  title: 'long long long long long long title',
}
