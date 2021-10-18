import { ComponentMeta, ComponentStory } from '@storybook/react'
import TextArea from './textarea'

export default {
  title: 'atoms/TextArea',
  component: TextArea,
  argTypes: {},
} as ComponentMeta<typeof TextArea>

const Template: ComponentStory<typeof TextArea> = (args) => (
  <TextArea {...args} />
)

export const Primary = Template.bind({})
Primary.args = {
  label: 'Label',
}
