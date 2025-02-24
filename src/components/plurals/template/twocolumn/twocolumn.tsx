import Layout from '../layout/layout'
import SideMenu from 'components/plurals/sidemenu/sidemenu'

const styles = {
  container: [
    'flex flex-col lg:flex-row flex-wrap',
    'lg:space-x-2.0',
    'px-1.0 lg:px-2.0 py-1.0',
  ].join(' '),
  mainColumn: ['w-full', 'order-2 lg:order-2', 'flex-1'].join(' '),
  sideColumn: ['order-1 lg:order-2', 'w-full lg:w-15'].join(' '),
}

export default function TwoColumn({
  mainColumnClassName,
  children,
}: {
  mainColumnClassName?: string
  children: React.JSX.Element
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
