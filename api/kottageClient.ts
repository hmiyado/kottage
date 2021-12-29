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
    return Promise.resolve({
      url,
      init: this.requestInitWithCsrfToken(init, currentCsrfToken),
    })
  }

  post(context: ResponseContext): Promise<Response | void> {
    const { fetch, url, init, response } = context
    this.csrfToken = response.headers.get(this.CsrfTokenHeaderKey)
    const currentCsrfToken = this.csrfToken
    if (currentCsrfToken == null) {
      return Promise.resolve()
    }
    if (response.status == 403) {
      // when response status is 403, retry with csrf token
      return fetch(url, this.requestInitWithCsrfToken(init, currentCsrfToken))
    }
    return Promise.resolve()
  }

  private requestInitWithCsrfToken(
    init: RequestInit,
    csrfToken: string
  ): RequestInit {
    const headers = Object.assign(init.headers ?? {}, {
      [this.CsrfTokenHeaderKey]: csrfToken,
    })
    return {
      ...init,
      headers,
    }
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
