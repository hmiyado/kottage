import initStoryshots from '@storybook/addon-storyshots'
//https://github.com/storybookjs/storybook/issues/8083#issuecomment-533184737
jest.mock('global', () =>
  Object.assign(global, { window: { STORYBOOK_HOOKS_CONTEXT: '' } })
)
initStoryshots()
