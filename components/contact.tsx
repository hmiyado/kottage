import styles from '../styles/Contact.module.css'

export default function Contact() {
  return (
    <section className={styles.section}>
      <h2 className={styles.headline}>Skill</h2>
      <p className={styles.body}>
        Android/Kotlin エンジニア。その他 Swift/iOS JavaScript, TypeScript, Ruby
        など。詳細は{' '}
        <a href="https://github.com/hmiyado/resume">GitHub レジュメ</a> を参照。
      </p>
      <h2 className={styles.headline}>Price</h2>
      <p className={styles.body}>時間単価6000円〜。詳細要相談。</p>
      <h2 className={styles.headline}>Contact</h2>
      <p className={styles.body}>
        <a href="https://twitter.com/messages/compose?recipient_id=miyado20th">
          Twitter DM
        </a>
      </p>
    </section>
  )
}
