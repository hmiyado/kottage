import styles from './servicereference.module.css'
import Image from 'next/image'
import Script from 'next/script'
import GitHubOnLightSurface from '../../../public/components/sidemenu/servicereference/GitHub-Mark-64px.png'
import GitHubOnDarkSurface from '../../../public/components/sidemenu/servicereference/GitHub-Mark-Light-64px.png'
import Qiita from '../../../public/components/sidemenu/servicereference/qiita.png'
import TwitterOnLightSurface from '../../../public/components/sidemenu/servicereference/twitter-on-light.png'
import TwitterOnDarkSurface from '../../../public/components/sidemenu/servicereference/twitter-on-dark.png'

const services = [
  {
    name: 'GitHub',
    url: 'https://github.com/hmiyado',
    lightImage: GitHubOnLightSurface,
    darkImage: GitHubOnDarkSurface,
  },
  {
    name: 'Qiita',
    url: 'https://qiita.com/hmiyado',
    lightImage: Qiita,
    darkImage: Qiita,
  },
  {
    name: 'Twitter',
    url: 'https://twitter.com/miyado20th',
    lightImage: TwitterOnLightSurface,
    darkImage: TwitterOnDarkSurface,
  },
]

export default function ServiceReference({
  theme = 'light',
  className,
}: {
  theme: 'light' | 'dark'
  className?: string
}): JSX.Element {
  return (
    <div className={`${className} ${styles.icons}`}>
      {services.map((service, index) => {
        const iconSize = 32
        const src =
          theme === 'light'
            ? service.lightImage
            : service.darkImage
            ? service.darkImage
            : service.lightImage
        return (
          <a key={index} href={service.url} target="_blank" rel="noreferrer">
            <Image
              src={src}
              alt={service.name}
              width={iconSize}
              height={iconSize}
            />
          </a>
        )
      })}
    </div>
  )
}
