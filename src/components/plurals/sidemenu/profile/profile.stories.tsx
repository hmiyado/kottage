import { Meta, StoryFn } from '@storybook/react'
import Profile from './profile'

export default {
  title: 'sidemenu/Profile',
  component: Profile,
  argTypes: {},
} as Meta<typeof Profile>

const Template: StoryFn<typeof Profile> = () => <Profile />

export const Primary = {
  render: Template,
}
