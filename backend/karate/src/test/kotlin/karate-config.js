function config() {
  var config = {
    baseUrl: 'http://localhost:8080/api/v1',
    origin: 'http://localhost:3000',
  };
  // don't waste time waiting for a connection or if servers don't respond within 5 seconds
  karate.configure('connectTimeout', 5000);
  karate.configure('readTimeout', 5000);
  karate.configure('headers', {
    Origin: config.origin
  });
  return config;
}
