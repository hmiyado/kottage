import styles from '../styles/Contact.module.css'
import Sentence from './sentence'

export default function Contact() {
  return (
    <section className={styles.section}>
      <Sentence title="Skill">
        <>
          Android/Kotlin エンジニア。その他 Swift/iOS JavaScript, TypeScript,
          Ruby など。詳細は{' '}
          <a className={styles.link} href="https://github.com/hmiyado/resume">
            GitHub レジュメ
          </a>{' '}
          を参照。
        </>
      </Sentence>
      <Sentence title="Price">時間単価6000円〜。詳細要相談。</Sentence>
      <Sentence title="Contact">
        <a href="https://twitter.com/miyado20th" className={styles.link}>
          Twitter
        </a>
      </Sentence>
    </section>
  )
}
