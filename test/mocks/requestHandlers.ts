import { compose, rest } from 'msw'
import { Entry, EntryFromJSON, EntryToJSON } from '../../api/openapi/generated'
import Path from '../../api/path'

const baseUrl = 'http://localhost:8080/'
const url = (path: string) => baseUrl + path
export const requestHandlers = [
  rest.post(url('api/v1/entries'), (request, response, context) => {
    const requestBody = request.body
    if (typeof requestBody !== 'object') {
      throw Error('')
    }
    const { title, body } = requestBody
    return response(
      compose(context.status(201)),
      context.json(
        EntryToJSON({
          serialNumber: 1,
          title,
          body,
          dateTime: new Date(),
          author: {
            id: 1,
            screenName: 'test',
          },
        })
      )
    )
  }),
  rest.post(url(Path.signIn), (request, response, context) => {
    return response(
      compose(
        context.status(201),
        context.json({
          id: 1,
          screenName: 'signIn',
        })
      )
    )
  }),
  rest.get(url('api/v1/users/current'), (request, response, context) => {
    return response(
      compose(context.status(200)),
      context.json({
        id: 1,
        screenName: 'users-current',
      })
    )
  }),
  rest.post(url(Path.signUp), (request, response, context) => {
    return response(
      compose(
        context.status(201),
        context.json({
          id: 1,
          screenName: 'signUp',
        })
      )
    )
  }),
  rest.post(url('api/v1/signOut'), (request, response, context) => {
    return response(compose(context.status(200)))
  }),
]
