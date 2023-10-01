import React from 'react'

import Entry from './entry'
import { Meta, StoryFn } from '@storybook/react'

export default {
  title: 'Entry',
  component: Entry,
} as Meta<typeof Entry>

export const Primary = {
  args: {
    props: {
      serialNumber: 1,
      title: 'short title',
      body: 'body',
      time: '2021-05-05T21:12',
      commentsCount: 0,
      author: 'hmiyado',
    },
  },
}

export const Entry1 = {
  args: {
    props: {
      serialNumber: 1,
      title: 'GW',
      body: 'GW はコーディングしたり、リモートゲームしたり、無計画だったわりには楽しく過ごせた。コーディングでは2年前のリポジトリを引っ張り出して整理したら、思っていた以上に興が乗った。次に触るのは2年後のGWかもしれない。',
      time: '2021-05-05T21:12',
      commentsCount: 10,
      author: 'hmiyado',
    },
  },
}
