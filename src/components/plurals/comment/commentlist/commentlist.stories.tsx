import React, { Suspense } from 'react'

import CommentList from './commentlist'
import { Meta, StoryFn } from '@storybook/react'
import { compose, rest } from 'msw'
import { SWRConfig } from 'swr'
import { Constants } from 'util/constants'

export default {
  title: 'comment/CommentList',
  component: CommentList,
  argTypes: {},
} as Meta<typeof CommentList>

const WrappedCommentList = (args: { entrySerialNumber: number }) => (
  <SWRConfig value={{ provider: () => new Map(), suspense: true }}>
    <Suspense fallback={<div>loading</div>}>
      <CommentList {...args} />
    </Suspense>
  </SWRConfig>
)

const Template: StoryFn<typeof CommentList> = (args) => (
  <WrappedCommentList {...args}></WrappedCommentList>
)

export const NoComments = {
  render: Template,

  args: {
    entrySerialNumber: 1,
  },

  parameters: {
    msw: {
      handlers: [
        rest.get(
          `${Constants.backendUrl}/api/v1/entries/1/comments`,
          (req, res, ctx) => {
            return res(
              ctx.json({
                totalCount: 0,
                items: [],
              }),
            )
          },
        ),
      ],
    },
  },
}

export const SevenComments = {
  render: Template,

  args: {
    entrySerialNumber: 1,
  },

  parameters: {
    msw: {
      handlers: [
        rest.get(
          `${Constants.backendUrl}/api/v1/entries/1/comments`,
          (req, res, ctx) => {
            return res(
              ctx.json({
                totalCount: 7,
                items: [
                  {
                    id: 7,
                    entrySerialNumber: 1,
                    name: 'コメント',
                    body: 'するぞー',
                    createdAt: new Date('2021-12-30T13:09:12+0000'),
                  },
                  {
                    id: 6,
                    entrySerialNumber: 1,
                    name: 'hogehgoe',
                    body: 'ああああ',
                    createdAt: new Date('2021-12-30T13:08:00+0000'),
                  },
                  {
                    id: 5,
                    entrySerialNumber: 1,
                    name: 'aaaa',
                    body: 'bbbbb',
                    createdAt: new Date('2021-12-30T11:28:46+0000'),
                  },
                  {
                    id: 4,
                    entrySerialNumber: 1,
                    name: 'a',
                    body: 'b',
                    createdAt: new Date('2021-12-30T09:31:42+0000'),
                  },
                  {
                    id: 3,
                    entrySerialNumber: 1,
                    name: 'aaa',
                    body: 'bbbbb',
                    createdAt: new Date('2021-12-30T09:29:58+0000'),
                  },
                  {
                    id: 2,
                    entrySerialNumber: 1,
                    name: 'a',
                    body: 'b',
                    createdAt: new Date('2021-12-30T09:27:22+0000'),
                  },
                  {
                    id: 1,
                    entrySerialNumber: 1,
                    name: 'aaa',
                    body: 'aaa',
                    createdAt: new Date('2021-12-30T09:12:37+0000'),
                  },
                ],
              }),
            )
          },
        ),
      ],
    },
  },
}
