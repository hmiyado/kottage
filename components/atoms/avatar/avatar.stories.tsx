import { ComponentMeta, ComponentStory } from '@storybook/react'
import Avatar from './avatar'
import styles from './avatar.stories.module.css'

export default {
  title: 'Avatar',
  component: Avatar,
  argTypes: {},
} as ComponentMeta<typeof Avatar>

const Template: ComponentStory<typeof Avatar> = (args) => <Avatar {...args} />

export const Primary = Template.bind({})
Primary.args = {}

export const OverrideClass = Template.bind({})
OverrideClass.args = {
  classes: {
    icon: styles.icon,
  },
}
