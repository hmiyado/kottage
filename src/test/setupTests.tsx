import { mockServer } from './mocks/mockServer'
import '@testing-library/jest-dom'
import 'whatwg-fetch'

// https://github.com/facebook/jest/issues/9983#issuecomment-696427273
import { TextDecoder } from 'util'
global.TextDecoder = TextDecoder as any

// https://jestjs.io/docs/manual-mocks#mocking-methods-which-are-not-implemented-in-jsdom
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: jest.fn().mockImplementation((query) => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: jest.fn(), // deprecated
    removeListener: jest.fn(), // deprecated
    addEventListener: jest.fn(),
    removeEventListener: jest.fn(),
    dispatchEvent: jest.fn(),
  })),
})

beforeAll(() => mockServer.listen())
afterEach(() => mockServer.resetHandlers())
afterAll(() => mockServer.close())

// https://github.com/remarkjs/react-markdown/issues/635#issuecomment-991137447
jest.mock('react-markdown', () => {
  const Mock = (props: any) => {
    return <>{props.children}</>
  }
  return Mock
})

jest.mock('remark-breaks', () => jest.fn())

jest.mock('rehype-highlight', () => {
  return jest.fn()
})
