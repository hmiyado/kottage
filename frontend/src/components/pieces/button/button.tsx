import React from 'react'

const styles = {
  button: [
    'text-primary-500 dark:text-primary-200 text-button tracking-wider',
    'disabled:text-light-on-surface-disabled dark:disabled:text-dark-on-surface-disabled',
    'h-2.0 px-1.0',
    'border-1 rounded',
    'border-light-outline-border dark:border-dark-outline-border',
  ].join(' '),
  imageButton: [
    'flex flex-wrap items-center',
    'text-primary-500 dark:text-primary-200  text-button tracking-wider',
    'disabled:text-light-on-surface-disabled dark:disabled:text-dark-on-surface-disabled',
    'h-2.0 pl-0.5 pr-0.5',
    'border-1 rounded',
    'border-light-outline-border dark:border-dark-outline-border',
  ].join(' '),
  image: [
    'fill-primary-500 dark:fill-primary-200',
    'inline-block',
    'w-1.5 h-1.5 mr-0.25',
  ].join(' '),
}

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
