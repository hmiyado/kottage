import React from 'react'
import styles from './segmentedbutton.module.css'

export interface Segment {
  label: string
  id: string
}

export type SegmentedButtonProps<T extends Segment> = {
  name: string
  segments: T[]
  defaultSegmentId?: T['id']
  onSelectedSegment: (segment: T) => void
}

export default function SegmentedButton<T extends Segment>({
  name,
  segments,
  defaultSegmentId,
  onSelectedSegment,
}: SegmentedButtonProps<T>): JSX.Element {
  return (
    <fieldset className={styles.container}>
      {segments.map((segment) => {
        return (
          <React.Fragment key={segment.id}>
            <input
              type="radio"
              id={segment.id}
              name={name}
              defaultChecked={segment.id === defaultSegmentId}
              onChange={() => onSelectedSegment(segment)}
            />
            <label htmlFor={segment.id}>{segment.label}</label>
          </React.Fragment>
        )
      })}
    </fieldset>
  )
}
