import Layout from '../components/layout/layout'
import styles from './rootpage.module.css'
import Profile from '../components/sidemenu/profile/profile'
import UserContext, { User } from '../context/user'
import UserForm from '../components/sidemenu/userform/userform'
import UserRepository, { Sign } from '../api/user/userRepository'
import { useContext, useEffect, useState } from 'react'
import Button from '../components/atoms/button/button'
import Plus from '../components/atoms/button/plus.svg'
import EntryForm from '../components/entryform/entryform'
import EntryRepository from '../api/entry/entryRepository'
import { Entries } from '../api/openapi/generated'
import Entry from '../components/entry/entry'
import { dateFormatter } from '../util/dateFormatter'

export async function getStaticProps() {
  try {
    const entries = await EntryRepository.getEntries()
    return {
      props: {
        entries: {
          items: entries.items?.map((v) => {
            const { dateTime, ...rest } = v
            return {
              dateTime: dateFormatter['YYYY-MM-DDThh:mm:ss'](
                new Date(dateTime)
              ),
              ...rest,
            }
          }),
        },
      },
    }
  } catch (e) {
    return {
      props: {},
    }
  }
}

export default function RootPage({ entries }: { entries: Entries }) {
  const { user, updateUser } = useContext(UserContext)
  const [showEntryForm, updateShowEntryForm] = useState(false)
  useEffect(() => {
    UserRepository.current()
      .then((currentUser) => updateUser(currentUser))
      .catch(() => updateUser(null))
  }, [])

  const entryForm = (_user: User, _showEntryForm: boolean) => {
    if (_user === null) {
      return null
    }
    if (_showEntryForm) {
      return (
        <EntryForm
          onSubmit={(title, body) => EntryRepository.createEntry(title, body)}
          onCancel={() => updateShowEntryForm(false)}
        />
      )
    } else {
      return (
        <Button
          text="Entry"
          Icon={Plus}
          onClick={() => updateShowEntryForm(true)}
        />
      )
    }
  }

  return (
    <Layout>
      <div className={styles.container}>
        <div className={styles.mainColumn}>
          {entryForm(user, showEntryForm)}
          {entries.items?.map((entry, index) => {
            return (
              <Entry
                key={index}
                title={entry.title}
                time={String(entry.dateTime)}
                author={entry.author.screenName}
              >
                {entry.body}
              </Entry>
            )
          })}
        </div>
        <div className={styles.sideColumn}>
          <Profile />

          <UserForm
            screenName={user?.screenName}
            onSignUpClicked={signAndUpdateUser(
              UserRepository.signUp,
              updateUser
            )}
            onSignInClicked={signAndUpdateUser(
              UserRepository.signIn,
              updateUser
            )}
            onSignOutClicked={signOut(updateUser)}
          />
        </div>
      </div>
    </Layout>
  )
}

const signAndUpdateUser = (
  sign: Sign,
  updateUser: (newUser: User) => void
): ((id: string, password: string) => Promise<void>) => {
  return async (id, password) => {
    try {
      const user = await sign(id, password)
      updateUser(user)
    } catch {
      updateUser(null)
    }
  }
}

const signOut = (
  updateUser: (newUser: User) => void
): (() => Promise<void>) => {
  return async () => {
    await UserRepository.signOut()
    updateUser(null)
  }
}
