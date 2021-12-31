import { ComponentMeta, ComponentStory } from '@storybook/react'
import Header from './header'

export default {
  title: 'layout/Header',
  component: Header,
  argTypes: {},
} as ComponentMeta<typeof Header>

const Template: ComponentStory<typeof Header> = () => <Header />

export const Primary = Template.bind({})
Primary.args = {}
