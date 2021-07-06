function readStream(stream) {
  return new Promise((resolve) => {
    let content = '';
    stream.on('data', (chunk) => {
      content += chunk;
    });
    stream.on('end', () => resolve(content));
  });
}

module.exports = readStream;
