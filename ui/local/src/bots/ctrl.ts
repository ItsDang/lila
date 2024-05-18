import { type Zerofish } from 'zerofish';
import { Libot, Libots } from './interfaces';

export interface Ctrl {
  zf: Zerofish;
  bots: { [id: string]: Libot };
  bot: () => Libot;
  setBot(name: string): Promise<void>;
  move(fen: string): Promise<string>;
}

export async function makeCtrl(bots: Libots, zf: Zerofish): Promise<Ctrl> {
  const nets = new Map<string, Uint8Array>();
  let bot: Libot;
  return {
    zf,
    bots,
    bot: () => bot,
    async setBot(id: string) {
      bot = bots[id];
      if (bot.netName && zf.netName !== bot.netName) {
        if (!nets.has(bot.netName)) {
          nets.set(bot.netName, await fetchNet(bot.netName));
        }
        zf.setNet(id, nets.get(bot.netName)!);
        zf.netName = bot.netName;
      }
    },
    move(fen: string) {
      return bot.move(fen);
    },
  };
}

async function fetchNet(netName: string): Promise<Uint8Array> {
  return fetch(site.asset.url(`lifat/bots/weights/${netName}`, { noVersion: true }))
    .then(res => res.arrayBuffer())
    .then(buf => new Uint8Array(buf));
}
