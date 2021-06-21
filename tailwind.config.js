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
  },
  variants: {
    extend: {},
  },
  plugins: [],
}
