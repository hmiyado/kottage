import { ComponentMeta, ComponentStory } from '@storybook/react'
import Footer from './footer'

export default {
  title: 'layout/Footer',
  component: Footer,
  argTypes: {},
} as ComponentMeta<typeof Footer>

const Template: ComponentStory<typeof Footer> = () => <Footer />

export const Primary = Template.bind({})
Primary.args = {}
