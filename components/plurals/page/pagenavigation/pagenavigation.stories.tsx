import React from 'react'

import PageNavigation from './pagenavigation'
import { ComponentMeta, ComponentStory } from '@storybook/react'

export default {
  title: 'page/pagenavigation/PageNavigation',
  component: PageNavigation,
  argTypes: {},
} as ComponentMeta<typeof PageNavigation>

const Template: ComponentStory<typeof PageNavigation> = (args) => (
  <PageNavigation {...args} />
)

export const Primary = Template.bind({})
Primary.args = {
  totalPages: 10,
  currentPage: 5,
}
export const First = Template.bind({})
First.args = {
  totalPages: 10,
  currentPage: 1,
}
export const LastPage = Template.bind({})
LastPage.args = {
  totalPages: 10,
  currentPage: 10,
}
