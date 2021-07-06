const runVnu = require('./runVnu');
const readStream = require('./readStream');

const getVnuErrors = (output) => JSON.parse(output).messages.filter(({ type }) => type === 'error');

async function vnu(inputStream) {
  const outputStream = runVnu(inputStream);

  const vnuErrors = getVnuErrors(await readStream(outputStream));

  return JSON.stringify(vnuErrors, undefined, '\t')
}

module.exports = vnu;
