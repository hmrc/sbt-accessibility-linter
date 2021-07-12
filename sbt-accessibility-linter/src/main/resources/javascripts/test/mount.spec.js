const mount = require('../src/mount');

describe('mount', () => {
  it('should mount some content into the DOM', async () => {
    const element = mount('<html><body><h1>Stuff</h1></body>');

    expect(element.querySelector('h1')).toBeTruthy();
    expect(element.querySelector('h1').innerHTML).toEqual('Stuff');
  });
});
