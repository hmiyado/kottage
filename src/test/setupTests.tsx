import { mockServer } from './mocks/mockServer'
import '@testing-library/jest-dom'
import { afterAll, afterEach, beforeAll, vi } from 'vitest'
import 'whatwg-fetch'

beforeAll(() => mockServer.listen())
afterEach(() => mockServer.resetHandlers())
afterAll(() => mockServer.close())

// https://github.com/remarkjs/react-markdown/issues/635#issuecomment-991137447
vi.mock('react-markdown', () => {
  const Mock = (props: any) => {
    return <>{props.children}</>
  }
  return Mock
})

vi.mock('remark-breaks', () => vi.fn())

vi.mock('rehype-highlight', () => {
  return vi.fn()
})
