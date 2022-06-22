import React from 'react'

export type User = {
  id: number
  screenName: string
} | null

const UserContext = React.createContext({
  user: null as User,
  updateUser: (user: User) => {
    // empty
  },
})
export default UserContext
