import { mockServer } from './mocks/mockServer'
import '@testing-library/jest-dom'

import fetch from 'node-fetch'

if (!globalThis.fetch) {
  globalThis.fetch = fetch as any
}

beforeAll(() => mockServer.listen())
afterEach(() => mockServer.resetHandlers())
afterAll(() => mockServer.close())
