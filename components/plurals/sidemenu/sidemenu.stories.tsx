import React from 'react'

import SideMenu from './sidemenu'
import { ComponentMeta, ComponentStory } from '@storybook/react'

export default {
  title: 'sidemenu/SideMenu',
  component: SideMenu,
  argTypes: {},
} as ComponentMeta<typeof SideMenu>

const Template: ComponentStory<typeof SideMenu> = (args) => (
  <SideMenu {...args} />
)

export const Primary = Template.bind({})
Primary.args = {}
