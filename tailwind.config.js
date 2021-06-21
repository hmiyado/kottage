module.exports = {
  purge: ['./pages/**/*.{js,ts,jsx,tsx}', './components/**/*.{js,ts,jsx,tsx}'],
  darkMode: false, // or 'media' or 'class'
  theme: {
    extend: {},
    colors: {
      white: '#FFFFFF',
      primary: {
        100: '#c8cee2',
        700: '#3a4b7c',
        900: '#05244f',
      },
    },
    spacing: {
      l: '1.5rem',
      xl: '3.0rem',
      x2l: '4.5rem',
      x6l: '9.0rem',
    },
  },
  variants: {
    extend: {},
  },
  plugins: [],
}
