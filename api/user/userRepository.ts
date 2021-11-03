import KottageClient from '../kottageClient'

export type Sign = (
  id: string,
  password: string
) => Promise<{ id: number; screenName: string }>

const signIn: Sign = async (id, password) => {
  return KottageClient.signInPost({
    signInPostRequest: {
      screenName: id,
      password,
    },
  })
}

const signUp: Sign = async (id, password) => {
  return KottageClient.usersPost({
    usersPostRequest: {
      screenName: id,
      password,
    },
  })
}

const signOut = async () => {
  return KottageClient.signOutPost()
}

const UserRepository = {
  signIn,
  signUp,
  signOut,
}

export default UserRepository
