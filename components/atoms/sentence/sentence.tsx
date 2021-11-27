import React from 'react'
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
        {typeof children === 'string'
          ? children.split('\n').map((line, index) => {
              return (
                <React.Fragment key={index}>
                  {line}
                  <br />
                </React.Fragment>
              )
            })
          : children}
      </section>
    </>
  )
}
