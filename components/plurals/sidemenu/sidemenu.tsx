import { useEffect, useState } from 'react'
import Profile from './profile/profile'
import ServiceReference from './servicereference/servicereference'
import styles from './sidemenu.module.css'
import UserForm, { UserFormProps } from './userform/userform'

export type SideMenuProps = UserFormProps & { className?: string }

export default function SideMenu({
  className,
  onSignUpClicked,
  onSignInClicked,
  onSignOutClicked,
}: SideMenuProps): JSX.Element {
  const [showUserForm, updateShowUserForm] = useState(false)
  const [theme, setTheme] = useState<'light' | 'dark'>('light')

  useEffect(() => {
    const initialThemeMediaQuery = window.matchMedia(
      '(prefers-color-scheme: dark)'
    )
    const setThemeByMediaQuery = (isDark: boolean) => {
      setTheme(isDark ? 'dark' : 'light')
    }
    initialThemeMediaQuery.addEventListener('change', (e) => {
      setThemeByMediaQuery(e.matches)
    })
    setThemeByMediaQuery(initialThemeMediaQuery.matches)
  }, [setTheme])

  return (
    <aside className={`${styles.container} ${className}`}>
      <div
        onClick={() => updateShowUserForm((pre) => !pre)}
        className={styles.profile}
      >
        <Profile />
      </div>

      <ServiceReference theme={theme} className={styles.servicereference} />

      <div className={styles.userform}>
        {showUserForm ? (
          <UserForm
            onSignUpClicked={onSignUpClicked}
            onSignInClicked={onSignInClicked}
            onSignOutClicked={onSignOutClicked}
          />
        ) : null}
      </div>
    </aside>
  )
}
