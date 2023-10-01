import { Meta, StoryFn } from '@storybook/react'
import Avatar from './avatar'
import styles from './avatar.stories.module.css'

export default {
  title: 'atoms/Avatar',
  component: Avatar,
  argTypes: {},
} as Meta<typeof Avatar>

export const Primary = {
  args: {},
}

export const OverrideClass = {
  args: {
    classes: {
      icon: styles.icon,
    },
  },
}
