import React from 'react'

import Sentence from './sentence'
import { ComponentMeta, ComponentStory } from '@storybook/react'

export default {
  title: 'atoms/Sentence',
  component: Sentence,
  argTypes: {},
} as ComponentMeta<typeof Sentence>

const Template: ComponentStory<typeof Sentence> = (args) => (
  <div style={{ width: '480px' }}>
    <Sentence {...args} />
  </div>
)

export const Short = Template.bind({})
Short.args = {
  title: 'short title',
}

export const Long = Template.bind({})
Long.args = {
  title: 'long long long long long long title',
}

export const MultiLines = Template.bind({})
MultiLines.args = {
  title: 'long title',
  children: 'line1\nline2line2line2\nline3 and two line breaks\n\nline4',
}

export const LongWordBody = Template.bind({})
LongWordBody.args = {
  title: 'long word body',
  children:
    'line1\nsuperlonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglongword\n',
}

export const Markdown = Template.bind({})
Markdown.args = {
  title: 'long word body',
  children: `
  # Heading 1
  
  ## Heading 2

  ### Heading 3

  #### Heading 4

  ##### Heading 5

  ##### Heading 6

  ## unorderd list
  
  * Item 1
  * Item 2
  * Item 3
  
  ## ordered list
  
  1. first
  1. second 
  1. third

  ## styles

  *Italic*

  **Bold**

  [Link(github)](https://github.com)

  ![Image](./public/components/pieces/avatar/miyado_icon.svg)

  >Block quote

  Horizontal rule
  ---

  \`Inline code\`

    ## code highlight
  [\`rehype-highlight\`](https://github.com/rehypejs/rehype-highlight).

  \`\`\`js
  import React from 'react'
  import ReactDOM from 'react-dom'
  import ReactMarkdown from 'react-markdown'
  import rehypeHighlight from 'rehype-highlight'

  ReactDOM.render(
    <ReactMarkdown rehypePlugins={[rehypeHighlight]}>{'# Your markdown here'}</ReactMarkdown>,
    document.querySelector('#content')
  )
  \`\`\`
  `,
}
