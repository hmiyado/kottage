import React from 'react'

import Comment from './comment'
import { ComponentMeta, ComponentStory } from '@storybook/react'

export default {
  title: 'comment/Comment',
  component: Comment,
  argTypes: {},
} as ComponentMeta<typeof Comment>

const Template: ComponentStory<typeof Comment> = (args) => <Comment {...args} />

export const Primary = Template.bind({})
Primary.args = {
  comment: {
    name: 'みやど',
    body: 'さっき買い物から帰ってきたところなのにまた買い出しにいく敗北感と面倒臭さに勝てませんでして．．．',
    createdAt: '2021-12-30T23:12:51+0900',
  },
}
