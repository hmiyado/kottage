import { Comment as OpenApiComment } from 'repository/openapi/generated'
import { convertCommentToProps } from './comment'
import { describe, expect, test } from 'vitest'

describe('convertCommentToProps', () => {
  test('should convert OpenApiComment to props', () => {
    const openapiComment: OpenApiComment = {
      id: 1,
      name: 'name',
      body: 'body',
      createdAt: new Date('2021-11-23T14:31:20+0000'),
      entrySerialNumber: 2,
    }
    const actual = convertCommentToProps(openapiComment)
    expect(actual).toStrictEqual({
      name: 'name',
      body: 'body',
      createdAt: '2021-11-23T23:31:20+09:00',
    })
  })
})
