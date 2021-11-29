export function rangeArray(
  start: number,
  endExclusive: number,
  step: number = 1
): number[] {
  const array: number[] = []
  for (let i = start; i < endExclusive; i += step) {
    array.push(i)
  }
  return array
}
