declare interface Env {
  readonly NODE_ENV: string;
  readonly VITE_SERVER_URL: string;
  readonly SSR: boolean;
  [key: string]: any;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
