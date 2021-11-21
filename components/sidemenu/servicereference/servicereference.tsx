import styles from './servicereference.module.css'
import Image from 'next/image'
import Script from 'next/script'
import GitHubOnLightSurface from '../../../public/components/sidemenu/servicereference/GitHub-Mark-64px.png'
import GitHubOnDarkSurface from '../../../public/components/sidemenu/servicereference/GitHub-Mark-Light-64px.png'
import Qiita from '../../../public/components/sidemenu/servicereference/qiita.png'
import TwitterOnLightSurface from '../../../public/components/sidemenu/servicereference/twitter-on-light.png'
import TwitterOnDarkSurface from '../../../public/components/sidemenu/servicereference/twitter-on-dark.png'

function provideImageByTheme(
  theme: 'light' | 'dark',
  alt: string,
  lightImage: StaticImageData,
  darkImage?: StaticImageData
): JSX.Element {
  const iconSize = 32
  const src =
    theme === 'light' ? lightImage : darkImage ? darkImage : lightImage
  return <Image src={src} alt={alt} width={iconSize} height={iconSize} />
}

export default function ServiceReference({
  theme = 'light',
}: {
  theme: 'light' | 'dark'
}): JSX.Element {
  return (
    <div>
      <div className={styles.icons}>
        <a href="https://github.com/hmiyado" target="_blank" rel="noreferrer">
          {provideImageByTheme(
            theme,
            'GitHub',
            GitHubOnLightSurface,
            GitHubOnDarkSurface
          )}
        </a>
        <a href="https://qiita.com/hmiyado" target="_blank" rel="noreferrer">
          {provideImageByTheme(theme, 'Qiita', Qiita)}
        </a>
        <a
          href="https://twitter.com/miyado20th"
          target="_blank"
          rel="noreferrer"
        >
          {provideImageByTheme(
            theme,
            'Twitter',
            TwitterOnLightSurface,
            TwitterOnDarkSurface
          )}
        </a>
      </div>
      <Script src="https://platform.twitter.com/widgets.js" />
      {/* Twitter script replace a tag to iframe, so theme does not switch if you do not reload */}
      <a
        className="twitter-timeline"
        data-lang="en"
        data-width="240"
        data-height="360"
        data-dnt="true"
        data-theme={theme}
        href="https://twitter.com/miyado20th?ref_src=twsrc%5Etfw"
      >
        Tweets by miyado20th
      </a>
    </div>
  )
}
