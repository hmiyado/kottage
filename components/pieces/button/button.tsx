import React from 'react'
import styles from './button.module.css'

export default function Button({
  text,
  Icon,
  ...attributes
}: {
  text: string
  Icon?: React.FC<React.ImgHTMLAttributes<HTMLImageElement>>
} & React.ButtonHTMLAttributes<HTMLButtonElement>) {
  if (Icon) {
    return (
      <button className={styles.imageButton} {...attributes}>
        <Icon className={styles.image} />
        {text}
      </button>
    )
  }
  return (
    <button className={styles.button} {...attributes}>
      {text}
    </button>
  )
}
