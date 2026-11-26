// Comprueba la cobertura italiana: textos L(es,en) sin traducir y claves sobrantes.
// También verifica que los marcadores ${...} se conserven en la traducción.
const fs = require('fs');
const path = require('path');

const root = path.resolve(__dirname, '..');
const renderer = path.join(root, 'renderer', 'js');
const dictPath = path.join(root, 'locales', 'it.json');
const dict = JSON.parse(fs.readFileSync(dictPath, 'utf8'));
const textos = new Set();
for (const f of fs.readdirSync(renderer).filter((x) => x.endsWith('.js'))) {
  const code = fs.readFileSync(path.join(renderer, f), 'utf8');
  const re = /L\(\s*(["'`])((?:\\.|(?!\1).)*)\1/gs;
  for (const m of code.matchAll(re)) textos.add(m[2].replace(/\r?\n/g, ' '));
}
const faltan = [...textos].filter((t) => !dict[t] && !/\$\{/.test(t) && t.length > 3);
const sobran = Object.keys(dict).filter((k) => !k.startsWith('_') && !textos.has(k));
const rotos = Object.entries(dict).filter(([k, v]) => {
  const a = (k.match(/\$\{[^}]*\}/g) || []).join('|');
  const b = (String(v).match(/\$\{[^}]*\}/g) || []).join('|');
  return a !== b;
});
console.log(`Textos detectados: ${textos.size}`);
console.log(`Traducciones italianas: ${Object.keys(dict).length}`);
console.log(`Sin traducir: ${faltan.length} · sobrantes: ${sobran.length} · marcadores rotos: ${rotos.length}`);
if (faltan.length) console.log('Ejemplos sin traducir:\n' + faltan.slice(0, 12).map((x) => ' · ' + x).join('\n'));
if (sobran.length) console.log('Ejemplos sobrantes:\n' + sobran.slice(0, 12).map((x) => ' · ' + x).join('\n'));
if (rotos.length) { rotos.slice(0, 8).forEach(([k, v]) => console.log(`Marcadores rotos: ${k} => ${v}`)); process.exit(1); }
