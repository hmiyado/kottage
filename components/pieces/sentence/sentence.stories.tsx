import React from 'react'

import Sentence from './sentence'
import { ComponentMeta, ComponentStory } from '@storybook/react'

export default {
  title: 'atoms/Sentence',
  component: Sentence,
  argTypes: {},
} as ComponentMeta<typeof Sentence>

const Template: ComponentStory<typeof Sentence> = (args) => (
  <div style={{ width: '480px' }}>
    <Sentence {...args} />
  </div>
)

export const Short = Template.bind({})
Short.args = {
  title: 'short title',
}

export const Long = Template.bind({})
Long.args = {
  title: 'long long long long long long title',
}

export const MultiLines = Template.bind({})
MultiLines.args = {
  title: 'long title',
  children: 'line1\nline2line2line2\nline3 and two line breaks\n\nline4',
}

export const LongWordBody = Template.bind({})
LongWordBody.args = {
  title: 'long word body',
  children:
    'line1\nsuperlonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglongword\n',
}
