import { Meta, StoryFn } from '@storybook/react'
import Footer from './footer'

export default {
  title: 'layout/Footer',
  component: Footer,
  argTypes: {},
} as Meta<typeof Footer>

const Template: StoryFn<typeof Footer> = () => <Footer />

export const Primary = {
  render: Template,
  args: {},
}
