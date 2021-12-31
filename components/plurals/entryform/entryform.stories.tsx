import React from 'react'

import EntryForm from './entryform'
import { ComponentMeta, ComponentStory } from '@storybook/react'

export default {
  title: 'EntryForm',
  component: EntryForm,
  argTypes: {
    onSubmit: {
      action: 'clicked',
    },
    onCancel: {
      action: 'clicked',
    },
  },
} as ComponentMeta<typeof EntryForm>

const Template: ComponentStory<typeof EntryForm> = (args) => (
  <EntryForm {...args} />
)

export const Primary = Template.bind({})
Primary.args = {}
