import Error from '../components/error/error'
import OneColumn from '../components/template/onecolumn/onecolumn'

export default function error404() {
  return (
    <OneColumn>
      <Error
        title="404 - Not Found"
        description="お探しのページは見つかりませんでした"
      />
    </OneColumn>
  )
}
