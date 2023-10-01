import React from 'react'

import type { Meta, StoryFn } from '@storybook/react'
import colorStyles from './textColor.module.css'
import opacityStyles from './textOpacity.module.css'
import sizeStyles from './textSize.module.css'
import Text, { TextColor, TextOpacity, TextSize } from './text'

const meta: Meta = {
  title: 'atoms/Text',
}
export default meta

const Template: StoryFn<typeof Text> = () => (
  <div style={{ width: '480px' }}>
    {Object.keys(sizeStyles).flatMap((size) => {
      return Object.keys(colorStyles).flatMap((color) => {
        return Object.keys(opacityStyles).map((opacity) => {
          return (
            <Text
              key={`${size}_${color}_${opacity}`}
              size={size as TextSize}
              color={color as TextColor}
              opacity={opacity as TextOpacity}
            >
              {`(size, color, opacity)=(${size}, ${color}, ${opacity})`}
            </Text>
          )
        })
      })
    })}
  </div>
)

export const TextAll = {
  render: Template,
}
