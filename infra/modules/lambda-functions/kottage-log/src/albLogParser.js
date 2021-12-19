// https://docs.aws.amazon.com/ja_jp/elasticloadbalancing/latest/application/load-balancer-access-logs.html
const albLogKeys = [
    'type',
    'time',
    'elb',
    'client:port',
    'target:port',
    'request_processing_time',
    'target_processing_time',
    'response_processing_time',
    'elb_status_code',
    'target_status_code',
    'received_bytes',
    'sent_bytes',
    '"request"',
    '"user_agent"',
    'ssl_cipher',
    'ssl_protocol',
    'target_group_arn',
    '"trace_id"',
    '"domain_name"',
    'chosen_cert_arn"',
    'matched_rule_priority',
    'request_creation_time',
    "actions_executed:",
    '"redirect_url"',
    '"error_reason"',
    '"target:port_list"',
    '"classification',
    '"classification_reason"',
]

function parseValues(line) {
    let tmpValue = ''
    let parsedValues = []
    let isInDoubleQuotedValue = false
    for(const char of line) {
        if (char == ' ' && !isInDoubleQuotedValue) {
            parsedValues.push(tmpValue)
            tmpValue = ''
            continue
        }
        if (char == '"') {
            isInDoubleQuotedValue = !isInDoubleQuotedValue
        }

        tmpValue += char
    }
    return parsedValues
}

function combineKeyValues(keys, values) {
    const keyLength = keys.length
    const valueLength = values.length
    if(keyLength != valueLength) {
        console.error('keys length #%d and values length #%d does not match. length is matched to min length', keyLength, valueLength)
    }
    let combined = {}
    for(let i = 0; i < Math.min(keyLength, valueLength); i++) {
        combined[keys[i]] = values[i]
    }
    return combined
}

function parseLogObjects(log) {
    const lines = log.split("\n")
    let logObjects = []
    for(const line of lines) {
        const values = parseValues(line)
        const combined = combineKeyValues(albLogKeys, values)
        logObjects.push(combined)
    }
    return logObjects;
}

function filterLogObjects(logObjects) {
    const validAlbLogKeys = [
        'time',
        'elb_status_code',
        '"request"',
        '"user_agent"',
    ]
    let filteredLogObjects = []
    for(const log of logObjects) {
        let object = {}
        for(const key of validAlbLogKeys) {
            object[key] = log[key]
        }
        filteredLogObjects.push(object)
    }
    return filteredLogObjects
}

module.exports = function convertToReadableLog(log) {
    const logObjects = parseLogObjects(log)
    const filteredLogObjects = filterLogObjects(logObjects)
    let readableLog = ''
    for(const logObject of filteredLogObjects) {
        readableLog += Object.values(logObject).join(' ') + '\n'
    }
    return readableLog
}
