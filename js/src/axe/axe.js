const readStream = require('../common/readStream');
const mount = require('../common/mount');
const runAxeCore = require('./runAxeCore');

async function axe(inputStream) {
  const standardInput = await readStream(inputStream);

  const violations = await runAxeCore(mount(standardInput));

  return JSON.stringify(violations, undefined, '\t');
}

module.exports = axe;
