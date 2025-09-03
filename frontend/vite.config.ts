// vite.config.ts
import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react-swc' // faster builds; swap to '@vitejs/plugin-react' if you prefer Babel
import tsconfigPaths from 'vite-tsconfig-paths'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), 'VITE_')
  const isProd = mode === 'production'

  return {
    // Speed + DX
    plugins: [
      react({
        jsxImportSource: 'react',
        // Enable these if you use styled-components or need React refresh fine-tuning:
        // plugins: [['@swc/plugin-styled-components', { displayName: !isProd }]],
        devTarget: 'es2020',
      }),
      tsconfigPaths(),
      // Tip: add type-checking with vite-plugin-checker for large projects
      // checker({ typescript: true }),
    ],

    // Only variables prefixed with VITE_ are exposed to the client
    envPrefix: 'VITE_',

    // Resolve (tsconfigPaths handles aliases from tsconfig.json "paths")
    resolve: {
      // If you don't want tsconfigPaths, uncomment and set aliases manually:
      // alias: { '@': fileURLToPath(new URL('./src', import.meta.url)) },
    },

    // Dev server (strict, predictable)
    server: {
      host: true,           // listen on LAN too
      port: 3000,
      strictPort: true,     // avoid random ports
      open: true,
      cors: true,
      hmr: { overlay: true },

      // API proxy (use VITE_API_URL in .env)
      proxy: {
        '/api': {
          target: env.VITE_API_URL || 'http://localhost:8080',
          changeOrigin: true,
          secure: false,
          // e.g. /api/users -> /users
          rewrite: (path) => path.replace(/^\/api/, ''),
        },
      },
    },

    // `vite preview` settings
    preview: {
      host: true,
      port: 5000,
    },

    // Build: optimized output + sensible defaults
    build: {
      target: 'es2020',
      outDir: 'dist',
      assetsDir: 'assets',
      sourcemap: isProd ? false : 'inline',
      cssCodeSplit: true,
      minify: isProd ? 'esbuild' : false,
      emptyOutDir: true,
      assetsInlineLimit: 4096, // 4kb data URLs

      rollupOptions: {
        output: {
          // Split heavy deps for better caching. Tweak as your stack evolves.
          manualChunks: {
            react: ['react', 'react-dom', 'react-router-dom'],
            mui: ['@mui/material', '@mui/icons-material'],
          },
        },
      },

      chunkSizeWarningLimit: 1000,
    },

    // Pre-bundle deps for faster dev
    optimizeDeps: {
      include: ['react', 'react-dom', 'react-router-dom'],
      esbuildOptions: { target: 'es2020' },
    },

    // Strip noisy things in production bundles
    esbuild: {
      drop: isProd ? ['console', 'debugger'] : [],
    },

    // Define handy globals you can import anywhere
    define: {
      __APP_VERSION__: JSON.stringify(process.env.npm_package_version),
      __BUILD_DATE__: JSON.stringify(new Date().toISOString()),
    },

    // CSS modules defaults (optional)
    css: {
      modules: {
        localsConvention: 'camelCaseOnly',
      },
    },

    // Vitest (lives here in Vite 4/5)
    test: {
      globals: true,
      environment: 'jsdom',
      setupFiles: './src/test/setup.ts', // create if you need RTL/JSDOM setup
      css: true,
      coverage: {
        provider: 'v8',
        reporter: ['text', 'html'],
        exclude: ['src/test/**', '**/*.d.ts'],
      },
    },
  }
})
