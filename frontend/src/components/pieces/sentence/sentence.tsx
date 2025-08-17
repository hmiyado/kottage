import React from 'react'
import ReactMarkdown from 'react-markdown'
import remarkBreaks from 'remark-breaks'
import rehypeHighlight from 'rehype-highlight'
import 'highlight.js/styles/github-dark.css'

const styles = {
  headline: [
    'text-headline5 dark:text-dark-on-surface mt-2.0 first:mt-0.5',
  ].join(' '),
  body: [
    'text-light-on-surface dark:text-dark-on-surface',
    'text-body1 dark:text-dark-on-surface mt-0.5',
  ].join(' '),
}

export default function Sentence({
  title,
  children,
}: {
  title: React.JSX.Element | string
  children: React.JSX.Element | string
}) {
  return (
    <>
      <h2 className={styles.headline}>{title}</h2>
      <section className={styles.body}>
        {typeof children === 'string' ? (
          <ReactMarkdown
            remarkPlugins={[remarkBreaks]}
            rehypePlugins={[rehypeHighlight]}
          >
            {children}
          </ReactMarkdown>
        ) : (
          children
        )}
      </section>
    </>
  )
}
