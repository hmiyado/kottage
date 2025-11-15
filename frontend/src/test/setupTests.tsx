import { mockServer } from './mocks/mockServer'
import { afterAll, afterEach, beforeAll, vi } from 'vitest'
import '@testing-library/jest-dom/vitest'
import 'whatwg-fetch'

beforeAll(() => mockServer.listen())
afterEach(() => mockServer.resetHandlers())
afterAll(() => mockServer.close())

// https://github.com/remarkjs/react-markdown/issues/635#issuecomment-991137447
vi.mock('react-markdown', () => {
  const Mock = (props: { children?: React.ReactNode }) => {
    return <>{props.children}</>
  }
  return {
    default: Mock,
  }
})

vi.mock('remark-breaks', () => ({
  default: vi.fn(),
}))

vi.mock('rehype-highlight', () => ({
  default: vi.fn(),
}))
