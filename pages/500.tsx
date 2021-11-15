import Error from '../components/error/error'
import Layout from '../components/layout/layout'

export default function Error500() {
  return (
    <Layout>
      <Error
        title="500 - Internal Server Error"
        description="予期しないエラーが発生しました"
      />
    </Layout>
  )
}
