import { Meta, StoryFn } from '@storybook/react'
import Header from './header'

export default {
  title: 'layout/Header',
  component: Header,
  argTypes: {},
} as Meta<typeof Header>

const Template: StoryFn<typeof Header> = () => <Header />

export const Primary = {
  render: Template,
  args: {},
}
