import { Constants } from '../util/constants'
import {
  Configuration,
  DefaultApi,
  Middleware,
  RequestContext,
  ResponseContext,
  FetchParams,
  Error403FromJSON,
  Error403CauseKindEnum,
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
      return response
        .json()
        .then((payload) => Promise.resolve(Error403FromJSON(payload)))
        .then((payload) => {
          if (payload.cause?.kind === Error403CauseKindEnum.CsrfTokenRequired) {
            // retry fetch only when csrf token is required
            return fetch(
              url,
              this.requestInitWithCsrfToken(init, currentCsrfToken),
            )
          }
          return Promise.reject()
        })
    }
    return Promise.resolve()
  }

  private requestInitWithCsrfToken(
    init: RequestInit,
    csrfToken: string,
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

class LoggerMiddleware implements Middleware {
  pre(context: RequestContext): Promise<FetchParams | void> {
    const { url, init } = context
    console.log(`request ${init.method} ${url}`)
    return Promise.resolve()
  }

  post(context: ResponseContext): Promise<Response | void> {
    const { url, init, response } = context
    console.log(`response ${response.status} from ${init.method} ${url}`)
    return Promise.resolve()
  }
}

class OpenApi extends DefaultApi {
  constructor() {
    super(
      new Configuration({
        basePath: `${Constants.backendUrl}/api/v1`,
        credentials: 'include',
        middleware: [
          new CsrfTokenMiddleware(),
          // new LoggerMiddleware()
        ],
      }),
    )
  }
}

const KottageClient = new OpenApi()

export default KottageClient
