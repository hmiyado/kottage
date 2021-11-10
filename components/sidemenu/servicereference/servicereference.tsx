import styles from './servicereference.module.css'
import Image from 'next/image'
import Script from 'next/script'
import GitHub from '../../../public/components/sidemenu/servicereference/GitHub-Mark-64px.png'
import Qiita from '../../../public/components/sidemenu/servicereference/qiita.png'

export default function ServiceReference(): JSX.Element {
  const iconSize = 32
  return (
    <div>
      <div>
        <a href="https://github.com/hmiyado" target="_blank" rel="noreferrer">
          <Image src={GitHub} alt="GitHub" width={iconSize} height={iconSize} />
        </a>
        <a href="https://qiita.com/hmiyado" target="_blank" rel="noreferrer">
          <Image src={Qiita} alt="qiita" width={iconSize} height={iconSize} />
        </a>
      </div>
      <Script src="https://platform.twitter.com/widgets.js" />
      <a
        className="twitter-timeline"
        data-lang="ja"
        data-width="240"
        data-height="360"
        data-dnt="true"
        data-theme="light"
        href="https://twitter.com/miyado20th?ref_src=twsrc%5Etfw"
      >
        Tweets by miyado20th
      </a>
    </div>
  )
}
