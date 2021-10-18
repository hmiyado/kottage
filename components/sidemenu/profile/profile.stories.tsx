import { ComponentMeta, ComponentStory } from '@storybook/react'
import Profile from './profile'

export default {
  title: 'sidemenu/Profile',
  component: Profile,
  argTypes: {},
} as ComponentMeta<typeof Profile>

const Template: ComponentStory<typeof Profile> = () => <Profile />

export const Primary = Template.bind({})
