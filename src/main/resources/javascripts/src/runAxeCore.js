/* istanbul ignore file */
const axeCore = require('axe-core');
const axeConfig = require('./axeConfig');

async function runAxeCore(element) {
  const { violations } = await axeCore.run(element, axeConfig);
  return violations;
}

module.exports = runAxeCore;
