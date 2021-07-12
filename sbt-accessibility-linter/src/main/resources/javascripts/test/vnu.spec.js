const stream = require('stream');

jest.mock('../src/runVnu');
const runVnu = require('../src/runVnu');
const vnu = require('../src/vnu');

describe('vnu', () => {
  const createStream = (content) => {
    const contentStream = new stream.Readable();
    contentStream.push(content);
    contentStream.push(null);
    return contentStream;
  };

  beforeEach(() => {
    jest.resetAllMocks();
    runVnu.mockImplementation(() => createStream('{ "messages": [] }'));
  });

  it('should resolve if no violations are found by axe', async () => {
    const result = vnu(createStream('some data'));

    await expect(result).resolves.toEqual('[]');
  });

  it('should pass through any violations', async () => {
    runVnu.mockImplementation(() => createStream('{ "messages": [{ "type": "error" }] }'));

    const result = vnu(createStream('some data'));

    await expect(result).resolves.toEqual('[\n\t{\n\t\t"type": "error"\n\t}\n]');
  });

  it('should pass the provided stream to VNU', async () => {
    const expectedStream = createStream('<html><body><h1>Content</h1></body>');

    await vnu(expectedStream);

    const { calls } = runVnu.mock;
    expect(calls).toHaveLength(1);
    expect(calls[0][0]).toBe(expectedStream);
  });
});
