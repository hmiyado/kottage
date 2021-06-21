module.exports = {
  purge: ['./pages/**/*.{js,ts,jsx,tsx}', './components/**/*.{js,ts,jsx,tsx}'],
  darkMode: false, // or 'media' or 'class'
  theme: {
    extend: { screens: {} },
    colors: {
      white: '#FFFFFF',
      primary: {
        100: '#c8cee2',
        500: '#5177af',
        700: '#3a4b7c',
        900: '#05244f',
      },
    },
    spacing: {
      s: '0.5rem',
      m: '1.0rem',
      l: '1.5rem',
      ll: '2.0rem',
      xl: '3.0rem',
      x2l: '4.5rem',
      x6l: '9.0rem',
    },
    fontSize: {
      headline1: '6.0rem',
      headline3: '3.0rem',
      headline5: '1.5rem',
      body1: '1.0rem',
    },
  },
  variants: {
    extend: {
      margin: ['first'],
    },
  },
  plugins: [],
}
