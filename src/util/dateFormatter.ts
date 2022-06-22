export const dateFormatter = {
  'YYYY-MM-DDThh:mm:ss+0900': (date: Date): string => {
    const YYYY = date.getFullYear()
    const MM = (date.getMonth() + 1).toString().padStart(2, '0')
    const DD = date.getDate().toString().padStart(2, '0')
    const hh = date.getHours().toString().padStart(2, '0')
    const mm = date.getMinutes().toString().padStart(2, '0')
    const ss = date.getSeconds().toString().padStart(2, '0')
    return `${YYYY}-${MM}-${DD}T${hh}:${mm}:${ss}+0900`
  },
}
