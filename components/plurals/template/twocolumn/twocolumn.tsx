import { useContext } from 'react'
import UserContext from '../../../../context/user'
import styles from './twocolumn.module.css'
import Layout from '../layout/layout'
import SideMenu from 'components/plurals/sidemenu/sidemenu'

export default function TwoColumn({
  mainColumnClassName,
  children,
}: {
  mainColumnClassName?: string
  children: JSX.Element
}) {
  return (
    <Layout>
      <div className={styles.container}>
        <main className={styles.mainColumn + ' ' + mainColumnClassName}>
          {children}
        </main>
        <SideMenu className={styles.sideColumn} />
      </div>
    </Layout>
  )
}
