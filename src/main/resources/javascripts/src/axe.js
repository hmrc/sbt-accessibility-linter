const readStream = require('./readStream');
const mount = require('./mount');
const runAxeCore = require('./runAxeCore');

async function axe(inputStream) {
  const standardInput = await readStream(inputStream);

  const violations = await runAxeCore(mount(standardInput));

  return JSON.stringify(violations, undefined, '\t')
}

module.exports = axe;
