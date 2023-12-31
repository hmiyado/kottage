import React from 'react'

import Sentence from './sentence'
import { Meta, StoryFn } from '@storybook/react'

export default {
  title: 'atoms/Sentence',
  component: Sentence,
  argTypes: {},
} as Meta<typeof Sentence>

const Template: StoryFn<typeof Sentence> = (args) => (
  <div style={{ width: '480px' }}>
    <Sentence {...args} />
  </div>
)

export const Short = {
  render: Template,

  args: {
    title: 'short title',
  },
}

export const Long = {
  render: Template,

  args: {
    title: 'long long long long long long title',
  },
}

export const MultiLines = {
  render: Template,

  args: {
    title: 'long title',
    children: 'line1\nline2line2line2\nline3 and two line breaks\n\nline4',
  },
}

export const LongWordBody = {
  render: Template,

  args: {
    title: 'long word body',
    children:
      'line1\nsuperlonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglongword\n',
  },
}

export const Markdown = {
  render: Template,

  args: {
    title: 'long word body',
    children: `
    # Heading 1
    
    ## Heading 2

    ### Heading 3

    #### Heading 4

    ##### Heading 5

    ###### Heading 6

    ## unorderd list
    
    * Item 1
        * Item 1-1
            * Item 1-1-1
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

    >>Indented Block quote

    Horizontal rule

    ---

    \`Inline code\`

    ## code highlight
    
    ### JavaScript

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

    ### Kotlin

    \`\`\`kotlin
    data class ClientSession(
        val token: String
    ): CsrfTokenBoundClient {
        override val representation: String = token
    }
    \`\`\`

    `,
  },
}
