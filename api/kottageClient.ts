import {
  Configuration,
  DefaultApi,
  Middleware,
  RequestContext,
  ResponseContext,
  FetchParams,
} from './openapi/generated'

class CsrfTokenMiddleware implements Middleware {
  private CsrfTokenHeaderKey = 'X-CSRF-Token'
  private csrfToken: string | null = null

  pre(context: RequestContext): Promise<FetchParams | void> {
    const currentCsrfToken = this.csrfToken
    if (currentCsrfToken == null) {
      return Promise.resolve()
    }

    const { url, init } = context
    const headers = Object.assign(init.headers ?? {}, {
      [this.CsrfTokenHeaderKey]: this.csrfToken,
    })
    return Promise.resolve({
      url,
      init: {
        ...init,
        headers,
      },
    })
  }

  post(context: ResponseContext): Promise<Response | void> {
    const { response } = context
    this.csrfToken = response.headers.get(this.CsrfTokenHeaderKey)
    return Promise.resolve()
  }
}

class OpenApi extends DefaultApi {
  constructor() {
    super(
      new Configuration({
        basePath: process.env.NEXT_PUBLIC_KOTTAGE_BASE_PATH,
        credentials: 'include',
        middleware: [new CsrfTokenMiddleware()],
      })
    )
  }
}

const KottageClient = new OpenApi()

export default KottageClient
