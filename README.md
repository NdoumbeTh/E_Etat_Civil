# e-ÉtatCivil — Starter kit (Phase 0)

Projet intégrateur M1 EPF Africa — Sujet 3 — Demandes en ligne d'actes d'état civil
Étudiant : Ndoumbe Thiombane · Encadrant : M. Baba TOP

Ce dépôt contient le starter kit Clean Architecture (back Spring Boot + front Angular)
prêt à démarrer. Le développement des modules commence en Séance 2/3.

## Checklist Phase 0 (à valider avant la Séance 1)

- [ ] Java 21 installé (`java -version`)
- [ ] Maven installé (`mvn -v`) — ou utiliser le wrapper `./mvnw` une fois généré
- [ ] Node.js 20+ et npm installés (`node -v`, `npm -v`)
- [ ] MySQL disponible (via Docker recommandé, voir plus bas)
- [ ] Ollama installé et un modèle léger téléchargé (`ollama pull llama3`)
- [ ] Dépôt Git créé et ce code poussé dessus
- [ ] Backend démarre sur `http://localhost:8080` et répond sur `/api/health`
- [ ] Frontend démarre sur `http://localhost:4200` et affiche l'écran de login

## 1. Créer le dépôt Git

```bash
cd e-etatcivil
git init
git add .
git commit -m "chore: starter kit Clean Architecture (Phase 0)"
git branch -M main
# Créer le dépôt vide sur GitHub/GitLab au préalable, puis :
git remote add origin <URL_DE_TON_DEPOT>
git push -u origin main
```

Ensuite, une branche par module (cf. cahier des charges, A.5) :
```bash
git checkout -b module-1-demandes
```

## 2. Lancer MySQL (Docker)

```bash
cd backend
docker compose up -d
```
Base `etatcivil`, utilisateur `etatcivil` / mot de passe `etatcivil` (voir `application.yml`).
Si tu n'as pas Docker, installe MySQL 8 en local et crée la base/l'utilisateur manuellement.

## 3. Installer et lancer Ollama (assistant, obligatoire pour ce sujet)

```bash
# macOS
brew install ollama
# Linux
curl -fsSL https://ollama.com/install.sh | sh
# Windows : installeur sur https://ollama.com/download

ollama serve            # démarre le serveur local (port 11434)
ollama pull llama3      # télécharge un modèle léger
ollama run llama3       # test rapide en ligne de commande
```
Le backend est déjà configuré (`spring.ai.ollama.base-url=http://localhost:11434`).
Ce module se prépare en autonomie mais ne sera **branché** qu'en Module 3 (Séance 5) —
vérifie seulement dès maintenant qu'Ollama tourne correctement.

## 4. Lancer le backend

```bash
cd backend
mvn spring-boot:run
```
Au premier démarrage, `DataSeeder` crée 4 comptes de démo (mot de passe `password123`) :
- citoyen@demo.sn
- officier@demo.sn
- chef@demo.sn
- admin@demo.sn

Vérifier : `curl http://localhost:8080/api/health` → `{"status":"UP", ...}`

## 5. Lancer le frontend

```bash
cd frontend
npm install
npm start   # ou: npx ng serve
```
Ouvrir `http://localhost:4200` → écran de login → se connecter avec un compte de démo
→ redirection vers `/dashboard` avec un contenu différent selon le rôle.
C'est exactement la Definition of Done de la Séance 2 (login → JWT → route protégée →
affichage selon rôle).

## Structure du projet

```
e-etatcivil/
├── backend/                        Spring Boot — Clean Architecture
│   └── src/main/java/com/epfafrica/etatcivil/
│       ├── domain/         entités métier (DemandeActe, TypeActe, ...) et enums
│       ├── application/    services et ports (à enrichir modules 1-3)
│       ├── infrastructure/ persistence JPA, sécurité JWT, config, IA (Ollama)
│       └── interfaces/     contrôleurs REST + DTO
├── frontend/                       Angular 20 standalone
│   └── src/app/
│       ├── core/            services, guards, intercepteurs, modèles
│       └── features/        login, dashboard (à enrichir modules 1-3)
└── README.md
```

## Prochaine étape (Séance 2)

Le starter kit couvre déjà login → JWT → route protégée → affichage selon rôle.
Reste à valider en séance : démonstration en direct devant l'enseignant, puis attaque
du Module 1 (dépôt de demande d'acte).
"# E_Etat_Civil" 
