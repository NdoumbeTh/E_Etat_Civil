export const environment = {
  production: false,
  // ancienne valeur (fonctionnait avec docker-compose en local, casse sur Minikube/K8s
  // car le navigateur essaie de joindre localhost:8080 directement au lieu de passer
  // par le proxy nginx /api/ -> http://backend:8080/api) :
  // apiUrl: 'http://localhost:8080/api',

  // FIX K8S (28/09) : URL relative pour que les appels passent par le nginx du frontend,
  // qui proxy déjà /api/ vers le service backend en interne (voir nginx.conf)
  apiUrl: '/api',
};
