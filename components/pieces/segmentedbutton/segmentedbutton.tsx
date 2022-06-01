import styles from './segmentedbutton.module.css'

export type Segment = {
  label: string
  id: string
}

export type SegmentedButtonProps = {
  name: string
  segments: Segment[]
  onSelectedSegment: (segment: Segment) => void
}

export default function SegmentedButton({
  name,
  segments,
  onSelectedSegment,
}: SegmentedButtonProps): JSX.Element {
  return (
    <fieldset className={styles.container}>
      {segments.map((segment) => {
        return (
          <>
            <input
              type="radio"
              id={segment.id}
              name={name}
              onChange={() => onSelectedSegment(segment)}
            />
            <label htmlFor={segment.id}>{segment.label}</label>
          </>
        )
      })}
    </fieldset>
  )
}
