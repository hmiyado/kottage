import colorStyles from './textColor.module.css'
import opacityStyles from './textOpacity.module.css'
import sizeStyles from './textSize.module.css'

export type TextSize = 'h1' | 'h3' | 'h5' | 'body1' | 'button' | 'caption'
export type TextColor = 'onSurface' | 'surfaceOverlay'
export type TextOpacity =
  | 'onSurfaceHigh'
  | 'onSurfaceMedium'
  | 'onSurfaceDisabled'

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

function getOpacityClass(opacity?: TextOpacity): string {
  if (opacity === undefined) {
    return opacityStyles.onSurfaceMedium
  }
  return opacityStyles[opacity]
}

export default function Text({
  size,
  color,
  opacity,
  children,
}: {
  size?: TextSize
  color?: TextColor
  opacity?: TextOpacity
  children?: string
}): JSX.Element {
  return (
    <div
      className={`${getSizeClass(size)} ${getColorClass(
        color
      )} ${getOpacityClass(opacity)}`}
    >
      {children}
    </div>
  )
}
