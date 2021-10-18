const KottageClient = {
  post: (endpoint: string, body: object): Promise<any> => {
    const request = new Request(`http://localhost:8080/${endpoint}`, {
      method: 'POST',
      headers: new Headers({
        'Content-Type': 'application/json',
      }),
      body: JSON.stringify(body),
    })
    return fetch(request).then((response) => {
      return response.json()
    })
  },
}

export default KottageClient
