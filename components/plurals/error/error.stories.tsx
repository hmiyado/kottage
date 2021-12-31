import React from 'react'

import Error from './error'
import { ComponentMeta, ComponentStory } from '@storybook/react'

export default {
  title: 'Error',
  component: Error,
  argTypes: {},
} as ComponentMeta<typeof Error>

const Template: ComponentStory<typeof Error> = (args) => <Error {...args} />

export const Primary = Template.bind({})
Primary.args = {
  title: '404 - Not Found',
  description: 'お探しのページは見つかりませんでした',
}
