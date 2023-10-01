import React from 'react'

import Comment from './comment'
import { Meta, StoryFn } from '@storybook/react'

export default {
  title: 'comment/Comment',
  component: Comment,
  argTypes: {},
} as Meta<typeof Comment>

export const Primary = {
  args: {
    comment: {
      name: 'みやど',
      body: 'さっき買い物から帰ってきたところなのにまた買い出しにいく敗北感と面倒臭さに勝てませんでして．．．',
      createdAt: '2021-12-30T23:12:51+0900',
    },
  },
}
