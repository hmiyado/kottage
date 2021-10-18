import KottageClient from './kottageClient'

export type Sign = (
  id: string,
  password: string
) => Promise<{ id: number; screenName: string }>

const signIn: Sign = (id, password) => {
  return KottageClient.post('api/v1/signIn', {
    screenName: id,
    password,
  }).then((json) => {
    return {
      id: json['id'],
      screenName: json['screenName'],
    }
  })
}

const signUp: Sign = (id, password) => {
  return KottageClient.post('api/v1/users', {
    screenName: id,
    password,
  }).then((json) => {
    return {
      id: json['id'],
      screenName: json['screenName'],
    }
  })
}

const UserRepository = {
  signIn,
  signUp,
}

export default UserRepository
