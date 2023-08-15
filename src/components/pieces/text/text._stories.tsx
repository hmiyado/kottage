import React from 'react'

import colorStyles from './textColor.module.css'
import opacityStyles from './textOpacity.module.css'
import sizeStyles from './textSize.module.css'
import Text, { TextColor, TextOpacity, TextSize } from './text'
import { storiesOf } from '@storybook/react'

// https://github.com/storybookjs/storybook/blob/next/MIGRATION.md#dropped-source-loader--storiesof-static-snippets
// const stories = storiesOf('Text', module)

// for (const size of Object.keys(sizeStyles)) {
//   for (const color of Object.keys(colorStyles)) {
//     for (const opacity of Object.keys(opacityStyles)) {
//       stories.add(`with ${size} ${color} ${opacity}`, () => {
//         return (
//           <Text
//             color={color as TextColor}
//             size={size as TextSize}
//             opacity={opacity as TextOpacity}
//           >
//             Hello Text
//           </Text>
//         )
//       })
//     }
//   }
// }
