import Error from '../components/error/error'
import Layout from '../components/layout/layout'

export default function error404() {
  return (
    <Layout>
      <Error
        title="404 - Not Found"
        description="お探しのページは見つかりませんでした"
      />
    </Layout>
  )
}
