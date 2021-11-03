import UserRepository from './userRepository'

describe('userRepository', () => {
  test('can sign in', async () => {
    const actual = await UserRepository.signIn('id', 'password')

    expect(actual).toStrictEqual({
      id: 1,
      screenName: 'signIn',
    })
  })
  test('can sign up', async () => {
    const actual = await UserRepository.signUp('id', 'password')

    expect(actual).toStrictEqual({
      id: 1,
      screenName: 'signUp',
    })
  })
  test('can sign out', async () => {
    const actual = await UserRepository.signOut()

    expect(actual).toBeUndefined
  })
})
