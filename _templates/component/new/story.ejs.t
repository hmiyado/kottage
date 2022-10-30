---
to: src/components/<%=path%>/<%=name%>.stories.tsx
---
import React from 'react'

import <%=Name%> from './<%=name%>'
import { ComponentMeta, ComponentStory } from '@storybook/react'

export default {
  title: '<%=path%>/<%=Name%>',
  component: <%=Name%>,
  argTypes: {},
} as ComponentMeta<typeof <%=Name%>>

const Component: ComponentStory<typeof <%=Name%>> = (args) => (
  < <%=Name%> {...args} />
)

export const Primary = Component.bind({})
Primary.args = {}
