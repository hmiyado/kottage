import { describe, expect, test } from 'vitest'
import UserRepository from './userRepository'

describe('userRepository', () => {
  test('can sign in', async () => {
    const actual = await UserRepository.signIn('id', 'password')

    expect(actual).toStrictEqual({
      id: 1,
      screenName: 'signIn',
      accountLinks: [],
    })
  })
  test('can sign up', async () => {
    const actual = await UserRepository.signUp('id', 'password')

    expect(actual).toStrictEqual({
      id: 1,
      screenName: 'signUp',
      accountLinks: [],
    })
  })
  test('can sign out', async () => {
    const actual = await UserRepository.signOut()

    expect(actual).toBeUndefined
  })
  test('can get current user', async () => {
    const actual = await UserRepository.current()

    expect(actual).toStrictEqual({
      id: 1,
      screenName: 'users-current',
      accountLinks: [],
    })
  })
})
