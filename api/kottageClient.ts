const KottageClient = {
  post: async (endpoint: string, body: object): Promise<any> => {
    const request = new Request(`http://localhost:8080/${endpoint}`, {
      method: 'POST',
      headers: new Headers({
        'Content-Type': 'application/json',
      }),
      body: JSON.stringify(body),
    })
    const response = await fetch(request)
    return response.json()
  },
}

export default KottageClient
