/* eslint-disable no-console */
const runVnu = require('./src/vnu');

runVnu(process.stdin).then((data) => {
  console.log(data)
}).catch((error) => {
  console.error(error);
  process.exit(1);
});
