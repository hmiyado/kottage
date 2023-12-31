import { dateFormatter } from './dateFormatter'

describe('YYYY-MM-DDThh:mm:ssZ', () => {
  test('should be test', () => {
    const date = new Date(2021, 4, 5, 21, 12, 0, 0)
    const actual = dateFormatter['YYYY-MM-DDThh:mm:ss+09:00'](date)
    expect(actual).toBe('2021-05-05T21:12:00+09:00')
  })
})
