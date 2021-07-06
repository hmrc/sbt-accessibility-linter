const runAxe = require('./src/axe');

runAxe(process.stdin).then((data) => {
  console.log(data)
}).catch((error) => {
  console.error(error);
  process.exit(1);
});
