import { Meta, StoryFn } from '@storybook/react'
import Button from './button'

export default {
  title: 'atoms/Button',
  component: Button,
  argTypes: {
    onClick: { action: 'clicked' },
  },
} as Meta<typeof Button>

export const NoIcon = {
  args: {
    text: 'BUTTON',
  },
}

export const Disabled = {
  args: {
    text: 'BUTTON',
    disabled: true,
  },
}
