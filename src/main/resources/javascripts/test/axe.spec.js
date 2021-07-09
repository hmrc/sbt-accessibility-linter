const stream = require('stream');
const axe = require('../src/axe');

jest.mock('../src/runAxeCore');
const runAxeCore = require('../src/runAxeCore');

describe('axe', () => {
  const getTestDataStream = (content) => {
    const contentStream = new stream.Readable();
    contentStream.push(content);
    contentStream.push(null);
    return contentStream;
  };

  beforeEach(() => {
    jest.resetAllMocks();
    runAxeCore.mockImplementation(() => []);
  });

  it('should resolve if no violations are found by axe', async () => {
    const result = axe(getTestDataStream('some data'));

    await expect(result).resolves.toEqual('[]');
  });

  it('should pass through any violations', async () => {
    runAxeCore.mockImplementation(() => ['something bad']);

    const result = axe(getTestDataStream('some data'));

    await expect(result).resolves.toEqual('[\n\t"something bad"\n]');
  });

  it('should pass the provided content as a DOM element to Axe', async () => {
    await axe(getTestDataStream('<html><body><h1>Content</h1></body>'));

    const { calls } = runAxeCore.mock;
    expect(calls).toHaveLength(1);
    expect(calls[0][0].querySelector('h1').innerHTML).toEqual('Content');
  });
});
