import { Entry as OpenApiEntry } from '../../../repository/openapi/generated'
import { convertEntryToProps, EntryProps } from './entry'

describe('convertEntryToProps', () => {
  test('should convert OpenApiEntry to props', () => {
    const openapiEntry: OpenApiEntry = {
      serialNumber: 1,
      title: 'title',
      body: 'body',
      commentsTotalCount: 10,
      dateTime: new Date('2021-11-23T14:31:20+0000'),
      author: {
        id: 2,
        screenName: 'name',
      },
    }
    const actual = convertEntryToProps(openapiEntry)
    expect(actual).toStrictEqual({
      serialNumber: 1,
      title: 'title',
      body: 'body',
      commentsCount: 10,
      time: '2021-11-23T23:31:20+09:00',
      author: 'name',
    } as EntryProps)
  })
})
