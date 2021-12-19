const https = require('https');
const slack_webhook = process.env["SlackIncomingWebhook"];

module.exports = async function postToSlack(message) {
    console.log('post to slack');
    const payload = {
        channel: '#kottage-log',
        username: 'kottage-log-lambda',
        text: message
    }
    const stringPayload = JSON.stringify(payload)
    // https://nodejs.org/api/https.html#httpsrequestoptions-callback
    console.log(stringPayload)
    const requestPromise = new Promise((resolve, reject) => {
        const options = {
            headers: {
                'content-type': 'application/x-www-form-urlencoded',
            },
            method: 'POST'
        }
        const request = https.request(slack_webhook, options,(res) => {
          const statusCode = parseInt(res.statusCode)
          if (statusCode >= 200 && statusCode < 300) {
            console.log('success to post to slack')
            resolve()
          } else {
            console.log('fail to post to slack')
            reject()
          }
        });
        request.write(encodeURI('payload='+stringPayload));
        request.end();
    })
    console.log('waiting response')
    return requestPromise
}
