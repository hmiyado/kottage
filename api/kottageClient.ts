import { Configuration, DefaultApi } from './openapi/generated'

class OpenApi extends DefaultApi {
  constructor() {
    super(
      new Configuration({
        credentials: 'include',
      })
    )
  }
}

const KottageClient = new OpenApi()

export default KottageClient
