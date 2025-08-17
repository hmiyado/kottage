import React from 'react'
import { UserDetail } from 'repository/openapi/generated'

export type User = UserDetail | null

const UserContext = React.createContext({
  user: null as User,
  updateUser: (user: User) => {
    // empty
  },
})
export default UserContext
