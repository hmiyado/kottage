import Button from 'components/pieces/button/button'
import ErrorBoundary from 'components/plurals/errorboundary/errorboundary'
import { Suspense, useEffect, useState } from 'react'
import Profile from './profile/profile'
import ServiceReference from './servicereference/servicereference'
import styles from './sidemenu.module.css'
import UserForm from './userform/userform'
import UserFormLoading from './userform/userformloading/userformloading'

export type SideMenuProps = { className?: string }

export default function SideMenu({ className }: SideMenuProps): JSX.Element {
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

  const handleErrorBoundaryFallback = (
    error: Error,
    resetError: () => void
  ) => {
    return (
      <div>
        <h1>エラーが発生しました。</h1>
        <Button text="戻る" onClick={resetError}></Button>
      </div>
    )
  }

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
          <ErrorBoundary fallback={handleErrorBoundaryFallback}>
            <Suspense fallback={<UserFormLoading />}>
              <UserForm />
            </Suspense>
          </ErrorBoundary>
        ) : null}
      </div>
    </aside>
  )
}
