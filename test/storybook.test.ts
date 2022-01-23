import initStoryshots, {
  Stories2SnapsConverter,
} from '@storybook/addon-storyshots'
import React from 'react'
import { act, create } from 'react-test-renderer'
import wait from 'waait'

//https://github.com/storybookjs/storybook/issues/8083#issuecomment-533184737
jest.mock('global', () =>
  Object.assign(global, { window: { STORYBOOK_HOOKS_CONTEXT: '' } })
)

// https://github.com/storybookjs/storybook/issues/7745#issuecomment-807338022
const converter = new Stories2SnapsConverter()
initStoryshots({
  asyncJest: true,
  test: async ({ story, context, done }) => {
    let renderer
    act(() => {
      renderer = create(React.createElement(story.render))
    })

    // Let one render cycle pass before rendering snapshot
    await act(() => wait(0))

    expect(renderer).toMatchSnapshot()

    done && done()
  },
})
