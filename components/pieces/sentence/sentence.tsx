import React from 'react'
import ReactMarkdown from 'react-markdown'
import rehypeHighlight from 'rehype-highlight'
import 'highlight.js/styles/github-dark.css'
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
          <ReactMarkdown rehypePlugins={[rehypeHighlight]}>
            {children}
          </ReactMarkdown>
        ) : (
          children
        )}
      </section>
    </>
  )
}
