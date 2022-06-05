import React from 'react'
import ReactMarkdown from 'react-markdown'
import styles from './sentence.module.css'

export default function Sentence({
  title,
  children,
}: {
  title: JSX.Element | string
  children: JSX.Element | string
}) {
  return (
    <>
      <h2 className={styles.headline}>{title}</h2>
      <section className={styles.body}>
        {typeof children === 'string' ? (
          <ReactMarkdown>{children}</ReactMarkdown>
        ) : (
          children
        )}
      </section>
    </>
  )
}
