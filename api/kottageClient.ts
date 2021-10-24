const KottageClient = {
  post: async (endpoint: string, body: object): Promise<any> => {
    const response = await fetch(`http://localhost:8080/${endpoint}`, {
      method: 'POST',
      headers: new Headers({
        'Content-Type': 'application/json',
      }),
      body: JSON.stringify(body),
    })
    return response.json()
  },
}

export default KottageClient
