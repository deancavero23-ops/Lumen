// Comprueba que todos los JSON del proyecto sean válidos (se usa en CI).
const fs = require('fs');
const path = require('path');

function walk(dir) {
  const out = [];
  for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
    const p = path.join(dir, e.name);
    if (e.isDirectory()) out.push(...walk(p));
    else if (e.name.endsWith('.json')) out.push(p);
  }
  return out;
}

const root = path.resolve(__dirname, '..');
let errores = 0;
for (const file of walk(root)) {
  if (file.includes(`${path.sep}node_modules${path.sep}`) || file.includes(`${path.sep}dist${path.sep}`)) continue;
  try {
    const raw = fs.readFileSync(file, 'utf8');
    if (raw.charCodeAt(0) === 0xFEFF) throw new Error('tiene BOM (UTF-8 con marca)');
    JSON.parse(raw);
  } catch (e) {
    errores++;
    console.error(`✖ ${path.relative(root, file)}: ${e.message}`);
  }
}
console.log(errores ? `JSON: ${errores} errores` : 'JSON: todos válidos');
process.exit(errores ? 1 : 0);
