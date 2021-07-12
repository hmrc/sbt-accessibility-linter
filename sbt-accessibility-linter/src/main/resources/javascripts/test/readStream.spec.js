const Stream = require('stream');
const readStream = require('../src/readStream');

describe('readStream', () => {
  it('should convert a stream to a string', async () => {
    const readableStream = new Stream.Readable();
    readableStream.push('some data');
    readableStream.push(null);

    const output = await readStream(readableStream);

    expect(output).toEqual('some data');
  });
});
