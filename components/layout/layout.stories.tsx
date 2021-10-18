import { ComponentMeta, ComponentStory } from '@storybook/react'
import Layout from './layout'

export default {
  title: 'layout/Layout',
  component: Layout,
  argTypes: {},
} as ComponentMeta<typeof Layout>

const Template: ComponentStory<typeof Layout> = (args) => <Layout {...args} />

export const Primary = Template.bind({})
Primary.args = {
  children: <div>test</div>,
}
