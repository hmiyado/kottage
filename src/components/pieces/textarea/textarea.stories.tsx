import { Meta, StoryFn } from '@storybook/react'
import TextArea from './textarea'

export default {
  title: 'atoms/TextArea',
  component: TextArea,
  argTypes: {},
} as Meta<typeof TextArea>

export const Primary = {
  args: {
    label: 'Label',
  },
}
