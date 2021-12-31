import styles from './servicereference.module.css'
import Image from 'next/image'
import GitHubOnLightSurface from '../../../../public/components/sidemenu/servicereference/GitHub-Mark-64px.png'
import GitHubOnDarkSurface from '../../../../public/components/sidemenu/servicereference/GitHub-Mark-Light-64px.png'
import Qiita from '../../../../public/components/sidemenu/servicereference/qiita.png'
import TwitterOnLightSurface from '../../../../public/components/sidemenu/servicereference/twitter-on-light.png'
import TwitterOnDarkSurface from '../../../../public/components/sidemenu/servicereference/twitter-on-dark.png'
import RssOnLightSurface from '../../../../public/components/sidemenu/servicereference/rss-on-light.png'
import RssOnDarkSurface from '../../../../public/components/sidemenu/servicereference/rss-on-dark.png'

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
  {
    name: 'Feed',
    url: 'https://miyado.dev/feed/atom.xml',
    lightImage: RssOnLightSurface,
    darkImage: RssOnDarkSurface,
  },
]

export default function ServiceReference({
  theme = 'light',
  className,
}: {
  theme: 'light' | 'dark'
  className?: string
}): JSX.Element {
  const pickUpImageByTheme = (service: {
    lightImage: StaticImageData
    darkImage?: StaticImageData
  }): StaticImageData => {
    if (!service.darkImage) {
      return service.lightImage
    }
    if (theme === 'light') {
      return service.lightImage
    } else {
      return service.darkImage
    }
  }

  return (
    <div className={`${className} ${styles.icons}`}>
      {services.map((service, index) => {
        const iconSize = 32
        const src = pickUpImageByTheme(service)
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
