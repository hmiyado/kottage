import { setupServer } from 'msw/node'
import { requestHandlers } from './requestHandlers'

export const mockServer = setupServer(...requestHandlers)
