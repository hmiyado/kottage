import Error from '../components/plurals/error/error'
import OneColumn from '../components/plurals/template/onecolumn/onecolumn'

export default function Error500() {
  return (
    <OneColumn>
      <Error
        title="500 - Internal Server Error"
        description="予期しないエラーが発生しました"
      />
    </OneColumn>
  )
}
