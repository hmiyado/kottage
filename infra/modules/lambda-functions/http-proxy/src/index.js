const http = require('http');
const kottageHost = process.env["KottageHost"];

/**
 * @param headers {object}
 */
function filterHeaders(headers) {
  const availableHeaders = [
    // request
    'accept',
    'accept-encoding',
    'accept-language',
    'access-control-request-headers',
    'access-control-request-method',
    'cookie',
    'origin',
    'referer',
    'sec-ch-ua',
    'sec-ch-ua-mobile',
    'sec-ch-ua-platform',
    'sec-fetch-dest',
    'sec-fetch-mode',
    'sec-fetch-site',
    'user-agent',
    'x-csrf-token',
    // response
    'access-control-allow-credentials',
    'access-control-allow-headers',
    'access-control-allow-methods',
    'access-control-allow-origin',
    'access-control-expose-headers',
    'access-control-max-age',
    'content-length',
    'content-type',
    'content-security-policy',
    'date',
    'set-cookie',
    'vary',
    'x-content-type-options',
    'x-csrf-token',
    'x-frame-options',
    'x-xss-protection',
    ]
  const result = {}
  for (const key in headers) {
    if (availableHeaders.indexOf(key.toLowerCase()) >= 0) {
      if (key === 'set-cookie') {
        result[key] = headers[key][0]
        continue
      }
      result[key] = headers[key]
    }
  }
  return result
}

/**
 * https://nodejs.org/api/https.html.
 */
exports.handler = async (event, context) => {
  return new Promise((resolve, reject) => {
    console.log('event=',event)
    const {
      rawPath,
      rawQueryString,
      requestContext,
      cookies,
      headers,
      body,
    } = event
    const options = {
      host: kottageHost,
      port: 8080,
      path: rawPath + (rawQueryString === "" ? "" : "?" + rawQueryString),
      headers: {
        cookie: cookies ? cookies.join("; ") : "",
        ...filterHeaders(headers),
      },
      method: requestContext.http.method,
      timeout: 1000,
    }
    console.log('request=', {
      options,
      body,
    })
    const request = http.request(options,(res)=> {
      console.log('response=',{
        statusCode: res.statusCode,
        headers: res.headers,
        request: {
          protocol: res.req.protocol,
          host: res.req.host,
          path: res.req.path,
          method: res.req.method,
        }
      })

      let rawData = '';
      res.on('data', (chunk) => { rawData += chunk; });
      res.on('end', () => {
        try {
          // https://docs.aws.amazon.com/apigateway/latest/developerguide/http-api-develop-integrations-lambda.html
          const response = {
            statusCode: res.statusCode,
            headers: filterHeaders(res.headers),
            body: rawData
          }
          console.log(response)
          resolve(response)
        } catch (e) {
          console.error(e.message);
          reject(e)
        }
      });
    })
    request.on('error', (e) => {
      const response = {
        statusCode: 500,
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          status: 'Internal Server Error',
          event: event
        })
      }
      console.log('response=',response)
      reject(response)
    });
    if (body) {
     request.write(body)
    }
    request.end()
  })
};
