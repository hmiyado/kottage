const fs = require('fs')

const rootDirectory = 'src/components'

const path = process.argv.slice(2)[0]
if (!path) {
  console.error(`$ node ${process.argv[1]} ${rootDirectory}/example/target`)
  process.exit(1)
}
const root = path.split('/')[0]
if (root !== rootDirectory) {
  console.error(
    `expected: path should start "${rootDirectory}", ex: ${rootDirectory}/parent/child`
  )
  console.error(`actual: path = ${path}`)
  process.exit(1)
}
const componentName = path.split('/').slice(-1)
if (!componentName || componentName === '') {
  console.error(`component name is not valid`)
  console.error(`actual: component name = ${componentName}`)
  process.exit(1)
}

if (!fs.existsSync(path)) {
  fs.mkdirSync(path, { recursive: true })
}
const capitalizeComponentName =
  componentName[0].toUpperCase() + componentName.slice(1)
const templateDirectory = './scripts/component-template/template'
const exec = require('child_process').execSync
const suffixes = ['.tsx', '.stories.tsx', '.module.css']
for (const suffix of suffixes) {
  exec(
    `sed -e 's/template/${componentName}/' ${templateDirectory}/template${suffix} | sed -e 's/Template/${capitalizeComponentName}/' > ./${path}/${componentName}${suffix}`
  )
}
