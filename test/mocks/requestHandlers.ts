import { compose, rest } from 'msw'
import Path from '../../api/path'

const baseUrl = 'http://localhost:8080/'
const url = (path: string) => baseUrl + path
export const requestHandlers = [
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
