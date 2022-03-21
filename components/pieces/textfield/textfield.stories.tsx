import { ComponentMeta, ComponentStory } from '@storybook/react'
import TextField from './textfiled'

export default {
  title: 'atoms/TextField',
  component: TextField,
  argTypes: {},
} as ComponentMeta<typeof TextField>

const Template: ComponentStory<typeof TextField> = (args) => (
  <TextField {...args} />
)

export const NoIcon = Template.bind({})
NoIcon.args = {
  label: 'Label',
  assistiveText: 'Assistive text',
}

export const NoIconNoAssistiveText = Template.bind({})
NoIconNoAssistiveText.args = {
  label: 'Label',
}
