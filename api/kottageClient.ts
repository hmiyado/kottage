import { Configuration, DefaultApi } from './openapi/generated'

class OpenApi extends DefaultApi {
  constructor() {
    super(
      new Configuration({
        basePath: process.env.NEXT_PUBLIC_KOTTAGE_BASE_URL,
        credentials: 'include',
      })
    )
  }
}

const KottageClient = new OpenApi()

export default KottageClient
