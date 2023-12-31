import { formatISO } from 'date-fns'

export const dateFormatter = {
  'YYYY-MM-DDThh:mm:ss+09:00': (date: Date): string => {
    return formatISO(date)
  },
}
