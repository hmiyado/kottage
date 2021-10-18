import { ComponentMeta, ComponentStory } from '@storybook/react'
import Button from './button'
import Plus from './plus.svg'

export default {
  title: 'atoms/Button',
  component: Button,
  argTypes: {
    onClick: { action: 'clicked' },
  },
} as ComponentMeta<typeof Button>

const Template: ComponentStory<typeof Button> = (args) => <Button {...args} />

export const NoIcon = Template.bind({})
NoIcon.args = {
  text: 'BUTTON',
  Icon: null,
}

export const WithIcon = Template.bind({})
WithIcon.args = {
  text: 'BUTTON',
  Icon: Plus,
}
