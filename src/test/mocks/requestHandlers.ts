import { compose, rest } from 'msw'
import {
  Entry,
  EntryFromJSON,
  EntryToJSON,
} from '../../repository/openapi/generated'
import Path from '../../repository/path'
import { Constants } from 'util/constants'

function rangeArray(
  start: number,
  endExclusive: number,
  step: number = 1
): number[] {
  const array: number[] = []
  for (let i = start; i < endExclusive; i += step) {
    array.push(i)
  }
  return array
}

const baseUrl = Constants.backendUrl
const url = (path: string) => baseUrl + path
export const requestHandlers = [
  rest.get(url('api/v1/entries'), (request, response, context) => {
    return response(
      compose(context.status(201)),
      context.json({
        items: rangeArray(0, 10).map((v) => {
          return {
            serialNumber: v,
            title: `${v}th entry`,
            body: `body`,
            commentsTotalCount: 10,
            dateTime: new Date(),
            author: {
              id: 1,
              screenName: 'test',
            },
          }
        }),
      })
    )
  }),
  rest.post(url('api/v1/entries'), (request, response, context) => {
    const requestBody = request.body
    if (typeof requestBody !== 'object' || requestBody == null) {
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
          commentsTotalCount: 10,
          dateTime: new Date(),
          author: {
            id: 1,
            screenName: 'test',
          },
        })
      )
    )
  }),
  rest.get(url('api/v1/entries/1'), (request, response, context) => {
    return response(
      compose(context.status(200)),
      context.json(
        EntryToJSON({
          serialNumber: 1,
          title: 'title',
          body: 'body',
          commentsTotalCount: 10,
          dateTime: new Date(),
          author: {
            id: 1,
            screenName: 'test',
          },
        })
      )
    )
  }),
  rest.post(url('api/v1/sign-in'), (request, response, context) => {
    return response(
      compose(
        context.status(201),
        context.json({
          id: 1,
          screenName: 'signIn',
          accountLinks: [],
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
        accountLinks: [],
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
          accountLinks: [],
        })
      )
    )
  }),
  rest.post(url('api/v1/sign-out'), (request, response, context) => {
    return response(compose(context.status(200)))
  }),
]
