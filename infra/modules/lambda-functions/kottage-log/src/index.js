const convertToReadableLog = require('./albLogParser')
const postToSlack = require('./slack')
const zlib = require('zlib');
const aws = require('aws-sdk');
const s3 = new aws.S3({ apiVersion: '2006-03-01' });


exports.handler = async (event, context) => {
    //console.log('Received event:', JSON.stringify(event, null, 2));

    // Get the object from the event and show its content type
    // https://docs.aws.amazon.com/lambda/latest/dg/with-s3.html
    const bucket = event.Records[0].s3.bucket.name;
    const key = decodeURIComponent(event.Records[0].s3.object.key.replace(/\+/g, ' '));
    const params = {
        Bucket: bucket,
        Key: key,
    };
    try {
        // https://docs.aws.amazon.com/AWSJavaScriptSDK/latest/AWS/S3.html#getObject-property
        const { Body } = await s3.getObject(params).promise();
        const unzipped = zlib.unzipSync(Body);
        const result = decodeURIComponent(unzipped).toString('utf-8');
        const log = convertToReadableLog(result)
        await postToSlack(log);
        return 'success';
    } catch (err) {
        console.error(err);
        const message = `Error getting object ${key} from bucket ${bucket}. Make sure they exist and your bucket is in the same region as this function.`;
        return message;
    }
};
