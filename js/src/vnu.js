/* eslint-disable no-console */
const runVnu = require('./vnu/vnu');

runVnu(process.stdin).then((data) => {
  console.log(data);
}).catch((error) => {
  console.error(error);
  process.exit(1);
});
