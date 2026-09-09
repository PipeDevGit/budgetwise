# Imagen de desarrollo: sirve con el dev server de Vite para tener recarga en
# caliente. El build de produccion vive en el CI; aca importa iterar rapido.
FROM node:20-alpine
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY . .

EXPOSE 5173
# --host expone el server fuera del contenedor; sin esto solo escucha en
# localhost y el puerto publicado no sirve de nada.
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]
