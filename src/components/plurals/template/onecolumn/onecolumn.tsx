import styles from './onecolumn.module.css'
import Layout from '../layout/layout'

export default function OneColumn({
  children,
}: {
  children: React.JSX.Element
}) {
  return (
    <Layout>
      <main className={styles.main}>{children}</main>
    </Layout>
  )
}
