import { http, HttpResponse } from 'msw'
import { EntryToJSON } from '../../repository/openapi/generated'
import Path from '../../repository/path'
import { Constants } from '../../util/constants'

function rangeArray(
  start: number,
  endExclusive: number,
  step: number = 1,
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
  http.get(url('/api/v1/entries'), () => {
    return HttpResponse.json(
      {
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
      },
      {
        status: 201,
      },
    )
  }),
  http.post(url('/api/v1/entries'), async ({ request }) => {
    const requestBody = await request.json()
    if (typeof requestBody !== 'object' || requestBody == null) {
      throw Error('')
    }
    const { title, body } = requestBody
    return HttpResponse.json(
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
      }),
      {
        status: 201,
      },
    )
  }),
  http.get(url('/api/v1/entries/1'), () => {
    return HttpResponse.json(
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
      }),
      {
        status: 200,
      },
    )
  }),
  http.post(url('/api/v1/sign-in'), () => {
    return HttpResponse.json(
      {
        id: 1,
        screenName: 'signIn',
        accountLinks: [],
      },
      {
        status: 201,
      },
    )
  }),
  http.get(url('/api/v1/users/current'), () => {
    return HttpResponse.json(
      {
        id: 1,
        screenName: 'users-current',
        accountLinks: [],
      },
      {
        status: 200,
      },
    )
  }),
  http.post(url(Path.signUp), () => {
    return HttpResponse.json(
      {
        id: 1,
        screenName: 'signUp',
        accountLinks: [],
      },
      { status: 201 },
    )
  }),
  http.post(url('/api/v1/sign-out'), () => {
    return new HttpResponse(null, {
      status: 200,
    })
  }),
]
