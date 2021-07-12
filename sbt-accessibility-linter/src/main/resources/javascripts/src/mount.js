const jsdom = require('jsdom');

const { JSDOM } = jsdom;

function mount(content) {
  const dom = new JSDOM(content);
  const { window } = dom;
  const { document } = window;

  return document.documentElement;
}

module.exports = mount;
