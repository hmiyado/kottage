declare module '*.svg' {
  //https://github.com/airbnb/babel-plugin-inline-react-svg/blob/master/src/transformSvg.js
  export type SVG = ({ class: string }) => JSX.Element
  const component: SVG
  export default component
}
