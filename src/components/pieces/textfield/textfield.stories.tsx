import { Meta, StoryFn } from '@storybook/react'
import TextField from './textfiled'

export default {
  title: 'atoms/TextField',
  component: TextField,
  argTypes: {},
} as Meta<typeof TextField>

export const NoIcon = {
  args: {
    label: 'Label',
    assistiveText: 'Assistive text',
  },
}

export const NoIconNoAssistiveText = {
  args: {
    label: 'Label',
  },
}
