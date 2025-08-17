import { describe, expect, test } from 'vitest'
import { dateFormatter } from './dateFormatter'

describe('YYYY-MM-DDThh:mm:ssZ', () => {
  test('should be test', () => {
    const date = new Date('2021-05-05T21:12:00+10:00')
    const actual = dateFormatter['YYYY-MM-DDThh:mm:ss+09:00'](date)
    expect(actual).toBe('2021-05-05T20:12:00+09:00')
  })
})
