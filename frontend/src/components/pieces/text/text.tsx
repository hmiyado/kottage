const colorStyles = {
  onSurface: ['text-light-on-surface dark:text-dark-on-surface'].join(' '),
  surfaceOverlay: [
    'text-light-surface-overlay dark:text-dark-surface-overlay',
  ].join(' '),
  onSurfaceMedium: [
    'text-light-on-surface-medium-text dark:text-dark-on-surface-medium-text',
  ].join(''),
}

const sizeStyles = {
  h1: ['text-headline1'].join(' '),
  h3: ['text-headline3'].join(' '),
  h5: ['text-headline5'].join(' '),
  body1: ['text-body1'].join(' '),
  button: ['text-button'].join(' '),
  caption: ['text-caption'].join(' '),
}

export type TextSize = 'h1' | 'h3' | 'h5' | 'body1' | 'button' | 'caption'
export type TextColor = 'onSurface' | 'surfaceOverlay' | 'onSurfaceMedium'

function getSizeClass(size?: TextSize): string {
  if (size === undefined) {
    return sizeStyles.body1
  }
  return sizeStyles[size]
}

function getColorClass(color?: TextColor): string {
  if (color === undefined) {
    return colorStyles.onSurface
  }
  return colorStyles[color]
}

export default function Text({
  size,
  color,
  children,
}: {
  size?: TextSize
  color?: TextColor
  children?: string
}): React.JSX.Element {
  return (
    <div className={`${getSizeClass(size)} ${getColorClass(color)}`}>
      {children}
    </div>
  )
}
