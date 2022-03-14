import { mockServer } from './mocks/mockServer'
import '@testing-library/jest-dom'

import fetch from 'node-fetch'

if (!globalThis.fetch) {
  globalThis.fetch = fetch as any
}

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
