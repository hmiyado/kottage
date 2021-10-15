import { ComponentMeta, ComponentStory } from '@storybook/react'
import Button from './button'

export default {
  title: 'Button',
  component: Button,
  argTypes: {},
} as ComponentMeta<typeof Button>

const Template: ComponentStory<typeof Button> = (args) => <Button {...args} />

export const NoIcon = Template.bind({})
NoIcon.args = {
  text: 'BUTTON',
  icon: null,
}
