import { Comment as OpenApiComment } from 'api/openapi/generated'
import { convertCommentToProps } from './comment'

describe('convertCommentToProps', () => {
  test('should convert OpenApiComment to props', () => {
    const openapiComment: OpenApiComment = {
      id: 1,
      name: 'name',
      body: 'body',
      createdAt: new Date('2021-11-23T14:31:20+0000'),
    }
    const actual = convertCommentToProps(openapiComment)
    expect(actual).toStrictEqual({
      name: 'name',
      body: 'body',
      createdAt: '2021-11-23T23:31:20+0900',
    })
  })
})
