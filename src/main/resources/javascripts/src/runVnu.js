/* istanbul ignore file */
const { spawn } = require('child_process');
const vnuJar = require('vnu-jar');

function runVnu(inputStream) {
  const child = spawn(
    'java',
    [`-jar ${vnuJar}`, '--exit-zero-always', '--stdout', '--format json', '-'],
    { shell: true, stdio: [inputStream, 'pipe', 'inherit'] },
  );

  return child.stdout;
}

module.exports = runVnu;
