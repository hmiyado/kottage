import initStoryshots, {
  Stories2SnapsConverter,
} from '@storybook/addon-storyshots'
import { act } from '@testing-library/react'
import { setupServer } from 'msw/node'
import React from 'react'
import { create } from 'react-test-renderer'

//https://github.com/storybookjs/storybook/issues/8083#issuecomment-533184737
jest.mock('global', () =>
  Object.assign(global, { window: { STORYBOOK_HOOKS_CONTEXT: '' } })
)

// https://github.com/storybookjs/storybook/issues/7745#issuecomment-807338022
const converter = new Stories2SnapsConverter()
initStoryshots({
  test: async ({ story, context, done }) => {
    const endpoints: any[] = context.parameters.msw?.handlers ?? []
    const shouldMockEndpoints = endpoints.length > 0
    const server = setupServer(...endpoints)
    server.listen()

    try {
      let renderer
      act(() => {
        // React.createElement() is important because of hooks [shouldn't call story.render() directly]
        renderer = create(React.createElement(story.render))
      })

      // wait for state changes
      await act(
        () =>
          new Promise((resolve) =>
            setTimeout(resolve, shouldMockEndpoints ? 10 : 0)
          )
      )

      expect(renderer).toMatchSnapshot()

      done && done()
    } finally {
      server.close()
    }
  },
})
