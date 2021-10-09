import React from 'react'

import Entry from '../components/entry'
import { ComponentMeta, ComponentStory } from '@storybook/react'

export default {
  title: 'Entry',
  component: Entry,
} as ComponentMeta<typeof Entry>

const Template: ComponentStory<typeof Entry> = (args) => <Entry {...args} />

export const Primary = Template.bind({})
Primary.args = {
  title: 'short title',
  children: 'body',
  time: '2021-05-05T21:12',
  author: 'hmiyado',
}

export const Entry1 = Template.bind({})
Entry1.args = {
  title: 'GW',
  children:
    'GW はコーディングしたり、リモートゲームしたり、無計画だったわりには楽しく過ごせた。コーディングでは2年前のリポジトリを引っ張り出して整理したら、思っていた以上に興が乗った。次に触るのは2年後のGWかもしれない。',
  time: '2021-05-05T21:12',
  author: 'hmiyado',
}
