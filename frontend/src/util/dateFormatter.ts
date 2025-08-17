export const dateFormatter = {
  'YYYY-MM-DDThh:mm:ss+09:00': (date: Date): string => {
    const dateWith0900Offset = new Date(date.getTime() + 9 * 60 * 60 * 1000)
    const dateWith0900OffsetISO = dateWith0900Offset.toISOString()
    // ISO string with utc timezone to ISO string with +09:00 timezone
    const dateISOwithTimezone0900 = dateWith0900OffsetISO.replace('Z', '+09:00')
    // ISO string without milliseconds
    const dateISOwithoutMilliseconds = dateISOwithTimezone0900.replace(
      /\.\d{3}/,
      '',
    )
    return dateISOwithoutMilliseconds
  },
}
