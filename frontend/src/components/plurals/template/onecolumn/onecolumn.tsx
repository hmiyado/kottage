import Layout from '../layout/layout'

const styles = {
  main: ['flex-1'].join(' '),
}

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
