import { Meta, StoryFn } from '@storybook/react'
import Layout from './layout'

export default {
  title: 'template/Layout',
  component: Layout,
  argTypes: {},
} as Meta<typeof Layout>

export const Primary = {
  args: {
    children: <div>test</div>,
  },
}
