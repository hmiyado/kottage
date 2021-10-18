import { ComponentMeta, ComponentStory } from '@storybook/react'
import TextField from './textfiled'
import VisibilityIcon from './visibility.svg'

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
  Icon: null,
  assistiveText: 'Assistive text',
}

export const WithIcon = Template.bind({})
WithIcon.args = {
  label: 'Label',
  Icon: VisibilityIcon,
  assistiveText: 'Assistive text',
}

export const NoIconNoAssistiveText = Template.bind({})
NoIconNoAssistiveText.args = {
  label: 'Label',
}
