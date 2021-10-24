import KottageClient from '../kottageClient'
import Path from '../path'

export type Sign = (
  id: string,
  password: string
) => Promise<{ id: number; screenName: string }>

const signIn: Sign = async (id, password) => {
  const json = await KottageClient.post(Path.signIn, {
    screenName: id,
    password,
  })
  return {
    id: json['id'],
    screenName: json['screenName'],
  }
}

const signUp: Sign = async (id, password) => {
  const json = await KottageClient.post(Path.signUp, {
    screenName: id,
    password,
  })
  return {
    id: json['id'],
    screenName: json['screenName'],
  }
}

const UserRepository = {
  signIn,
  signUp,
}

export default UserRepository
